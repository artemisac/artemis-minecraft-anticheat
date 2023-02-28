package cc.ghast.packet.codec;

import ac.artemis.packet.PacketGenerator;
import ac.artemis.packet.spigot.utils.ServerUtil;
import cc.ghast.packet.PacketManager;
import cc.ghast.packet.exceptions.InvalidPacketException;
import cc.ghast.packet.profile.ArtemisProfile;
import ac.artemis.packet.protocol.ProtocolDirection;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.protocol.EnumProtocolCurrent;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.WriteableBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.SneakyThrows;
import org.bukkit.Server;

/**
 * @author Ghast
 * @since 30/08/2020
 * Artemis © 2020
 */
public class ArtemisEncoder extends MessageToByteEncoder<GPacket> {

    private final ArtemisProfile profile;
    private PacketGenerator generator = ac.artemis.packet.PacketManager.getApi().getGenerator(ServerUtil.getGameVersion());

    public ArtemisEncoder(ArtemisProfile profile) {
        this.profile = profile;
    }

    @Override
    @SneakyThrows
    protected void encode(ChannelHandlerContext channelHandlerContext, GPacket obj, ByteBuf byteBuf) {
        final Integer packetId = generator == null
                ? (generator = ac.artemis.packet.PacketManager.getApi().getGenerator(ServerUtil.getGameVersion())).getPacketId(obj)
                : generator.getPacketId(obj);

        if (packetId == null || packetId < 0){
            ServerUtil.sendConsoleMessage("&4&lFailed packet encoding! Fatal error... (" + obj.getRealName() + ")");
            throw new InvalidPacketException(obj.getClass());
        }

        final boolean viaVersion = PacketManager.INSTANCE.getHookManager().getViaVersionHook() != null;


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

        if (viaVersion) {
            byteBuf.resetReaderIndex();
            final ByteBuf parent = PacketManager.INSTANCE
                    .getHookManager()
                    .getViaVersionHook()
                    .transformPacketSend(profile.getUuid(), byteBuf, packetId);

            if (parent == null) {
                byteBuf.clear().resetReaderIndex();
                return;
            }

        } else {
        }

        //System.out.println("Sending packet of id " + new ProtocolByteBuf(MutableByteBuf.translate(byteBuf), profile.getVersion()).readVarInt() + " of class " + obj.getClass().getName());
        byteBuf.resetReaderIndex();

    }

    @Override
    public boolean acceptOutboundMessage(Object msg) {
        return GPacket.class.isAssignableFrom(msg.getClass());
    }
}
