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
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 30-Mar-20
 */

@Check(type = Type.AURA, var = "O", threshold = 2)
public class AuraBadPacketO extends ArtemisCheck implements PacketHandler {

    private int buffer;
    private boolean sent;

    public AuraBadPacketO(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity use = (GPacketPlayClientUseEntity) packet;

            if (use.getType().equals(PlayerEnums.UseType.ATTACK)) {
                if (sent && data.user.isInventoryOpen() && buffer++ > 3) {
                    log();
                } else {
                    buffer = 0;
                }
            }
        } else if (packet instanceof GPacketPlayClientArmAnimation) {
            sent = true;
        } else if (packet instanceof PacketPlayClientFlying) {
            sent = false;
        }
    }
}
