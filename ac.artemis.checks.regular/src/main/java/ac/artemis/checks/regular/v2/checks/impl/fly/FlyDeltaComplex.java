package ac.artemis.checks.regular.v2.checks.impl.fly;

import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.ComplexPositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.packet.minecraft.PotionEffectType;

@Check(type = Type.FLY, var = "DeltaComplex")
@Experimental
public class FlyDeltaComplex extends ComplexPositionCheck {
    private double lastGroundY = 0.0, buffer;

    public FlyDeltaComplex(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(final PlayerPosition from, final PlayerPosition to) {
        // The usual deltaY
        final double deltaY = to.getY() - from.getY();

        // If the user is in a vehicle/void/has a velocity, we should exempt it.
        final boolean invalid = deltaY > 0.0 || this.isExempt(
                ExemptType.VELOCITY,
                ExemptType.VEHICLE,
                ExemptType.VOID,
                ExemptType.MOVEMENT,
                ExemptType.GAMEMODE
        );

        // Ensure the player is not touching air
        final boolean touchingAir = data.getUser().isOnFakeGround();

        // This might cause issues on 1.7.
        // Todo: Make a potion wrapper
        final int jumpModifier = MathUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP);

        // Threshold based on almost magic values
        final double threshold = jumpModifier > 0 ? 1.5 + (Math.pow(jumpModifier + 4.2, 2.0) / 16.0) : 1.5;

        if (touchingAir && invalid) {
            final double distanceGround = to.getX() - lastGroundY;

            if (distanceGround > threshold && lastGroundY != 0.0) {
                if (++buffer > 3) {
                    this.log("d=" + distanceGround);
                }
            } else {
                buffer = 0.0;
            }
        } else {
            this.lastGroundY = to.getY();
        }
    }
}
