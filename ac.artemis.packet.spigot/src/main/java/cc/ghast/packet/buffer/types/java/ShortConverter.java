package cc.ghast.packet.buffer.types.java;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.buffer.BufConverter;

/**
 * @author Ghast
 * @since 01-May-20
 */
public class ShortConverter extends BufConverter<Short> {

    public ShortConverter() {
        super("Short", Short.class);
    }

    @Override
    public void write(MutableByteBuf buffer, Short value) {
        buffer.writeShort(value);
    }

    @Override
    public Short read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        return buffer.readShort();
    }
}
