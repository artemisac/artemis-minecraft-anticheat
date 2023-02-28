package cc.ghast.packet.buffer.types.java;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.buffer.BufConverter;

/**
 * @author Ghast
 * @since 01-May-20
 */
public class BooleanConverter extends BufConverter<Boolean> {

    public BooleanConverter() {
        super("Boolean", Boolean.class);
    }

    @Override
    public void write(MutableByteBuf buffer, Boolean value) {
        buffer.writeBoolean(value);
    }

    @Override
    public Boolean read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        return buffer.readBoolean();
    }
}
