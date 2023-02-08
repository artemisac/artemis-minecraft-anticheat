package ac.artemis.checks.regular.v2.checks.impl.aura.badpacket;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockDig;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 30-Mar-20
 */

@Check(type = Type.AURA, var = "J", threshold = 2)
@ClientVersion
public class AuraBadPacketJ extends ArtemisCheck implements PacketHandler {
    private boolean sent;

    public AuraBadPacketJ(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            sent = true;
        } else if (packet instanceof GPacketPlayClientBlockDig) {
            final GPacketPlayClientBlockDig dig = (GPacketPlayClientBlockDig) packet;

            if (dig.getType() == null) return;

            if ((dig.getType().equals(PlayerEnums.DigType.START_DESTROY_BLOCK)
                    || dig.getType().equals(PlayerEnums.DigType.RELEASE_USE_ITEM))
                    && sent) {
                log("Bad-Packet");
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            sent = false;
        }
    }
}
