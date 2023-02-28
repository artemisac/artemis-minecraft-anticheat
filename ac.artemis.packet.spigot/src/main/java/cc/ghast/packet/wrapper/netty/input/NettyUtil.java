package cc.ghast.packet.wrapper.netty.input;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.utils.ServerUtil;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;

public interface NettyUtil {

    NettyUtil instance = ServerUtil.getGameVersion().isOrAbove(ProtocolVersion.V1_8) ? new ModernUtil() : new LegacyUtil();

    static NettyUtil getInstance() {
        return instance;
    }

    Object newByteBufStream(MutableByteBuf byteBuf);
}
