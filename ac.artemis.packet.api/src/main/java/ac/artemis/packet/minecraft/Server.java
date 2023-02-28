package ac.artemis.packet.minecraft;

import ac.artemis.packet.minecraft.config.Configuration;
import ac.artemis.packet.minecraft.entity.Console;
import ac.artemis.packet.minecraft.entity.Messager;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.plugin.PluginManager;
import ac.artemis.packet.minecraft.scheduler.Scheduler;
import ac.artemis.packet.minecraft.world.World;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * The wrapped Server instance.
 */
public interface Server {
    /**
     * Broadcasts a specific message to the whole server
     *
     * @param message Message to be sent
     */
    void broadcast(final String message);

    /**
     * Dispatches a command on behalf of a messenger to the server
     *
     * @param sender Sender of the command
     * @param command String serialized command
     */
    void dispatchCommand(final Messager sender, final String command);

    /**
     * @param uuid UUID of the player
     * @return Player wrapper instance of the player
     */
    Player getPlayer(final UUID uuid);

    /**
     * @return Gets collection of online players.
     */
    Collection<Player> getOnlinePlayers();

    /**
     * @return Gets the version of the server
     */
    String getVersion();

    /**
     * @return Gets the plugin manager of the server
     */
    PluginManager getPluginManager();

    /**
     * @return Gets the server scheduler
     */
    Scheduler getScheduler();

    /**
     * @return Gets wrapped console messager
     */
    Console getConsoleSender();

    /**
     * @param name Name of the world
     * @return World wrapper of the corresponding world
     */
    World getWorld(final String name);

    /**
     * @return Collection of worlds available
     */
    List<World> getWorlds();

    /**
     * @param path Path to the configuration file
     * @return Wrapped configuration at said path
     */
    Configuration getConfig(final String path);


    /**
     * Virtual wrapped server instance
     *
     * @return the server
     */
    static Server v() {
        return ServerInstance.getServer();
    }

    /**
     * Stored reference to the server to not break stuff / make stuff
     * messy.
     */
    class ServerInstance {
        private static Server server;

        /**
         * Gets server.
         *
         * @return the server
         */
        public static Server getServer() {
            return server;
        }

        /**
         * Sets server.
         *
         * @param v the server
         */
        public static void setServer(Server v) {
            server = v;
        }
    }
}
