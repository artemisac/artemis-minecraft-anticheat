package ac.artemis.checks.regular.v2.checks.impl.fly;

import ac.artemis.packet.minecraft.GameMode;
import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.annotations.NMS;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.SimplePositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.utils.position.SimplePosition;
import ac.artemis.core.v5.utils.MathUtil;
import ac.artemis.core.v5.utils.bounding.Vec3d;

@Check(type = Type.FLY, var = "InvalidVelocity")
@NMS
@Experimental
public class FlyInvalid extends SimplePositionCheck {
    private int buffer = 0;

    public FlyInvalid(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(final SimplePosition from, final SimplePosition to) {

        // Stairs cause an invalid motion as it accelerates on the Y axis, making this false [Ghast, view MoveEntity in NMS]
        if (isExempt(ExemptType.STEPABLE)) return;

        // Get the data from the data and the motion vector
        final Player player = data.getPlayer();
        final Vec3d motion = NMSManager.getInms().getMotion(player);

        // Get the deltaY and the motionY from NMS and from the movements
        final double deltaX = to.getX() - from.getX();
        final double deltaY = to.getY() - from.getY();
        final double deltaZ = to.getZ() - from.getZ();

        // Get the motion values for the vector
        final double motionX = motion.getX();
        final double motionY = motion.getY();
        final double motionZ = motion.getZ();

        // Make sure the data isn't affected by velocity or by any illegal modifications of the server
        final boolean potion = player.hasEffect(PotionEffectType.JUMP);
        final boolean invalid = data.getMovement().isOnGround()
                || data.getUser().isOnGround()
                || data.getMovement().isStrider()
                || data.getMovement().isInvalidNMSMotion()
                || data.getPrediction().getQueuedVelocities().size() > 0
                || player.getGameMode() == GameMode.CREATIVE
                || player.isAllowedFlight()
                || player.isInsideVehicle();

        velocity: {
            if (motionY > 0.0 || invalid || potion) break velocity;

            if (deltaY > 0.42) log("m=" + motionY + " d=" + deltaY);
        }

        motion: {
            if (invalid) break motion;

            if (deltaX > motionX && deltaZ > motionZ) {
                // Get the horizontal distance and the horizontal motion of the data
                final double horizontalDistance = MathUtil.hypot(deltaX, deltaZ);
                final double horizontalMotion = motionX + motionZ;

                // Its impossible for the data to make a large air movement without having some acceleration from the initial jump.
                final boolean motionInvalid = (deltaX > 0.089 || deltaZ > 0.089)
                        && horizontalDistance > 0.21 && horizontalMotion == 0.0;

                if (motionInvalid) {
                    if (++buffer > 7) {
                        this.log("readNBTNMS=" + horizontalDistance + " m=" + horizontalMotion);
                    }
                } else {
                    buffer = 0;
                }
            }

            debug("m=" + motionY + " d=" + deltaY);
        }
    }
}
