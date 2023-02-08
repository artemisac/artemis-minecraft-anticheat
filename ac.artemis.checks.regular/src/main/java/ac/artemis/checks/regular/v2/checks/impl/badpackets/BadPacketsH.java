package ac.artemis.checks.regular.v2.checks.impl.badpackets;


import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientEntityAction;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

@Check(type = Type.BADPACKETS, var = "H", threshold = 1)
public class BadPacketsH extends ArtemisCheck implements PacketHandler {

    private boolean sent;

    public BadPacketsH(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity useEntity = (GPacketPlayClientUseEntity) packet;

            if (sent && useEntity.getType().equals(PlayerEnums.UseType.ATTACK)) {
                log("Bad-Packet");
            }
        } else if (packet instanceof GPacketPlayClientEntityAction) {
            final GPacketPlayClientEntityAction entityAction = (GPacketPlayClientEntityAction) packet;

            if (entityAction.getAction().equals(PlayerEnums.PlayerAction.START_SPRINTING)
                    || entityAction.getAction().equals(PlayerEnums.PlayerAction.STOP_SPRINTING)
                    || entityAction.getAction().equals(PlayerEnums.PlayerAction.START_SNEAKING)
                    || entityAction.getAction().equals(PlayerEnums.PlayerAction.STOP_SNEAKING)) {
                sent = true;
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            sent = false;
        }
    }
}
