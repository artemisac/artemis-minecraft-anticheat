package cc.ghast.packet.buffer.types.minecraft;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.buffer.BufConverter;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;

public class LongLocationConverter extends BufConverter<BlockPosition> {


    public LongLocationConverter() {
        super("LongLocation", BlockPosition.class);
    }

    @Override
    public void write(MutableByteBuf buffer, BlockPosition value) {
        long var = ((long) value.getX() & BlockPosition.h) << BlockPosition.g
                | ((long) value.getY() & BlockPosition.i) << BlockPosition.f
                | ((long) value.getZ() & BlockPosition.j) << 0;

        buffer.writeLong(var);
    }

    @Override
    public BlockPosition read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        long position = buffer.readLong();

        int x, y, z;

        if (version.isAbove(ProtocolVersion.V1_14)) {
            x = (int) (position >> 38);
            y = (int) (position & 0xFFF);
            z = (int) ((position << 26 >> 38));
        } else {
            x = (int) (position >> 38);
            y = (int) ((position >> 26) & 0xFFF);
            z = (int) (position << 38 >> 38);
        }



        return new BlockPosition(x, y, z);
    }
}
