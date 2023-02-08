package ac.artemis.checks.regular.v2.checks.impl.aura.badpacket;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientHeldItemSlot;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 30-Mar-20
 */

@Check(type = Type.AURA, var = "I", threshold = 2)
public class AuraBadPacketI extends ArtemisCheck implements PacketHandler {

    private boolean sent;

    public AuraBadPacketI(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientHeldItemSlot) {

            if (sent) log("Bad-Packet");

        } else if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity act = (GPacketPlayClientUseEntity) packet;

            if (act.getType().equals(PlayerEnums.UseType.ATTACK)) {
                sent = true;
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            sent = false;
        }
    }
}
