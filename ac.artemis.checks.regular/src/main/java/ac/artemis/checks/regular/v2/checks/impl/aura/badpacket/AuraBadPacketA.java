package ac.artemis.checks.regular.v2.checks.impl.aura.badpacket;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.annotations.ServerVersion;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 30-Mar-20
 */

@Check(type = Type.AURA, var = "A", threshold = 2)
@ClientVersion
@ServerVersion
public class AuraBadPacketA extends ArtemisCheck implements PacketHandler {
    public AuraBadPacketA(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private boolean sent;

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            GPacketPlayClientUseEntity att = (GPacketPlayClientUseEntity) packet;
            if (att.getType().equals(PlayerEnums.UseType.ATTACK)
                    && !data.user.isLagging() && !sent) {
                log("Bad-Packet");
            }
        } else if (packet instanceof GPacketPlayClientArmAnimation) {
            sent = true;
        } else if (packet instanceof PacketPlayClientFlying) {
            sent = false;
        }
    }
}
