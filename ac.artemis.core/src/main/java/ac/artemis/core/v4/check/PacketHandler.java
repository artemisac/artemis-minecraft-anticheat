package ac.artemis.core.v4.check;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.spigot.wrappers.GPacket;

public interface PacketHandler {

    /**
     * This is a redundant method just to make sure if I ever make changes (which I will) to how packets are handled
     * before they're sent to the checks.
     *
     * @param packet GPacket Packet
     */
    void handle(final GPacket packet);
}
