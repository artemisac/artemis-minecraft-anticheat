package cc.ghast.packet.buffer.types.java;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.buffer.BufConverter;

/**
 * @author Ghast
 * @since 01-May-20
 */
public class DoubleConverter extends BufConverter<Double> {

    public DoubleConverter() {
        super("Double", Double.class);
    }

    @Override
    public void write(MutableByteBuf buffer, Double value) {
        buffer.writeDouble(value);
    }

    @Override
    public Double read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        return buffer.readDouble();
    }
}
