package ac.artemis.core.v4.utils.function;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;

public interface PacketDataConsumer {
    void consume(PlayerData data, GPacket packet);
}
