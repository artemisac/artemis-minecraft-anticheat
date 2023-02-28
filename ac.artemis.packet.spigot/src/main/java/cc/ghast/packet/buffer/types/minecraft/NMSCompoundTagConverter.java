package cc.ghast.packet.buffer.types.minecraft;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.buffer.BufConverter;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.wrapper.netty.MutableByteBufOutputStream;
import cc.ghast.packet.wrapper.netty.input.NettyUtil;
import cc.ghast.packet.reflections.ReflectUtil;

/**
 * @author Ghast
 * @since 30/08/2020
 * Artemis © 2020
 */
public class NMSCompoundTagConverter extends BufConverter<Object> {
    public NMSCompoundTagConverter() {
        super("NBTCompound", Object.class);
    }

    @Override
    public void write(MutableByteBuf buffer, Object value) {
        ReflectUtil.writeCompoundTag(value, MutableByteBufOutputStream.build(buffer));
    }

    @Override
    public Object read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        int index = buffer.readerIndex();
        byte id = buffer.readByte();

        if (id == 0) {
            return null;
        } else {
            buffer.readerIndex(index);
            return ReflectUtil.getCompoundTag(NettyUtil.getInstance().newByteBufStream(buffer));
        }
    }
}
