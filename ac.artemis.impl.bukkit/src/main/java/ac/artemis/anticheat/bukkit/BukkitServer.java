package ac.artemis.anticheat.bukkit;

import ac.artemis.anticheat.bukkit.entity.BukkitPlayer;
import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.config.Configuration;
import ac.artemis.packet.minecraft.entity.Console;
import ac.artemis.packet.minecraft.entity.Messager;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.plugin.PluginManager;
import ac.artemis.packet.minecraft.scheduler.Scheduler;
import ac.artemis.packet.minecraft.world.World;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BukkitServer implements Server {
    private final transient JavaPlugin plugin;
    private final transient PluginManager pluginManager;
    private final transient Scheduler scheduler;
    private final transient Console console;

    public BukkitServer(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pluginManager = new BukkitPluginManager(Bukkit.getPluginManager());
        this.scheduler = new BukkitScheduler(Bukkit.getScheduler(), plugin);
        this.console = new BukkitConsole(Bukkit.getConsoleSender());
    }

    @Override
    public void broadcast(String s) {
        Bukkit.broadcastMessage(s);
    }

    @Override
    public void dispatchCommand(Messager messager, String s) {
        Bukkit.dispatchCommand(messager.v(), s);
    }

    @Override
    public Player getPlayer(UUID uuid) {
        return new BukkitPlayer(Bukkit.getPlayer(uuid));
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(BukkitPlayer::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getVersion() {
        return Bukkit.getVersion();
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public Console getConsoleSender() {
        return console;
    }

    @Override
    public World getWorld(String s) {
        return new BukkitWorld(Bukkit.getWorld(s));
    }

    @Override
    public List<World> getWorlds() {
        return Bukkit.getWorlds()
                .stream()
                .map(BukkitWorld::new)
                .collect(Collectors.toList());
    }

    @Override
    public Configuration getConfig(String s) {
        return new BukkitConfiguration(s, plugin);
    }
}
