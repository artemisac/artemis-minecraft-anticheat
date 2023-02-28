package cc.ghast.packet;

import ac.artemis.packet.PacketListener;
import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.profile.ArtemisProfile;
import ac.artemis.packet.callback.LoginCallback;
import ac.artemis.packet.callback.PacketCallback;
import ac.artemis.packet.spigot.wrappers.GPacket;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Ghast
 * @since 15/08/2020
 * Artemis © 2020
 */
public class PacketAPI {
    public static void addListener(PacketListener listener){
        PacketManager.INSTANCE.getManager().injectListener(listener);
    }

    public static ArtemisProfile getProfile(UUID uuid) {
        return PacketManager.INSTANCE.getListener().getInjector().getProfile(uuid);
    }

    public static boolean isInjected(UUID uuid) {
        return PacketManager.INSTANCE.getListener().getInjector().getProfile(uuid) != null;
    }


    public static void disinject(Player player){
        if (isInjected(player.getUniqueId()))
            PacketManager.INSTANCE.getListener().getInjector()
                .uninjectPlayer(player.getUniqueId());
    }

    public static ProtocolVersion getVersion(UUID uuid) {
        return PacketManager.INSTANCE.getListener().getInjector().getProfile(uuid).getVersion();
    }

    public static void sendPacket(Player player, GPacket packet){
        sendPacket(player, packet, null);
    }

    public static void sendPacket(Player player, GPacket packet, Consumer<PacketCallback> callback){
        PacketManager.INSTANCE.getListener().getInjector().writePacket(player.getUniqueId(), packet, true, callback);
    }

    public static void addLoginCallback(LoginCallback loginCallback) {
        PacketManager.INSTANCE.getListener().getInjector().addLoginCallback(loginCallback);
    }

    public static void removeLoginCallback(LoginCallback loginCallback) {
        PacketManager.INSTANCE.getListener().getInjector().removeLoginCallback(loginCallback);
    }
}
