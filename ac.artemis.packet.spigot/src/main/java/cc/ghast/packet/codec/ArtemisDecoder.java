package cc.ghast.packet.codec;

import ac.artemis.packet.PacketGenerator;
import ac.artemis.packet.protocol.ProtocolDirection;
import ac.artemis.packet.protocol.ProtocolState;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.utils.ServerUtil;
import cc.ghast.packet.PacketManager;
import cc.ghast.packet.buffer.types.Converters;
import cc.ghast.packet.exceptions.IncompatiblePipelineException;
import cc.ghast.packet.profile.ArtemisProfile;
import cc.ghast.packet.utils.Chat;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.wrapper.packet.login.GPacketLoginServerSuccess;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.protocol.EnumProtocol;
import cc.ghast.packet.protocol.EnumProtocolCurrent;
import cc.ghast.packet.protocol.EnumProtocolLegacy;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.handshake.GPacketHandshakeClientSetProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.DecoderException;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * @author Ghast
 * @since 24-Apr-20
 */
public class ArtemisDecoder extends ChannelDuplexHandler {

    private static final boolean debug = false;

    private final ArtemisProfile profile;
    private final Inflater inflater;
    private final ProtocolDirection direction;
    private boolean compress;
    private static final ExecutorService PACKET_SERVICE = Executors.newSingleThreadExecutor();

    public ArtemisDecoder(ArtemisProfile profile, ProtocolDirection direction) {
        this.profile = profile;
        this.inflater = new Inflater();
        this.direction = direction;
        /*this.protocol = ServerUtil.getGameVersion().isBelow(ProtocolVersion.V1_8_9)
                ? EnumProtocolCurrent.HANDSHAKE
                : EnumProtocolLegacy.HANDSHAKE;*/
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (direction.equals(ProtocolDirection.IN)) {
            this.handle(ctx, msg);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        promise.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        if (direction.equals(ProtocolDirection.OUT)){
            this.handle(ctx, msg);
        }
        super.write(ctx, msg, promise);
    }

    @SneakyThrows
    private void handle(ChannelHandlerContext ctx, Object msg) {
        if (debug) {
            System.out.println("Has decoder: " + (ctx.channel().pipeline().get("decoder") != null));
            System.out.println("Has decompresser: " + (ctx.channel().pipeline().get("decompress") != null));
            System.out.println("Structure: " + ctx.channel().pipeline().toMap());
            System.out.println(msg.toString());
        }

        // Make sure we're receiving getX ByteBuf. If getX protocol is placed before such, it may screw with the decompression
        if (msg instanceof ByteBuf) {
            final int index = ((ByteBuf) msg).readerIndex();
            // Decode the message and send it off
            final ByteBuf buffer = Unpooled.unmodifiableBuffer((ByteBuf) msg).copy().retain();
            try {
                final int readIndex = buffer.readerIndex();
                final boolean cancelled = buffer.isReadable() && decode(MutableByteBuf.translate(buffer));

                // Nullify if the packet was cancelled
                buffer.readerIndex(readIndex);
            } catch (Exception e){
                System.out.println("[!] Error on player of version " + profile.getVersion());
                e.printStackTrace();
            }
            ((ByteBuf) msg).readerIndex(index);
        } else {
            // If the message is not getX ByteBuf, there's obviously an issue with the pipeline, so disinject and throw error
            new IncompatiblePipelineException(msg.getClass()).printStackTrace();
        }
    }

    protected boolean decode(MutableByteBuf in) throws Exception {
        final Channel channel = (Channel) profile.getChannel();
        final boolean isCompressor = channel.pipeline().names().contains("decompress") ||
                channel.pipeline().names().contains("compress");

        /*
         * Why
         */
        if (!compress && isCompressor && (profile.getVersion().isOrAbove(ProtocolVersion.V1_8))) {
            try {
                in = decompress(in);
            } catch (Exception e){
                in.resetReaderIndex();
                compress = true;
                byte[] abyte = new byte[in.readableBytes()];
                in.readBytes(abyte);
                in.resetReaderIndex();
                in = MutableByteBuf.translate(Unpooled.wrappedBuffer(abyte));
                /*e.printStackTrace();
                Bukkit.getScheduler().runTask(PacketManager.INSTANCE.getPlugin(), () -> {
                    Bukkit.getPlayer(profile.getUuid()).kickPlayer(e.getMessage());
                });*/
            }
        } else {
            byte[] abyte = new byte[in.readableBytes()];
            in.readBytes(abyte);
            in.resetReaderIndex();
            in = MutableByteBuf.translate(Unpooled.wrappedBuffer(abyte));
        }


        if (in.readableBytes() > 0) {
            final int readerIndex = in.readerIndex();
            // Get the var_int packet id of the packet. This is quite important as it's what determines it's type
            int id = Converters.VAR_INT.read(in, profile.getVersion());
            final int oldId = id;

            if (profile.getGenerator() != null && profile.getVersion() != profile.getGenerator().getVersion()) {
                profile.setGenerator(ac.artemis.packet.PacketManager.getApi().getGenerator(profile.getVersion()));
            }

            if (profile.getGenerator() == null) {
                profile.setGenerator(ac.artemis.packet.PacketManager.getApi().getGenerator(ServerUtil.getGameVersion()));
            }

            final boolean viaVersion = PacketManager.INSTANCE.getHookManager().getViaVersionHook() != null
                    && profile.getProtocol().equals(ProtocolState.PLAY)
                    && !profile.getVersion().equals(ServerUtil.getGameVersion())
                    && direction.equals(ProtocolDirection.IN);

            if (viaVersion) {
                in.readerIndex(readerIndex);

                ByteBuf parent = PacketManager.INSTANCE
                        .getHookManager()
                        .getViaVersionHook()
                        .transformPacket(profile.getUuid(), (ByteBuf) in.getParent(), id);

                if (parent == null)
                    return false;

                in = MutableByteBuf.translate(parent);
                in.readerIndex(readerIndex);
                id = Converters.VAR_INT.read(in, ServerUtil.getGameVersion());
            }

            if (debug) {
                System.out.println("Reader index=" + in.readerIndex());
                System.out.println("Id of " + id);
            }

            // Initialize the protocol byte buf
            final ProtocolByteBuf protocolByteBuf = new ProtocolByteBuf(in, profile.getVersion());
            protocolByteBuf.setId(id);

            // Collect the packet from the enum map. This needs to be rewritten for better accuracy tho
            final GPacket packet;

            try {
                if (viaVersion) {
                    packet = (GPacket) ac.artemis.packet.PacketManager.getApi().getGenerator(ServerUtil.getGameVersion())
                            .getPacketFromId(direction, profile.getProtocol(), protocolByteBuf.getId(), profile.getUuid(), ServerUtil.getGameVersion());
                } else {
                    packet = (GPacket) profile.getGenerator()
                            .getPacketFromId(direction, profile.getProtocol(), protocolByteBuf.getId(), profile.getUuid(), profile.getVersion());
                }
            } catch (Throwable e) {
                System.out.println("Error on packet of id " + id + " of user of UUID " + profile.getUuid()
                        + " (ver: " + profile.getVersion() + " dir:" + direction + ")");
                e.printStackTrace();
                in.resetReaderIndex();
                return false;
            }


            if (packet != null) {
                if (packet instanceof ReadableBuffer) {
                    ReadableBuffer buffer = (ReadableBuffer) packet;
                    try {
                        buffer.read(protocolByteBuf);
                    } catch (Exception e) {
                        System.out.println("Error on packet of player of version " + profile.getVersion()
                                + " (ViaVersion: " + viaVersion
                                + " ver: " + packet.getVersion()
                                + " id: " + id
                                + " oldId: " + oldId
                                + " dir:" + direction + ")");
                        e.printStackTrace();
                    }
                }

                if (debug) {
                    System.out.println("pakcet=" + packet.getRealName());
                }

                // Handle and collect the handshake
                if (packet instanceof GPacketHandshakeClientSetProtocol){
                    handleHandshake((GPacketHandshakeClientSetProtocol) packet);
                }

                else if (packet instanceof GPacketLoginServerSuccess) {
                    handleLoginSuccess((GPacketLoginServerSuccess) packet);
                }

                if (profile.getUuid() != null && PacketManager.INSTANCE.getListener().getInjector().contains(profile)) {
                    PacketManager.INSTANCE.getManager().callPacket(profile, packet);
                }

                if (packet.isCancelled()) {
                    return true;
                }
            } else {
                //System.out.println("Failed to handle packet of id " + id + " of state " + profile.getProtocol() + " of version " + profile.getVersion());
            }

            // Reset the reader index to prevent following pipelines to have getX sort of issue. Normally it doesn't, I'm
            // Still new to Netty. I'll need to investigate. More can be seen @ https://netty.io/4.0/api/io/netty/buffer/ByteBuf.html
            in.resetReaderIndex();

            if (debug) {
                System.out.println("Packet of ID=" + packet);
            }

        } else {
            if (debug) System.out.println("NO READABLE BYTES BRUH");
        }

        in.release();

        return false;
    }

    private MutableByteBuf decompress(MutableByteBuf byteBuf) throws DataFormatException, DecoderException {
        if (byteBuf.readableBytes() != 0) {
            ProtocolByteBuf packetdataserializer = new ProtocolByteBuf(byteBuf, profile.getVersion());
            int i = packetdataserializer.readVarInt();

            if (i == 0) {
                return packetdataserializer.readBytes(byteBuf.readableBytes());
            } else {
                if (i < 256) {
                    throw new DecoderException("Badly compressed packet - size of " + i
                            + " is below server threshold of " + 256);
                }

                if (i > 2097152) {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of " + 2097152);
                }

                byte[] abyte = new byte[packetdataserializer.readableBytes()];

                packetdataserializer.readBytes(abyte);
                this.inflater.setInput(abyte);
                byte[] abyte1 = new byte[i];

                this.inflater.inflate(abyte1);
                this.inflater.reset();
                return MutableByteBuf.translate(Unpooled.wrappedBuffer(abyte1));
            }

        }
        return byteBuf;
    }

    private MutableByteBuf prepend(MutableByteBuf byteBuf) {
        final int readableBytes = byteBuf.readableBytes();
        final int size = prependData(readableBytes);
        if (size > 3) {
            throw new IllegalArgumentException("unable to fit " + readableBytes + " into " + 3);
        }
        final ProtocolByteBuf packetDataSerializer = new ProtocolByteBuf(
                MutableByteBuf.translate(((ByteBuf)byteBuf.getByteBuf().getParent()).alloc().buffer()),
                profile.getVersion()
        );
        packetDataSerializer.ensureWritable(size + readableBytes);
        packetDataSerializer.writeVarInt(readableBytes);
        packetDataSerializer.writeBytes(byteBuf, byteBuf.readerIndex(), readableBytes);
        return packetDataSerializer.getByteBuf();
    }

    private void handleHandshake(GPacketHandshakeClientSetProtocol handshake){
        final ProtocolVersion version = ProtocolVersion.getVersion(handshake.getProtocolVersion());

        if (PacketManager.INSTANCE.getHookManager().getViaVersionHook() == null) {
            profile.setVersion(version);
        }
        final GPacketHandshakeClientSetProtocol.State state = handshake.getNextState();
        this.profile.setProtocol(state.equals(GPacketHandshakeClientSetProtocol.State.STATUS)
                ? ProtocolState.STATUS : ProtocolState.LOGIN);
        this.profile.setVersion(version);

        profile.setGenerator(ac.artemis.packet.PacketManager.getApi().getGenerator(version));
    }

    private void handleLoginSuccess(GPacketLoginServerSuccess loginSuccess) {
        this.profile.setProtocol(ProtocolState.PLAY);
        this.profile.setUuid(loginSuccess.getGameProfile().getId());
        PacketManager.INSTANCE.getListener().getInjector().injectPlayer(profile);
        PacketManager.INSTANCE.getListener().getInjector().callLoginCallbacks(profile);
        Bukkit.getConsoleSender().sendMessage(
                Chat.translate("&r[&bPacket&r] &aSuccessfully &binjected into player &r"
                + loginSuccess.getGameProfile().getName() + " &b of UUID &r" + loginSuccess.getGameProfile().getId()));
    }

    public static int prependData(final int i) {
        for (int j = 1; j < 5; ++j) {
            if ((i & -1 << j * 7) == 0x0) {
                return j;
            }
        }
        return 5;
    }
}
