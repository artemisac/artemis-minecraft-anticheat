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
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientEntityAction;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 30-Mar-20
 */

@Check(type = Type.AURA, var = "H")
@ClientVersion(/*version = {ProtocolVersion.V1_7, ProtocolVersion.V1_7_10, ProtocolVersion.V1_8, ProtocolVersion.V1_8_5,
        ProtocolVersion.V1_8_9, ProtocolVersion.V1_9, ProtocolVersion.V1_9_1, ProtocolVersion.V1_9_2, ProtocolVersion.V1_9_4,
        ProtocolVersion.V1_10, ProtocolVersion.V1_10_2, ProtocolVersion.V1_11, ProtocolVersion.V1_12, ProtocolVersion.V1_12_2,
        ProtocolVersion.V1_13, ProtocolVersion.V1_13_1, ProtocolVersion.V1_13_2, ProtocolVersion.V1_14, ProtocolVersion.V1_14_1,
        ProtocolVersion.V1_14_2, ProtocolVersion.V1_14_3, ProtocolVersion.V1_14_4}*/)
public class AuraBadPacketH extends ArtemisCheck implements PacketHandler {

    private boolean sent;

    public AuraBadPacketH(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity act = (GPacketPlayClientUseEntity) packet;

            if (act.getType().equals(PlayerEnums.UseType.ATTACK) && sent) {
                log("Bad-Packet");
            }

        } else if (packet instanceof GPacketPlayClientEntityAction) {
            final GPacketPlayClientEntityAction act = (GPacketPlayClientEntityAction) packet;

            if (act.getAction().equals(PlayerEnums.PlayerAction.START_SPRINTING)
                    || act.getAction().equals(PlayerEnums.PlayerAction.STOP_SPRINTING)
                    || act.getAction().equals(PlayerEnums.PlayerAction.START_SNEAKING)
                    || act.getAction().equals(PlayerEnums.PlayerAction.STOP_SNEAKING)) {
                sent = true;
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            sent = false;
        }
    }
}
