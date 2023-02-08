package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataKey;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;

public class CompoundTagDataSerializer extends AbstractDataSerializer<CompoundTag> {
    public CompoundTagDataSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, CompoundTag value) {
        buf.writeTag(value);
    }

    @Override
    public CompoundTag read(ProtocolByteBuf buf) {
        return buf.readTag();
    }

    @Override
    public DataKey<CompoundTag> createKey(int id)
    {
        return new DataKey<>(id, this);
    }

    @Override
    public CompoundTag copyValue(CompoundTag value)
    {
        return value;
    }
}
