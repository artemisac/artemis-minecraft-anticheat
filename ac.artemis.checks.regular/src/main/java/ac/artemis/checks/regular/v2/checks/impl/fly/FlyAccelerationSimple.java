package ac.artemis.checks.regular.v2.checks.impl.fly;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.TeleportHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setback;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.SimplePositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.ModifiableFlyingLocation;
import ac.artemis.core.v4.utils.position.SimplePosition;

@Setback
@Check(type = Type.FLY, var = "AccelerationSimple")
public class FlyAccelerationSimple extends SimplePositionCheck implements TeleportHandler {
    private double lastDeltaY, lastAcceleration;
    private int hoverBuffer, accelerationBuffer, airTicks;

    public FlyAccelerationSimple(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(SimplePosition from, SimplePosition to) {
        final boolean invalid = this.isExempt(
                ExemptType.GAMEMODE,
                ExemptType.MOVEMENT,
                ExemptType.FLIGHT,
                ExemptType.GROUND,
                ExemptType.LIQUID,
                ExemptType.PISTON,
                ExemptType.LIQUID_WALK,
                ExemptType.JOIN,
                ExemptType.STRIDER,
                ExemptType.RESPAWN,
                ExemptType.TPS,
                ExemptType.VEHICLE,
                ExemptType.SLIME,
                ExemptType.SNOW,
                ExemptType.UNDERBLOCK,
                ExemptType.LADDER,
                ExemptType.STEPABLE,
                ExemptType.WEB
        ) || data.prediction.getVelocityTicks() < 2;

        if (invalid) {
            return;
        }

        final double deltaY = (to.getY() - from.getY()) - data.entity.getMotion().getVertical();

        final double acceleration = Math.abs(deltaY - lastDeltaY);
        final double deltaAcceleration = acceleration - lastAcceleration;

        final boolean onGround = data.user.isOnFakeGround();

        if (!onGround) {
            // Hover
            ++airTicks;

            if (airTicks > 5 && deltaY == lastDeltaY) {
                if (++hoverBuffer > 3) {
                    log();
                }
            } else {
                hoverBuffer = 0;
            }

            if (airTicks > 5 && deltaAcceleration == 0.0 || deltaAcceleration > 0.018) {
                if (++accelerationBuffer > 4) {
                    log();
                }
            } else {
                accelerationBuffer = 0;
            }
        } else {
            airTicks = 0;
        }

        this.debug(String.format("airTicks=%d accel=%.4f hover=%d axel=%d", airTicks, deltaAcceleration, hoverBuffer, accelerationBuffer));
        this.lastDeltaY = deltaY;
        this.lastAcceleration = acceleration;
    }

    @Override
    public void handle(ModifiableFlyingLocation confirmedLocation) {
        this.airTicks = 0;
    }
}
