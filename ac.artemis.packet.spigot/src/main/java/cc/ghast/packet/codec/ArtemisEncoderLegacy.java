package cc.ghast.packet.codec;

import ac.artemis.packet.PacketGenerator;
import ac.artemis.packet.spigot.utils.ServerUtil;
import cc.ghast.packet.exceptions.InvalidPacketException;
import cc.ghast.packet.profile.ArtemisProfile;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.WriteableBuffer;
import lombok.SneakyThrows;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Ghast
 * @since 30/08/2020
 * Artemis © 2020
 */
public class ArtemisEncoderLegacy extends MessageToByteEncoder<GPacket> {

    private final ArtemisProfile profile;
    private final PacketGenerator generator = ac.artemis.packet.PacketManager.getApi().getGenerator(ServerUtil.getGameVersion());

    public ArtemisEncoderLegacy(ArtemisProfile profile) {
        this.profile = profile;
    }

    @Override
    @SneakyThrows
    protected void encode(ChannelHandlerContext channelHandlerContext, GPacket obj, ByteBuf byteBuf) {
        final int packetId = generator.getPacketId(obj);

        if (packetId < 0){
            throw new InvalidPacketException(obj.getClass());
        }


        obj.setUuid(profile.getUuid());
        obj.setVersion(profile.getVersion());
        // Modify with hooks
        //PacketManager.INSTANCE.getHookManager().modifyAll(profile, ProtocolDirection.OUT, getZ);
        final MutableByteBuf alloc = MutableByteBuf.translate(byteBuf.retain());
        final ProtocolByteBuf transformed = new ProtocolByteBuf(alloc, profile.getVersion());

        try {
            transformed.writeVarInt(packetId);

            if (obj instanceof WriteableBuffer) {
                WriteableBuffer writeableBuffer = (WriteableBuffer) obj;
                writeableBuffer.write(transformed);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        byteBuf.resetReaderIndex();

    }

    @Override
    public boolean acceptOutboundMessage(Object msg) {
        return GPacket.class.isAssignableFrom(msg.getClass());
    }
}
