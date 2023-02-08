package ac.artemis.checks.regular.v2.checks.impl.fly;

import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setback;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.SimplePositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.SimplePosition;

@Check(type = Type.FLY, var = "DeltaSimple")
@Setback
public class FlyDeltaSimple extends SimplePositionCheck {

    private double buffer;
    private double lastDeltaY;

    public FlyDeltaSimple(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(SimplePosition from, SimplePosition to) {
        // Go away environment false flags
        final boolean invalid = this.isExempt(
                ExemptType.FLIGHT,
                ExemptType.VOID,
                ExemptType.VEHICLE,
                ExemptType.LIQUID,
                ExemptType.SLIME,
                ExemptType.GROUND,
                ExemptType.MOVEMENT,
                ExemptType.TELEPORT
        );

        if (invalid) {
            this.debug("Invalid environment");
            return;
        }

        // Go away velocity false flags
        if (data.movement.getHighestVerticalVelocity() > 0.0) {
            return;
        }

        // Go away teleport false flags
        if (data.movement.getTeleportTicks() < 4) {
            return;
        }

        final double deltaY = to.getY() - from.getY();

        // Basic minecraft method
        final double estimation = (lastDeltaY - 0.08D) * 0.9800000190734863D;

        // We don't want to check if the impl is not moving at all, or barely in attempt to false this
        if (Math.abs(deltaY + 0.0980000019) < 0.005) {
            buffer = 0.0D;
            return;
        }

        // Check if they're accelerating more quickly than possible
        if (Math.abs(estimation - deltaY) > 0.002) {
            if ((buffer += 1.5) > 5) {
                log();
            } else {
                buffer = Math.max(0, buffer - 1.25);
            }
        } else {
            buffer = Math.max(0, buffer - 10D);
        }

        this.lastDeltaY = deltaY;
    }
}
