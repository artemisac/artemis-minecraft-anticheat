package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataKey;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;

public class BlockPosSerializer extends AbstractDataSerializer<BlockPosition> {
    public BlockPosSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, BlockPosition value) {
        buf.writeBlockPositionIntoLong(value);
    }

    @Override
    public BlockPosition read(ProtocolByteBuf buf) {
        return buf.readBlockPositionFromLong();
    }

    @Override
    public DataKey<BlockPosition> createKey(int id) {
        return new DataKey<>(id, this);
    }

    @Override
    public BlockPosition copyValue(BlockPosition value)
    {
        return value;
    }
}
