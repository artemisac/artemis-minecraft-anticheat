package cc.ghast.packet.buffer.types.java;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.buffer.BufConverter;

/**
 * @author Ghast
 * @since 01-May-20
 */
public class LongConverter extends BufConverter<Long> {

    public LongConverter() {
        super("Long", Long.class);
    }

    @Override
    public void write(MutableByteBuf buffer, Long value) {
        buffer.writeLong(value);
    }

    @Override
    public Long read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        return buffer.readLong();
    }
}
