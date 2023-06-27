package ac.artemis.packet.wrapper.client;

import ac.artemis.packet.wrapper.PacketClient;

public interface PacketPlayClientPing extends PacketClient {
    /**
     * @return ID of the ping to the client
     */
    int getId();
}
