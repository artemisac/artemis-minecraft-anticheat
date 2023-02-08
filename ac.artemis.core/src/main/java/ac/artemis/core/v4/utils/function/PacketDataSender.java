package ac.artemis.core.v4.utils.function;

import ac.artemis.core.v4.data.PlayerData;

public interface PacketDataSender {
    void consume(PlayerData data, final short tick);
}
