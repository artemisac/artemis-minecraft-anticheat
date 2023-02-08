package ac.artemis.checks.enterprise.protocol;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientHeldItemSlot;

/**
 * @author Ghast
 * @since 04/12/2020
 * Artemis © 2020
 *
 * This is a simple packet slot check. I haven't fully checked MCP, though as far as
 * I am concerned the only slots capable of being selected for this specific check are
 * the hotbar ones, hence [0;8].
 */
@Check(type = Type.PROTOCOL, var = "G", threshold = 1)
public class ProtocolG extends ArtemisCheck implements PacketHandler {

    public ProtocolG(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(GPacket packet) {
        if (packet instanceof GPacketPlayClientHeldItemSlot) {
            final GPacketPlayClientHeldItemSlot wrapper = (GPacketPlayClientHeldItemSlot) packet;

            if (wrapper.getSlot() > 8 || wrapper.getSlot() < 0) {
                log();
            }
        }
    }
}
