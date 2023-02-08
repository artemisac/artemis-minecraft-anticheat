package ac.artemis.checks.regular.v2.checks.impl.aura;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 21-Mar-20
 */

@Check(type = Type.AURA, var = "Movement")
public class AuraMovement extends ArtemisCheck implements PacketHandler {

    private Long lastFlying;
    private long lastMovePacket;
    private double vl;

    public AuraMovement(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            final long now = packet.getTimestamp();

            if (data.movement.getTeleportTicks() > 0 || data.user.isLagging() || data.getPlayer() == null || data.getPlayer().isDead()) {
                return;
            }

            if (this.lastFlying != null) {
                if (now - this.lastFlying > 40L && now - this.lastFlying < 90L) {
                    if ((vl += 0.25) > 1.5) {
                        log(2);
                    }
                } else {
                    vl = Math.max(0, vl - 0.05);
                }

                this.lastFlying = null;
            }

            this.lastMovePacket = now;
        } else if (packet instanceof GPacketPlayClientUseEntity
                && ((GPacketPlayClientUseEntity) packet).getType() == PlayerEnums.UseType.ATTACK) {
            final long now = packet.getTimestamp();
            final long lastFlying = this.lastMovePacket;

            if (now - lastFlying < 10L) {
                this.lastFlying = lastFlying;
            } else {
                vl = Math.max(0, vl - 0.025);
            }
        }
    }
}
