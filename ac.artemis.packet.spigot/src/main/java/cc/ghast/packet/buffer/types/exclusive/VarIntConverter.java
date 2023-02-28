package cc.ghast.packet.buffer.types.exclusive;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.buffer.BufConverter;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;

/**
 * @author Ghast
 * @since 01-May-20
 */
public class VarIntConverter extends BufConverter<Integer> {
    public VarIntConverter() {
        super("VarInt", int.class);
    }

    @Override
    public void write(MutableByteBuf buffer, Integer value) {
        while ((value & -128) != 0) {
            buffer.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        buffer.writeByte(value);
    }

    @Override
    public Integer read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        int i = 0;
        int j = 0;

        byte b0;

        do {
            b0 = buffer.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }

    public static int getVarIntSize(int input) {
        for (int i = 1; i < 5; ++i) {
            if ((input & -1 << i * 7) == 0) {
                return i;
            }
        }

        return 5;
    }
}
