package ac.artemis.checks.regular.v2.checks.impl.emulator;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PredictionHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.annotations.Setback;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PredictionPosition;

import static ac.artemis.packet.protocol.ProtocolVersion.*;

/**
 * @author Ghast
 * @since 18/10/2020
 * Artemis © 2020
 */

@Check(type = Type.PREDICTION, var = "Speed", threshold = 20)
@ClientVersion(version = {V1_7, V1_7_10, V1_8, V1_8_5, V1_8_9, V1_9, V1_9_1, V1_9_2, V1_9_4, V1_10, V1_10_2, V1_11, V1_12, V1_12_1, V1_12_2})
@Setback
public class PredictionSpeed extends ArtemisCheck implements PredictionHandler {
    public PredictionSpeed(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private double buffer;
    private int fails;
    private boolean wasFlight;

    @Override
    public void handle(final PredictionPosition prediction) {
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
                ExemptType.RESPAWN,
                ExemptType.LADDER,
                ExemptType.WEB,
                ExemptType.SLIME
        );

        final double distance = prediction.got().distance(prediction.expected());


        if (distance > 16) {
            return;
        }

        final boolean flag = distance > 0.01D && !exempt;

        //final boolean invalid = got.distanceXZ(expected) < 1E-4D && got.distanceY(expected) > 0.25D;
        this.buffer = flag ? buffer + (fails = 1) * Math.max(distance * 100.D, 2) : Math.max(buffer - fails++, 0);

        if (buffer > 1500 && flag)  {
            this.log(
                    new Debug<>("distance", distance),
                    new Debug<>("buffer", buffer),
                    new Debug<>("fails", fails),
                    new Debug<>("tags", data.entity.readTags())
            );
        }

        this.debug("distance=" + distance + " buffer=" + buffer + " exempt=" + exempt + " tags=" + data.entity.readTags());
    }
}
