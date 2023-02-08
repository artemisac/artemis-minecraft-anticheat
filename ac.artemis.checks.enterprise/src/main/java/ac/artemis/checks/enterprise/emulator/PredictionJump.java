package ac.artemis.checks.enterprise.emulator;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PredictionHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.annotations.Drop;
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
 *
 * This check is a **pain in the ass**. To be more accurate, it's essentially a ground-spoof
 * check on steroids. We check if a jump was possible and flag on such basis. If the jump was
 * impossible, we can flag it. This prevents any ghost jump fly and lazily patches the issue
 * with ground status being quite often wrong (odd bug!)
 */
@Check(type = Type.PREDICTION, var = "Jump")
@ClientVersion(version = {V1_7, V1_7_10, V1_8, V1_8_5, V1_8_9, V1_9, V1_9_1, V1_9_2, V1_9_4, V1_10, V1_10_2, V1_11, V1_12, V1_12_1, V1_12_2})
@Setback
@Drop()
public final class PredictionJump extends ArtemisCheck implements PredictionHandler {
    public PredictionJump(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private double buffer;
    private boolean wasFlight;

    @Override
    public void handle(final PredictionPosition prediction) {
        final boolean empty = this.isNull(CheckType.POSITION, CheckType.MOVEMENT, CheckType.ROTATION);

        if (empty) return;

        /*
         * When we turn off fly due to predictions relying on the last estimations and our
         * predictions not really working well with flying we are going to exempt till we
         * are on ground as our motions reset there and we can start after this happens.
         */
        if (wasFlight) {
            if (data.entity.isOnGround()) wasFlight = false;
            return;
        }

        /*
         * The same stuff explained above applies here as well this is where we just set
         * if the player is flying. Simple stuff
         */
        if (this.isExempt(ExemptType.FLIGHT)) {
            this.wasFlight = true;
            return;
        }

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

        if (this.isExempt(ExemptType.VEHICLE, ExemptType.FLIGHT, ExemptType.WORLD, ExemptType.JOIN)) {
            this.buffer = 0;
            return;
        }

        this.debug("[Artemis | Motion] " + motion);

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
                ExemptType.WEB,
                ExemptType.RESPAWN
        );

        if (exempt) {
            this.debug("exempt");
            return;
        }

        final int jumpTicks = data.entity.getJumpTicks();
        final boolean jumping = data.entity.isJumping();

        flag: {
            if (!jumping) break flag;

            final boolean onGround = data.collision.isWasGroundCollide() || data.collision.isGroundCollide();
            final boolean canJump = jumpTicks == 0;

            if (!onGround) {
                this.log(
                        new Debug<>("jumpTicks", jumpTicks),
                        new Debug<>("buffer", buffer),
                        new Debug<>("exempt", exempt)
                );
            }

            this.debug("[Flag]");
        }
        this.debug("jumpTicks=%d buffer=%f exempt=%s", jumpTicks, buffer, exempt);
    }
}
