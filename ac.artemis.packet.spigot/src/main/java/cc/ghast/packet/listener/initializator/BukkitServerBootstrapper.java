package cc.ghast.packet.listener.initializator;

import cc.ghast.packet.PacketManager;
import cc.ghast.packet.profile.ArtemisProfile;
import ac.artemis.packet.protocol.ProtocolDirection;
import cc.ghast.packet.codec.ArtemisDecoder;
import cc.ghast.packet.codec.ArtemisEncoder;
import cc.ghast.packet.listener.injector.Injector;
import cc.ghast.packet.listener.injector.InjectorModern;
import cc.ghast.packet.reflections.ReflectUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Big credits to Myles; Heavily inspired from the BukkiChannelInitializer from ViaVersion
 * Can be found here: https://github.com/ViaVersion/ViaVersion/
 * @author Ghast
 * @since 18/08/2020
 * Artemis © 2020
 */
public class BukkitServerBootstrapper extends ChannelInitializer<Channel> {

    private final ChannelInitializer<Channel> original;
    private static Method initChannelMethod;

    public BukkitServerBootstrapper(ChannelInitializer<Channel> original) {
        this.original = original;
    }

    public ChannelInitializer<Channel> getOriginal() {
        return original;
    }

    @Override
    protected void initChannel(Channel socketChannel) throws Exception {
        final String address = ReflectUtil.parseAddress(socketChannel.remoteAddress());
        final UUID id = UUID.randomUUID();
        final ArtemisProfile info = new ArtemisProfile(id, null, address, socketChannel);

        // Inject the profile
        PacketManager.INSTANCE.getListener().getInjector().injectFuturePlayer(info);

        // Add originals
        initChannelMethod.invoke(this.original, socketChannel);

        socketChannel.pipeline().addBefore("decoder", Injector.clientBound,
                new ArtemisDecoder(info, ProtocolDirection.IN));
        socketChannel.pipeline().addBefore("encoder", Injector.serverBound,
                new ArtemisDecoder(info, ProtocolDirection.OUT));
        socketChannel.pipeline().addLast(Injector.encoder,
                new ArtemisEncoder(info));

        socketChannel.attr(InjectorModern.KEY_IDENTIFIER).set(id);

    }

    static {
        try {
            initChannelMethod = ChannelInitializer.class.getDeclaredMethod("initChannel", Channel.class);
            initChannelMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            PacketManager.INSTANCE.fatal("Failed to initialize Channel injection [Init Channel reflections]");
            e.printStackTrace();
        }
    }
}