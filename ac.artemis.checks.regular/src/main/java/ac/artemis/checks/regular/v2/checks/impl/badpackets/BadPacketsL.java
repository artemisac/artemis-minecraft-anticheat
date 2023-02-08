package ac.artemis.checks.regular.v2.checks.impl.badpackets;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockPlace;

/**
 * @author Elevated
 * @since 18-Apr-20
 */

@Check(type = Type.BADPACKETS, var = "L", threshold = 1)
public class BadPacketsL extends ArtemisCheck implements PacketHandler {

    private Long lastFlying;
    private long lastMovePacket;
    private double vl;

    public BadPacketsL(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            final long now = System.currentTimeMillis();

            if (this.lastFlying != null) {
                final long delay = now - this.lastFlying;

                if (delay > 40L && delay < 100L) {
                    vl += 0.25;

                    if (vl > 0.5) {
                        log("vl=" + vl + " delay=" + delay);
                    }
                } else {
                    vl = Math.max(0, vl - 0.025);
                }

                this.lastFlying = null;
            }
        } else if (packet instanceof GPacketPlayClientBlockPlace) {
            final long now = System.currentTimeMillis();
            final long lastFlying = this.lastMovePacket;

            if (now - lastFlying < 10L) {
                this.lastFlying = lastFlying;
            } else {
                vl = Math.max(0, vl - 0.025);
            }
        }
    }
}
