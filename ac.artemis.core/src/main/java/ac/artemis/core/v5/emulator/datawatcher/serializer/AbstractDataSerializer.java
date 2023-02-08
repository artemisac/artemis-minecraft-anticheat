package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataSerializer;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public abstract class AbstractDataSerializer<T> implements DataSerializer<T> {
    protected final PlayerData data;

    public AbstractDataSerializer(PlayerData data) {
        this.data = data;
    }

    public abstract T read(ProtocolByteBuf buf);
}
