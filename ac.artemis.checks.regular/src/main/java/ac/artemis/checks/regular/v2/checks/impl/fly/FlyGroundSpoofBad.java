package ac.artemis.checks.regular.v2.checks.impl.fly;

import ac.artemis.core.v4.check.TeleportHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setback;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.SimplePositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.ModifiableFlyingLocation;
import ac.artemis.core.v4.utils.position.SimplePosition;

/**
 * @author Elevated
 * @since 18-Apr-20
 */
@Setback
@Check(type = Type.FLY, var = "BadPacket")
public class FlyGroundSpoofBad extends SimplePositionCheck implements TeleportHandler {
    public FlyGroundSpoofBad(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private int buffer;

    @Override
    public void handlePosition(SimplePosition from, SimplePosition to) {
        final boolean exempt = this.isExempt(
                ExemptType.FLIGHT,
                ExemptType.VEHICLE,
                ExemptType.VOID,
                ExemptType.JOIN,
                ExemptType.SLIME,
                ExemptType.WORLD,
                ExemptType.GAMEMODE,
                ExemptType.MOVEMENT,
                ExemptType.COLLIDE_ENTITY,
                ExemptType.LIQUID,
                ExemptType.LIQUID_WALK,
                ExemptType.RESPAWN,
                ExemptType.LADDER,
                ExemptType.WEB,
                ExemptType.UNDERBLOCK
        );

        if (exempt) {
            this.debug("Invalid conditions");
            return;
        }

        final boolean onGroundServer = data.user.isOnFakeGround();
        final boolean onGroundClient = to.getY() % 0.015625 == 0.0;

        if (onGroundServer != onGroundClient) {
            if (++buffer > 5) {
                this.log(
                        new Debug<>("buffer", buffer),
                        new Debug<>("delta", (to.getY() % 0.015625))
                );
            }
        } else {
            buffer = 0;
        }
    }

    @Override
    public void handle(ModifiableFlyingLocation confirmedLocation) {
        this.buffer = 0;
    }
}
