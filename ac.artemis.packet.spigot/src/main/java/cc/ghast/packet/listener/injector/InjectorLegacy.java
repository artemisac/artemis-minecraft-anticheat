package cc.ghast.packet.listener.injector;

import cc.ghast.packet.PacketManager;
import cc.ghast.packet.listener.initializator.BukkitLegacyServerBootstrapper;
import cc.ghast.packet.profile.ArtemisProfile;
import ac.artemis.packet.callback.PacketCallback;
import ac.artemis.packet.callback.LoginCallback;
import cc.ghast.packet.reflections.FieldAccessor;
import cc.ghast.packet.reflections.ReflectUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.reflections.Reflection;
import net.minecraft.util.com.google.common.cache.Cache;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import net.minecraft.util.io.netty.channel.LegacyNettyProxy;
import net.minecraft.util.io.netty.channel.*;
import net.minecraft.util.io.netty.util.AttributeKey;
import org.bukkit.plugin.PluginDescriptionFile;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Massive credits to Myles. InjectReader is pretty much getX paste of his solution with getX lot changed around to
 * look nicer and more adequate; MIT license software ftw I guess.
 * @author Ghast, Myles
 * @since 18/08/2020
 * Artemis © 2020
 */
public class InjectorLegacy implements Injector {

    public InjectorLegacy() {
        System.out.println("[Artemis] Using Legacy Encoder");
    }

    public static final AttributeKey<UUID> KEY_IDENTIFIER = new AttributeKey<>("artemis_id");

    private final ChannelFuture future = (ChannelFuture) ReflectUtil.getChannelFuture();
    private final List<LoginCallback> callbacks = new ArrayList<>();
    private final Map<UUID, ArtemisProfile> profiles = new WeakHashMap<>();
    private final Cache<ArtemisProfile, Long> futureProfiles = CacheBuilder
            .newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    public static final FieldAccessor<ChannelPipeline> BRIDGE = Reflection.getField(AbstractChannel.class, "pipeline", ChannelPipeline.class);
    private ChannelFuture serverBoot;

    @Override
    public void injectReader() {
        ChannelPipeline pipeline = BRIDGE.get(future.channel());
        ChannelHandler serverBootstrap = pipeline.first();
        ChannelInitializer<Channel> serverBootstrapInit = null;

        iter: {
            /*
             * Here we iterate through every single pipeline and attempt to find the one which corresponds to the
             * server bootstrap. Such one will contain getX childHandler.
             */
            for (Map.Entry<String, ChannelHandler> stringChannelHandlerEntry : pipeline) {
                final ChannelHandler handler = stringChannelHandlerEntry.getValue();

                if (handler == null)
                    continue;

                /*
                 * This is quite unconventional but pretty much we attempt to get the field. If it does not exist or
                 * produces an error, we can pretty much be confident it is not the server bootstrap.
                 */
                try {
                    Field field = handler.getClass().getDeclaredField("childHandler");
                    field.setAccessible(true);
                    serverBootstrapInit = (ChannelInitializer) field.get(handler);
                    serverBootstrap = handler;
                    break iter;
                } catch (Exception e){
                    // Ignored
                    //e.printStackTrace();
                }
            }
        }


        /*
         * If there is no bootstrapper. Well... that's not good.
         */
        if (serverBootstrapInit == null) {
            throw new IllegalStateException("Not adequate bootstrapper found!");
        }

        try {
            ChannelInitializer<?> newBootstrapper = new BukkitLegacyServerBootstrapper(serverBootstrapInit);
            Field field = serverBootstrap.getClass().getDeclaredField("childHandler");
            field.setAccessible(true);
            field.set(serverBootstrap, newBootstrapper);
            this.serverBoot = future;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // let's find who to blame!
            final ClassLoader cl = serverBootstrap.getClass().getClassLoader();
            final boolean isPlugin = cl.getClass().getName().equals("org.bukkit.plugin.java.PluginClassLoader");
            if (isPlugin) {
                try {
                    PluginDescriptionFile yaml = (PluginDescriptionFile) cl.getClass().getField("description").get(cl);
                    throw new IllegalStateException("Unable to inject, due to " + serverBootstrap.getClass().getName() + ", try without the plugin " + yaml.getName() + "?");
                } catch (NoSuchFieldException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }

            else {
                throw new IllegalStateException("Unable to find core component 'childHandler', please check your plugins. issue: " + serverBootstrap.getClass().getName());
            }
        }
    }

    @Override
    public void uninjectReader() {
        PacketManager.INSTANCE.fatal("We failed to inject ViaVersion, have you got late-bind enabled with something else?");
        //getDirectionY.printStackTrace();
    }

    @Override
    public void injectPlayer(ArtemisProfile profile) {
        // ProtocolLib channel
        this.profiles.put(profile.getUuid(), profile);
        uninjectFuturePlayer(profile);
        PacketManager.INSTANCE.info("Successfully injected into user of UUID " + profile.getUuid());
    }

    @Override
    public void uninjectPlayer(UUID uuid) {
        if (this.profiles.containsKey(uuid)) {
            final ArtemisProfile profile = profiles.get(uuid);
            final Channel channel = (Channel) profile.getChannel();

            CompletableFuture.runAsync(() -> {
                final ChannelPipeline pipeline = BRIDGE.get(channel);
                if (pipeline != null && channel.isOpen() && channel.isActive()) {

                    ChannelHandler handler;
                    if ((handler = pipeline.get(clientBound)) != null) {
                        pipeline.remove(handler);
                    }

                    if ((handler = pipeline.get(serverBound)) != null) {
                        pipeline.remove(handler);
                    }

                    if ((handler = pipeline.get(encoder)) != null) {
                        pipeline.remove(handler);
                    }
                }
            });

            this.profiles.remove(uuid);
        }

    }

    @Override
    public void injectFuturePlayer(ArtemisProfile profile) {
        this.futureProfiles.put(profile, System.currentTimeMillis());
    }

    @Override
    public void uninjectFuturePlayer(ArtemisProfile profile) {
        new HashSet<>(this.futureProfiles.asMap().entrySet())
                .stream()
                .filter(e -> ((Channel) e.getKey().getChannel()).attr(KEY_IDENTIFIER).get().equals(profile.getId()))
                .findFirst()
                .ifPresent(e -> {
            futureProfiles.asMap().remove(e.getKey());
        });
    }

    @Override
    public ArtemisProfile getProfile(UUID uuid) {
        return this.profiles.get(uuid);
    }

    @Override
    public void addLoginCallback(LoginCallback loginCallback) {
        this.callbacks.add(loginCallback);
    }

    @Override
    public void removeLoginCallback(LoginCallback loginCallback) {
        this.callbacks.remove(loginCallback);
    }

    @Override
    public void callLoginCallbacks(ArtemisProfile profile) {
        this.callbacks.forEach(e -> e.onLogin(profile));
    }

    @Override
    public void writePacket(UUID target, GPacket packet, boolean flush, Consumer<PacketCallback> callback) {
        final ArtemisProfile profile = this.getProfile(target);

        if (profile == null) {
            throw new IllegalStateException("Attempt to send packet to an unregistered profile (uuid: "
                    + target + " packet: " + packet.getRealName());

        }

        final Channel channel = (Channel) profile.getChannel();

        if (channel == null) {
            throw new IllegalStateException("Attempt to send packet to getX profile without getX channel (uuid: "
                    + target + " packet: " + packet.getRealName());
        }

        if (!channel.isOpen()) {
            return;
        }

        final ChannelFuture channelfuture = flush
                ? channel.writeAndFlush(packet)
                : channel.write(packet);
        channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);

        if (callback != null) {
            channelfuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) {
                    callback.accept(new PacketCallback(System.currentTimeMillis(), channelfuture.isSuccess()
                            ? PacketCallback.Type.SUCCESS : PacketCallback.Type.FAILED));
                }
            });
        }

    }

    @Override
    public boolean contains(ArtemisProfile profile) {
        return this.profiles.containsValue(profile);
    }
}
