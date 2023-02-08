package ac.artemis.checks.enterprise.emulator;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PredictionHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.lists.EvictingArrayList;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.PredictionPosition;

import java.util.Arrays;
import java.util.List;

import static ac.artemis.packet.protocol.ProtocolVersion.*;

/**
 * @author Ghast
 * @since 18/10/2020
 * Artemis © 2020
 *
 * By far the most unstable yet hardest check to bypass. Over time, it's definitely grown
 * in stability, though it's detections are simply just 'bizarre'. This check is essentially
 * an exponential buffer check for prediction offsets. If all the sum in the past 10 distances
 * are above a certain threshold, the buffer grows by [distance * 100, 500]. This effectively
 * prevents a majority of all the outliers and makes cheating much harder on a significant
 * scale. The more a player strays from the predicted outcome, the more likely it will trigger
 * the check.
 */
@Check(type = Type.PREDICTION, var = "JoJo", threshold = 25)
public class PredictionVolatile extends ArtemisCheck implements PredictionHandler {
    public PredictionVolatile(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private final List<Double> distances = new EvictingArrayList<>(10);
    private double buffer;
    private int fails;
    private boolean wasFlight;

    @Override
    public void handle(final PredictionPosition prediction) {
        final boolean empty = this.isNull(CheckType.POSITION, CheckType.MOVEMENT, CheckType.ROTATION);

        if (empty) return;

        String colorX = data.prediction.getDistanceX() > 0.005D
                ? "&c"
                : data.prediction.getDistanceX() > 1E-6
                    ? "&a"
                    : "&2";

        String colorY = data.prediction.getDistanceY() > 1E-5
                ? "&c"
                : data.prediction.getDistanceY() > 1E-8
                ? "&a"
                : "&2";

        String colorZ = data.prediction.getDistanceZ() > 0.005D
                ? "&c"
                : data.prediction.getDistanceZ() > 1E-6
                ? "&a"
                : "&2";

        String colorPlusX = prediction.isPredictSmallerX()
                ? "&7[&4&l-&7] (&a" + MathUtil.roundToPlace(prediction.wasGotX(), 8)
                    + "&7) (&c" + MathUtil.roundToPlace(prediction.wasExpectX(), 8) + "&8)"
                : "&7[&a&l+&7]";

        String colorPlusY = prediction.isPredictSmallerY()
                ? "&7[&4&l-&7] (&a" + MathUtil.roundToPlace(prediction.wasGotY(), 8)
                    + "&7) (&c" + MathUtil.roundToPlace(prediction.wasExpectY(), 8) + "&8)"
                : "&7[&a&L+&7]";

        String colorPlusZ = prediction.isPredictSmallerZ()
                ? "&7[&4&l-&7] (&a" + MathUtil.roundToPlace(prediction.wasGotZ(), 8)
                    + "&7) (&c" + MathUtil.roundToPlace(prediction.wasExpectZ(), 8) + "&8)"
                : "&7[&a&l+&7]";

        final String motion = String.format(
                "iter=%d motion: f=%.4f x=%.4f y=%.4f z=%.4f mX=%.2f mY=%.2f speed=%.4f \n&7x:%s %.8f %s \n&7y:%s %.8f %s \n&7z:%s %.8f %s",
                data.prediction.getIteration(),
                data.prediction.getFriction(),
                data.entity.getMotionX(),
                data.entity.getMotionY(),
                data.entity.getMotionZ(),
                data.entity.getMoveForward(),
                data.entity.getMoveStrafing(),
                data.entity.getAIMoveSpeed(),
                colorX,
                data.prediction.getDistanceX(),
                colorPlusX,
                colorY,
                data.prediction.getDistanceY(),
                colorPlusY,
                colorZ,
                data.prediction.getDistanceZ(),
                colorPlusZ
        );

        this.debug("[Artemis | Motion] " + motion);

        if (wasFlight) {
            if (data.entity.isOnGround()) wasFlight = false;
            return;
        }

        if (this.isExempt(ExemptType.FLIGHT)) {
            this.wasFlight = true;
            return;
        }

        if (this.isExempt(ExemptType.VEHICLE, ExemptType.WORLD, ExemptType.JOIN)) {
            this.buffer = 0;
            return;
        }

        final boolean exempt = this.isExempt(
                ExemptType.FLIGHT,
                ExemptType.VEHICLE,
                ExemptType.VOID,
                ExemptType.JOIN,
                ExemptType.WORLD,
                ExemptType.GAMEMODE,
                ExemptType.MOVEMENT,
                ExemptType.COLLIDE_ENTITY,
                ExemptType.LIQUID,
                ExemptType.FLIGHT,
                ExemptType.LIQUID_WALK,
                ExemptType.SLIME,
                ExemptType.LADDER,
                ExemptType.WEB,
                ExemptType.COLLIDED_HORIZONTALLY
        );

        final double distance = prediction.differenceSquared();
        final boolean invalid = distance > 0.005D && prediction.isPredictSmaller();

        if (distance > 16) {
            return;
        }

        String color = distance > 0.005D ? "&c" : distance > 1E-6 ? "&a" : "&2";

        this.debug(String.format("%sdistance=%s%.7f %sinvalid=%s", "&7", color,
                distance, "&7", (invalid ? "&6&lFLAG"
                        : "&r&lLegit")));
        this.debug("    &7->&r " + data.entity.readTags());

        if (exempt) {
            //debug("Exempt: " + Arrays.toString(exemptTypes()));
            return;
        }

        this.distances.add(distance);

        if (!invalid) {
            fails++;
        }

        final double mean = MathUtil.getSum(distances);

        final boolean flag = mean > 0.5 && invalid && data.getVersion().isOrBelow(V1_12_2);

        this.buffer = flag
                ? Math.min(Math.min(buffer + (fails = 1) * (distance * 100), buffer + 500), 25000)
                : Math.max(buffer - (fails+=fails), 0);

        if (buffer > 20000 && flag)  {
            this.buffer /= 1.5;
            this.log(
                    new Debug<>("distance", distance),
                    new Debug<>("buffer", buffer),
                    new Debug<>("tags", data.entity.readTags())
            );
        }
    }
}
