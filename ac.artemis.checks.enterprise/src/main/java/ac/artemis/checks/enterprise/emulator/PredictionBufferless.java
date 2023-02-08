package ac.artemis.checks.enterprise.emulator;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PredictionHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PredictionPosition;
import ac.artemis.core.v5.utils.raytrace.Point;

import java.util.Arrays;

/**
 * @author Ghast
 * @since 18/10/2020
 * Artemis © 2020
 *
 * This is an experimental check which does not heavily rely on a buffer. Whilst this is exponentially
 * more unreliable than the other checks available, it does serve as a brilliant to base
 */
@Check(type = Type.PREDICTION, var = "Lacrimosa", threshold = 25)
//@ClientVersion(version = {V1_7, V1_7_10, V1_8, V1_8_5, V1_8_9, V1_9, V1_9_1, V1_9_2, V1_9_4, V1_10, V1_10_2, V1_11, V1_12, V1_12_1, V1_12_2})
@Experimental
public class PredictionBufferless extends ArtemisCheck implements PredictionHandler {
    public PredictionBufferless(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private double buffer;
    private boolean wasFlight;

    @Override
    public void handle(PredictionPosition prediction) {
        final boolean empty = this.isNull(CheckType.POSITION, CheckType.MOVEMENT, CheckType.ROTATION);

        if (empty) return;

        final String motion = String.format(
                " motion: x=%.4f y=%.4f z=%.4f yaw=%.4f jumping=%s sprinting=%s sneaking=%s moving=%s",
                data.entity.getMotionX(),
                data.entity.getMotionY(),
                data.entity.getMotionZ(),
                data.entity.getRotationYaw(),
                data.entity.isJumping(),
                data.entity.isSprinting(),
                data.entity.isSneaking(),
                data.movement.isMoving()
        );

        //this.debug("[Artemis | Motion] " + motion);

        if (wasFlight) {
            if (data.entity.isOnGround()) wasFlight = false;
            return;
        }

        if (this.isExempt(ExemptType.FLIGHT)) {
            this.wasFlight = true;
            return;
        }

        if (this.isExempt(ExemptType.VEHICLE, ExemptType.WORLD, ExemptType.JOIN, ExemptType.LADDER)) {
            this.buffer = -10;
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

        final Point maxMotion = new Point(
                data.entity.getMaxMotionX(),
                data.entity.getMaxMotionY(),
                data.entity.getMaxMotionZ()
        );

        final Point delta = new Point(
                prediction.got().getX() - prediction.was().getX(),
                prediction.got().getY() - prediction.was().getY(),
                prediction.got().getZ() - prediction.was().getZ()
        );

        final double distance = prediction.got().distanceSquareXZ(prediction.expected());
        final boolean invalid = maxMotion.lengthSquared() < delta.lengthSquared()
                && maxMotion.squareDistanceTo(delta) > 1E-3
                && prediction.differenceSquared() > 1E-4;

        if (exempt) {
            debug("Exempt: " + Arrays.toString(exemptTypes()));
            return;
        }

        flag: {
            this.buffer++;

            if (buffer < 1 || !invalid) {
                break flag;
            }

            this.log(
                    new Debug<>("buffer", buffer),
                    new Debug<>("distance", distance),
                    new Debug<>("exempt", Arrays.toString(exemptTypes()))
            );
        }


        this.debug(String.format("distance=%.5f buffer=%.4f", distance, buffer));
    }
}
