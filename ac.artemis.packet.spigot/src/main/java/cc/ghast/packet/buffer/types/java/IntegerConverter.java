package cc.ghast.packet.buffer.types.java;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.buffer.BufConverter;

/**
 * @author Ghast
 * @since 01-May-20
 */
public class IntegerConverter extends BufConverter<Integer> {

    public IntegerConverter() {
        super("Integer", Integer.class);
    }

    @Override
    public void write(MutableByteBuf buffer, Integer value) {
        buffer.writeInt(value);
    }

    @Override
    public Integer read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        return buffer.readInt();
    }
}
