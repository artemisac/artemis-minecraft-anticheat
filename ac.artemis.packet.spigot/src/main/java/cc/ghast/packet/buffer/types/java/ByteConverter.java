package cc.ghast.packet.buffer.types.java;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.buffer.BufConverter;

/**
 * @author Ghast
 * @since 01-May-20
 */
public class ByteConverter extends BufConverter<Byte> {

    public ByteConverter() {
        super("Byte", Byte.class);
    }

    @Override
    public void write(MutableByteBuf buffer, Byte value) {
        buffer.writeByte(value);
    }

    @Override
    public Byte read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        return buffer.readByte();
    }
}
