package cc.ghast.packet.buffer.types.java;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.buffer.BufConverter;

/**
 * @author Ghast
 * @since 01-May-20
 */
public class FloatConverter extends BufConverter<Float> {

    public FloatConverter() {
        super("Float", Float.class);
    }

    @Override
    public void write(MutableByteBuf buffer, Float value) {
        buffer.writeFloat(value);
    }

    @Override
    public Float read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        return buffer.readFloat();
    }
}
