package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataKey;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class BooleanDataSerializer extends AbstractDataSerializer<Boolean> {
    public BooleanDataSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Boolean value) {
        buf.writeBoolean(value);
    }

    @Override
    public Boolean read(ProtocolByteBuf buf) {
        return buf.readBoolean();
    }

    @Override
    public DataKey<Boolean> createKey(int id)
    {
        return new DataKey<>(id, this);
    }

    @Override
    public Boolean copyValue(Boolean value)
    {
        return value;
    }
}
