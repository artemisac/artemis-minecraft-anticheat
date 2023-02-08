package ac.artemis.checks.regular.v2.checks.impl.badpackets;


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

@Check(type = Type.BADPACKETS, var = "G", threshold = 1)
@ClientVersion
public class BadPacketsG extends ArtemisCheck implements PacketHandler {

    private boolean sent;

    public BadPacketsG(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            if (sent) {
                log("Invalid hit packet");
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            sent = false;
        } else if (packet instanceof GPacketPlayClientBlockDig) {
            final GPacketPlayClientBlockDig wrapper = (GPacketPlayClientBlockDig) packet;

            if (wrapper.getType() != null
                    && wrapper.getType() != PlayerEnums.DigType.DROP_ALL_ITEMS
                    && wrapper.getType() != PlayerEnums.DigType.DROP_ITEM) {
                sent = true;
            }
        }
    }
}
