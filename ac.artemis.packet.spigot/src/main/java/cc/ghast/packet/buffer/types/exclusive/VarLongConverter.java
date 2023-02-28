package cc.ghast.packet.buffer.types.exclusive;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.buffer.BufConverter;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;

/**
 * @author Ghast
 * @since 01-May-20
 */
public class VarLongConverter extends BufConverter<Long> {
    public VarLongConverter() {
        super("VarLong", Long.class);
    }

    @Override
    public void write(MutableByteBuf buffer, Long value) {
        while ((value & -128L) != 0L) {
            buffer.writeByte((int) (value & 127L) | 128);
            value >>>= 7;
        }

        buffer.writeByte(value.intValue());
    }

    @Override
    public Long read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        long i = 0L;
        int j = 0;

        while (true) {
            byte b0 = buffer.readByte();
            i |= (long) (b0 & 127) << j++ * 7;

            if (j > 10) {
                throw new RuntimeException("VarLong too big");
            }

            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }
}
