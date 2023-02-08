package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataKey;
import cc.ghast.packet.buffer.ProtocolByteBuf;

import java.util.Optional;

public class OptionalBlockStateSerializer extends AbstractDataSerializer<Optional<Integer>> {
    public OptionalBlockStateSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Optional<Integer> value) {
        if (value.isPresent()) {
            buf.writeBoolean(true);
            buf.writeVarInt(value.get());
        } else {
            buf.writeBoolean(false);
        }

    }
    
    @Override
    public Optional<Integer> read(ProtocolByteBuf buf) {
        return buf.readBoolean() ? Optional.of(buf.readVarInt()) : Optional.empty();
    }
    
    @Override
    public DataKey<Optional<Integer>> createKey(int id) {
        return new DataKey<>(id, this);
    }
    
    @Override
    public Optional<Integer> copyValue(Optional<Integer> value)
    {
        return value;
    }
}
