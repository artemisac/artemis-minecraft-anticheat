package ac.artemis.anticheat.bukkit.listener;

import ac.artemis.packet.minecraft.Unsafe;
import ac.artemis.core.Artemis;
import ac.artemis.packet.PacketManager;
import ac.artemis.packet.callback.LoginCallback;
import ac.artemis.packet.profile.Profile;
import cc.ghast.packet.PacketAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerDataListener implements Listener  {
    public PlayerDataListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void start() {
        PacketManager.getApi().addLoginCallback(callback);
    }

    public void end() {
        PacketAPI.removeLoginCallback(callback);
    }

    private final Set<UUID> toInject = new HashSet<>();
    private final LoginCallback callback = new LoginCallback() {
        @Override
        public void onLogin(Profile profile) {
            //injectPlayer(profile.getUuid());
            final UUID uuid = profile.getUuid();
            if (toInject.contains(uuid)) {
                Artemis.v()
                        .getApi()
                        .getPlayerDataManager()
                        .injectPlayer(uuid);
            } else {
                toInject.add(uuid);
            }
        }
    };

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final UUID uuid = e.getPlayer().getUniqueId();
        if (toInject.contains(uuid)) {
            Artemis.v()
                    .getApi()
                    .getPlayerDataManager()
                    .injectPlayer(uuid);
        } else {
            toInject.add(uuid);
        }
    }

    @EventHandler()
    public void onLeave(PlayerQuitEvent e) {
        Artemis.v()
                .getApi()
                .getPlayerDataManager()
                .uninjectPlayer(Unsafe.v().fromBukkitPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Artemis.v()
                .getApi()
                .getPlayerDataManager()
                .uninjectPlayer(Unsafe.v().fromBukkitPlayer(e.getPlayer()));
    }

}
