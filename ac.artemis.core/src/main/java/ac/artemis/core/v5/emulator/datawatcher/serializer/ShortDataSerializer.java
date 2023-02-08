package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataKey;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class ShortDataSerializer extends AbstractDataSerializer<Short> {
    public ShortDataSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Short value) {
        buf.writeShort(value);
    }

    @Override
    public Short read(ProtocolByteBuf buf) {
        return buf.readShort();
    }

    @Override
    public DataKey<Short> createKey(int id)
    {
        return new DataKey<>(id, this);
    }

    @Override
    public Short copyValue(Short value)
    {
        return value;
    }
}
