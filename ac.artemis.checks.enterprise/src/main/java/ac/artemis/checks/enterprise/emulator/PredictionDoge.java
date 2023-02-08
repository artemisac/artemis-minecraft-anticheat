package ac.artemis.checks.enterprise.emulator;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PredictionHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PredictionPosition;

import java.util.Arrays;

import static ac.artemis.packet.protocol.ProtocolVersion.*;

/**
 * @author Ghast
 * @since 18/10/2020
 * Artemis © 2020
 *
 * This check is quite peculiar as the buffer is insanely low, though the threshold is indeed
 * quite high and has a wide array of requirements. The objective of this check is to follow
 * a more traditional flag method by incrementing the predicted offset to the buffer if a
 * threshold is met, or decrease it by the predicted offset + 0.05. This effectively flags
 * every fly and long-jump without causing a single false positive. Pretty fancy schmancy.
 */
@Check(type = Type.PREDICTION, var = "Doge", threshold = 25)
@ClientVersion(version = {V1_7, V1_7_10, V1_8, V1_8_5, V1_8_9, V1_9, V1_9_1, V1_9_2, V1_9_4, V1_10, V1_10_2, V1_11, V1_12, V1_12_1, V1_12_2})
public class PredictionDoge extends ArtemisCheck implements PredictionHandler {
    public PredictionDoge(PlayerData data, CheckInformation info) {
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
                ExemptType.COLLIDED_HORIZONTALLY,
                ExemptType.RESPAWN
        );

        final double distance = prediction.got().distanceSquareXZ(prediction.expected());
        final boolean invalid = distance > 0.05D && prediction.isPredictSmallerXZ();

        if (exempt) {
            debug("Exempt: " + Arrays.toString(exemptTypes()));
            return;
        }

        flag: {
            this.buffer = invalid ? Math.min(buffer + distance, Byte.MAX_VALUE) : Math.min(buffer - distance - 0.05D, Byte.MAX_VALUE);
            this.buffer = Math.max(buffer, 0);

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
