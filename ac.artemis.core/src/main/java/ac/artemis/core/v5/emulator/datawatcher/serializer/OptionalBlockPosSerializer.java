package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataKey;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;

import java.util.Optional;

public class OptionalBlockPosSerializer extends AbstractDataSerializer<Optional<BlockPosition>> {
    public OptionalBlockPosSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Optional<BlockPosition> value) {
        if (value.isPresent()) {
            buf.writeBoolean(true);
            buf.writeBlockPositionIntoLong(value.get());
        } else {
            buf.writeBoolean(false);
        }

    }
    
    @Override
    public Optional<BlockPosition> read(ProtocolByteBuf buf) {
        return buf.readBoolean() ? Optional.of(buf.readBlockPositionFromLong()) : Optional.empty();
    }
    
    @Override
    public DataKey<Optional<BlockPosition>> createKey(int id) {
        return new DataKey<>(id, this);
    }
    
    @Override
    public Optional<BlockPosition> copyValue(Optional<BlockPosition> value)
    {
        return value;
    }
}
