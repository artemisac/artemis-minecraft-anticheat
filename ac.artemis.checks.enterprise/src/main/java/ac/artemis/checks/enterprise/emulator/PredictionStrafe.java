package ac.artemis.checks.enterprise.emulator;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PredictionHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PredictionPosition;

import static ac.artemis.packet.protocol.ProtocolVersion.*;

/**
 * @author Ghast
 * @since 18/10/2020
 * Artemis © 2020
 *
 * This check significantly lacks in aggressivity. This is supposed to flag any non-movement corrected
 * aura, scaffold and so and forth. If the user is doing weird sprint movement, we can flag it.
 * It would be nice to make this more useful.
 */
@Check(type = Type.PREDICTION, var = "Strafe")
@ClientVersion(version = {V1_7, V1_7_10, V1_8, V1_8_5, V1_8_9, V1_9, V1_9_1, V1_9_2, V1_9_4, V1_10, V1_10_2, V1_11, V1_12, V1_12_1, V1_12_2})
public class PredictionStrafe extends ArtemisCheck implements PredictionHandler {
    public PredictionStrafe(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private double buffer;

    @Override
    public void handle(final PredictionPosition prediction) {
        // Null check
        final boolean nu11 = this.isNull(CheckType.POSITION, CheckType.MOVEMENT, CheckType.ROTATION);

        if (nu11) return;
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

        // Exempt check
        final boolean exempt = this.isExempt(
                ExemptType.FLIGHT,
                ExemptType.VEHICLE,
                ExemptType.VOID,
                ExemptType.TELEPORT,
                ExemptType.JOIN,
                ExemptType.GAMEMODE,
                ExemptType.MOVEMENT,
                ExemptType.COLLIDE_ENTITY,
                ExemptType.LIQUID,
                ExemptType.PLACING
        );

        // Collect the values
        final float moveStrafing = data.entity.getMoveStrafing();
        final float moveForward = data.entity.getMoveForward();

        // Make sure user is on ground and is sprinting, otherwise this is redundant
        final boolean env = data.user.isOnFakeGround() && data.entity.getPlayerControls().isSprint();
        // Flag if user is sprinting yet moving on strafe
        final boolean flag = (moveForward < 0) || (moveForward == 0 && moveStrafing != 0);

        flag: {
            // Prevent exemptions
            if (exempt) break flag;

            // If the environment is not adequate/invalid flag, reset the buffer
            if (!env || !flag) {
                this.buffer = 0;

                break flag;
            }

            if (buffer++ <= 2) break flag;

            this.log(
                    new Debug<>("forward", moveForward),
                    new Debug<>("strafe", moveStrafing)
            );
        }

        this.debug("moveForward=%a moveStrafing=%a buffer=%a exempt=%s", moveForward, moveStrafing, buffer, exempt);
    }
}
