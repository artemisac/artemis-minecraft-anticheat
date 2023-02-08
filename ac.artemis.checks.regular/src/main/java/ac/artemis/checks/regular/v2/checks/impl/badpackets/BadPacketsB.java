package ac.artemis.checks.regular.v2.checks.impl.badpackets;


import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;

/**
 * @author 7x6
 * @since 30/08/2019
 */

@Check(type = Type.BADPACKETS, var = "B", threshold = 1)
public class BadPacketsB extends ArtemisCheck implements PacketHandler {

    private double yMap;
    private double yDifference;
    private int verbose;

    //TODO: Test


    public BadPacketsB(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            if (data.movement.getLocation() == null
                    || data.movement.getLastLocation() == null
                    || !data.movement.isOnGround()
                    || data.movement.getTeleportTicks() == 0
                    || data.user.isOnCooldown()) return;
            final double dif = yMap - data.movement.getLocation().getY();
            final int d = (int) (dif * 100);
            if ((dif > 0 && dif < 0.2) && (d != 98 && d != 99 && d != 97)) {
                if (Math.abs(dif - yDifference) == (Math.round(Math.abs(dif - yDifference) * 100.0) / 100.0)) {
                    verbose++;
                    if (verbose > 2) {
                        log("Consistent Y-Dif, " + dif + " -> Verbose " + verbose);
                    }
                } else {
                    verbose = 1;
                }
            }
            yDifference = dif;
            yMap = data.movement.getLocation().getY();
        }
        return;
    }
}
