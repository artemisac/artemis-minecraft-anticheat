package ac.artemis.checks.regular.v2.checks.impl.badpackets;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.VelocityHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;

/**
 * @author Elevated
 * @since 18-Apr-20
 */

@Check(type = Type.BADPACKETS, var = "K", threshold = 1)
@ClientVersion()
public class BadPacketsK extends ArtemisCheck implements PacketHandler, VelocityHandler {
    private int vl;

    public BadPacketsK(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            if (isNull(CheckType.ROTATION) || data.getPlayer().isInsideVehicle()) return;

            if (!TimeUtil.elapsed(data.movement.getLastMoveCancel(), 1000L)) {
                debug("Recent move cancel");
                return;
            }

            final PacketPlayClientFlying wrapper = (PacketPlayClientFlying) packet;

            if (wrapper.isLook() && !data.user.isLagging() && !data.user.isOnCooldown()) {
                final double deltaYaw = MathUtil.distanceBetweenAngles(data.movement.lastRotation.getYaw(),
                        data.movement.rotation.getYaw());
                final double deltaPitch = MathUtil.distanceBetweenAngles(data.movement.lastRotation.getPitch(),
                        data.movement.rotation.getPitch());

                if (deltaYaw == 0 && deltaPitch == 0) {
                    if (vl++ > 7) {
                        log("vl=" + vl);
                    }
                } else {
                    vl = 0;
                }
            }
        }
    }

    @Override
    public void handle(final Velocity velocity) {
        this.vl = 0;
    }
}
