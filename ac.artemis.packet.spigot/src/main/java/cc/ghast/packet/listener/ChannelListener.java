package cc.ghast.packet.listener;

import ac.artemis.packet.spigot.utils.ServerUtil;
import cc.ghast.packet.PacketManager;
import cc.ghast.packet.listener.injector.InjectorFactory;
import cc.ghast.packet.utils.Chat;
import cc.ghast.packet.listener.injector.Injector;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Ghast
 * @since 11-May-20
 */

@Getter
public class ChannelListener implements Listener {

    private final PacketManager packetManager;
    private final Injector injector;

    @SneakyThrows
    public ChannelListener(PacketManager packetManager) {
        this.packetManager = packetManager;
        //*
        Bukkit.getPluginManager().registerEvents(this, packetManager.getPlugin());
        this.injector = new InjectorFactory(ServerUtil.getGameVersion()).buildInjector();
        this.injector.injectReader();
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent e) {
        //this.injector.injectPlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        this.injector.uninjectPlayer(e.getPlayer().getUniqueId());
        Bukkit.getConsoleSender().sendMessage(Chat.translate("&r[&bPacket&r] &aSuccessfully &cdisinjected&r from player &r" + e.getPlayer().getName()));
    }



}
