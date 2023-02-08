package ac.artemis.checks.regular.v2.checks.impl.fly;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setback;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.settings.CheckSetting;
import ac.artemis.core.v4.check.templates.position.SimplePositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.SimplePosition;
import ac.artemis.packet.minecraft.entity.impl.Player;

/**
 * @author Ghast
 * @since 21-Mar-20
 */
@Setback
@Check(type = Type.FLY, var = "Vanilla")
public class FlyVanilla extends SimplePositionCheck {

    private double previousDeltaY, previousMotionY, buffer;

    @Setting(type = CheckSettings.MAX_STREAK_FOR_VL, defaultValue = "5")
    private final CheckSetting maxStreak = info.getSetting(CheckSettings.MAX_STREAK_FOR_VL);

    @Setting(type = CheckSettings.MIN_DELTA, defaultValue = "0")
    private final CheckSetting minDelta = info.getSetting(CheckSettings.MIN_DELTA);

    public FlyVanilla(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(SimplePosition from, SimplePosition to) {
        final Player player = data.getPlayer();

        // Ensure that the player is not actually in an environment which is prone to falses
        final boolean invalid = player.isAllowedFlight()
                || player.isFlying()
                || data.movement.isInVehiclePacket()
                || data.user.isOnGround()
                || data.user.isOnFakeGround()
                || data.collision.isCollidesBoat()
                || data.movement.isInLiquid()
                || data.movement.isInWeb()
                || data.movement.isOnLadder()
                || data.movement.isOnSlab()
                || data.movement.isOnStair()
                || data.movement.isOnSoulSand()
                || data.movement.isUnderBlock()
                || data.movement.isOnIce()
                || data.movement.isInTrapdoor()
                || data.movement.isSlimeVelocity();

        final boolean exempt = this.isExempt(
                ExemptType.FLIGHT,
                ExemptType.VOID,
                ExemptType.SNOW,
                ExemptType.LIQUID,
                ExemptType.COLLIDED_HORIZONTALLY,
                ExemptType.RESPAWN,
                ExemptType.JOIN,
                ExemptType.TELEPORT
        );

        if (invalid || exempt) {
            debug("Invalid conditions");
            return;
        }

        // Delta Y = to - from.
        final double deltaY = (to.getY() - from.getY());

        // Relative delta
        final double motionY = (previousDeltaY - deltaY) / deltaY;

        // If the previous deltaY is equal to the previous, which is in theory impossible without fly permissions
        final boolean flag = previousMotionY == motionY && Math.abs(deltaY) > minDelta.getAsDouble();

        process: {
            if (!flag) {
                // Decrease the buffer by 1
                this.buffer = buffer > 0 ? buffer - 1 : 0;
                break process;
            }

            if (buffer++ <= maxStreak.getAsInt()) {
                break process;
            }

            // Flag with of course a buffer as such is a heuristic.
            this.log("motY=" + motionY + " dY=" + deltaY + " buf=" + buffer);
        }

        // Output some debug for... debugging purposes
        this.debug("motionY=" + motionY);

        // Set the values
        this.previousDeltaY = deltaY;
        this.previousMotionY = motionY;
    }
}
