package cc.ghast.packet.buffer.types.java;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.buffer.BufConverter;

/**
 * @author Ghast
 * @since 01-May-20
 */
public class BytesConverter extends BufConverter<MutableByteBuf> {

    public BytesConverter() {
        super("ByteArray", MutableByteBuf.class);
    }

    @Override
    public void write(MutableByteBuf buffer, MutableByteBuf value) {
        buffer.writeBytes(value);
    }

    @Override
    public MutableByteBuf read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        return buffer.readBytes(buffer.readableBytes());
    }
}
