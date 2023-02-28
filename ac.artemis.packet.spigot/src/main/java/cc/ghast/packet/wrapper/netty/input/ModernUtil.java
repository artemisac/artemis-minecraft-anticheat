package cc.ghast.packet.wrapper.netty.input;

import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

/**
 * @author Ghast
 * @since 18/10/2020
 * ArtemisPacket © 2020
 */
public class ModernUtil implements NettyUtil {
    @Override
    public Object newByteBufStream(MutableByteBuf byteBuf) {
        return new ByteBufInputStream((ByteBuf) byteBuf.getParent());
    }
}
