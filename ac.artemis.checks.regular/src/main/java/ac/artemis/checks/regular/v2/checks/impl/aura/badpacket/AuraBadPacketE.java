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
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientEntityAction;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 30-Mar-20
 */

@Check(type = Type.AURA, var = "E", threshold = 2)
@ClientVersion
public class AuraBadPacketE extends ArtemisCheck implements PacketHandler {
    private int stage;

    public AuraBadPacketE(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        final int modulo = stage % 6;

        if (modulo == 0) {
            if (packet instanceof GPacketPlayClientArmAnimation)
                ++stage;
            else
                stage = 0;
        } else if (modulo == 1) {
            if (packet instanceof GPacketPlayClientUseEntity)
                ++stage;
            else
                stage = 0;
        } else if (modulo == 2) {
            if (packet instanceof GPacketPlayClientEntityAction)
                ++stage;
            else
                stage = 0;
        } else if (modulo == 3) {
            if (packet instanceof PacketPlayClientFlying)
                ++stage;
            else
                stage = 0;
        } else if (modulo == 4) {
            if (packet instanceof GPacketPlayClientEntityAction)
                ++stage;
            else
                stage = 0;
        } else if (modulo == 5) {
            if (packet instanceof PacketPlayClientFlying) {
                if (++stage >= 30)
                    log("Bad-Packet: Modulo %1.8.8");
            } else
                stage = 0;
        }
    }
}
