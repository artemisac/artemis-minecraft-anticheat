package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataKey;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class ByteDataSerializer extends AbstractDataSerializer<Byte> {
    public ByteDataSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Byte value)
    {
        buf.writeByte(value);
    }

    @Override
    public Byte read(ProtocolByteBuf buf) {
        return buf.readByte();
    }

    @Override
    public DataKey<Byte> createKey(int id)
    {
        return new DataKey<Byte>(id, this);
    }

    @Override
    public Byte copyValue(Byte value)
    {
        return value;
    }
}
