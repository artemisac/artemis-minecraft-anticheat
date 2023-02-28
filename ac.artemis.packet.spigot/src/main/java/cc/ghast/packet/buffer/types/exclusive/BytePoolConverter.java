package cc.ghast.packet.buffer.types.exclusive;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.buffer.BufConverter;
import cc.ghast.packet.buffer.types.Converters;
import cc.ghast.packet.wrapper.codec.BytePool;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;


public class BytePoolConverter extends BufConverter<BytePool> {
    public BytePoolConverter() {
        super("BytePool", BytePool.class);
    }

    @Override
    public void write(MutableByteBuf buffer, BytePool value) {
        Converters.VAR_INT.write(buffer, value.getVar());
        buffer.writeBytes(value.getData());
    }

    @Override
    public BytePool read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        int varint = Converters.VAR_INT.read(buffer, version);
        byte[] abyte = new byte[varint];
        buffer.readBytes(abyte);
        return new BytePool(abyte, varint);
    }
}
