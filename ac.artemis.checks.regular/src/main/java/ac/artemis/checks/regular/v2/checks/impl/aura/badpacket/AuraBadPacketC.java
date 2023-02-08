package ac.artemis.checks.regular.v2.checks.impl.aura.badpacket;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.nms.EnumDirection;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockPlace;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 30-Mar-20
 */

@Experimental()
@Check(type = Type.AURA, var = "C")
public class AuraBadPacketC extends ArtemisCheck implements PacketHandler {

    private boolean sent;

    public AuraBadPacketC(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity use = (GPacketPlayClientUseEntity) packet;
            if (use.getType().equals(PlayerEnums.UseType.ATTACK))
                sent = true;
        } else if (packet instanceof GPacketPlayClientBlockPlace) {
            final GPacketPlayClientBlockPlace plc = (GPacketPlayClientBlockPlace) packet;
            if (!plc.getItem().isPresent() || plc.getItem().get().getType() == null || !plc.getDirection().isPresent()) {
                return;
            }
            if (plc.getItem().get().getType().toString().contains("SWORD")) {
                return;
            }
            if (sent && plc.getDirection().get() != EnumDirection.DOWN) {
                log("Bad-Packet");
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            sent = false;
        }
    }
}
