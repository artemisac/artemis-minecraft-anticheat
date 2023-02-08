package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataKey;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class IntDataSerializer extends AbstractDataSerializer<Integer> {
    public IntDataSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Integer value) {
        buf.writeInt(value);
    }

    @Override
    public Integer read(ProtocolByteBuf buf) {
        return buf.readInt();
    }

    @Override
    public DataKey<Integer> createKey(int id)
    {
        return new DataKey<>(id, this);
    }

    @Override
    public Integer copyValue(Integer value)
    {
        return value;
    }
}
