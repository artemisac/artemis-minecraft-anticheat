package ac.artemis.anticheat.bukkit;

import ac.artemis.anticheat.api.ArtemisServerClient;
import ac.artemis.anticheat.bukkit.commands.CommandManager;
import ac.artemis.anticheat.bukkit.logging.BukkitNotificationListener;
import ac.artemis.packet.minecraft.Minecraft;
import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.Unsafe;
import ac.artemis.packet.minecraft.console.ConsoleReader;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.plugin.Plugin;
import ac.artemis.anticheat.bukkit.listener.BukkitListener;
import ac.artemis.anticheat.bukkit.listener.PlayerDataListener;
import ac.artemis.anticheat.bukkit.nms.v1_7.NMS_v1_7_10;
import ac.artemis.anticheat.bukkit.nms.v1_8.NMS_v1_8;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.APIManager;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.nms.minecraft.INMS;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.ArtemisSpigotAPI;
import ac.artemis.core.v5.reflect.ReflectBridge;
import ac.artemis.anticheat.bukkit.reflect.StandardBukkitReflection;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.utils.ServerUtil;
import fr.minuskube.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Ghast
 * @since 06-Mar-20
 */
public enum BukkitArtemis implements Artemis {
    INSTANCE;

    // Plugin
    private JavaPlugin plugin;
    private transient Plugin wrappedPlugin;
    // API Manager
    private APIManager api;
    private InventoryManager inventoryManager;
    private PlayerDataListener playerDataListener;
    private BukkitNotificationListener bukkitNotificationListener;

    /**
     * Method which loads the plugin as well as all the necessity. This enum serves as the proper way to access the
     * path without causing any issues. Extremely important to keep in mind this should not be modified nor accessed.
     *
     * @param pl JavaPlugin variable
     * @return itself
     */
    public APIManager load(Plugin pl) {
        this.plugin = pl.v();
        this.wrappedPlugin = pl;

        Minecraft.MinecraftInstance.setMinecraft(new BukkitMinecraft());
        Server.ServerInstance.setServer(new BukkitServer(plugin));
        Unsafe.MinecraftInstance.setDeprecated(new BukkitUnsafe());
        Artemis.ArtemisInstance.setInstance(this);
        ReflectBridge.init(new StandardBukkitReflection());
        Material.instance.setWrapper(new Material.MaterialWrapper() {
            @Override
            public Material getMaterial(String s) {
                return new BukkitMaterial(org.bukkit.Material.getMaterial(s));
            }
        });

        Chat.sendConsoleMessage(
                String.format(
                        "&7[&bArtemis&7] Initiating Artemis v%s on VM (%s)",
                        plugin.getDescription().getVersion(),
                        System.getProperty("java.vm.name")
                )
        );

        if (!new File(plugin.getDataFolder(), "data").exists()) {
            Chat.sendConsoleMessage(
                    "&b[&r&l!&b]&7&m-----------------------------------------------------------------------------&b[&r&l!&b]",
                    "",
                    "&7Welcome to Artemis Anticheat! We will proceed the installation process. By installing the anticheat, ",
                    "&7you automatically agree to &bour terms and service &r(&bhttps://artemis.ac/tos.pdf&r)&7.",
                    "&7We will &benforce these as strictly as possible&7. Make sure to always be in compliance with said terms. ",
                    "",
                    "&7To continue, &bplease type &r'&bagree&r'",
                    "&b>&r>&b>\n"
            );

            try {
                final ConsoleReader scan = ReflectBridge.v().getReader();

                while (true) {
                    final String output = scan.readLine(null, null);

                    if (!output.contains("gree")) {
                        Chat.sendConsoleMessage("&r[&c\uD800\uDD02&r] &cFailed&r to agree to the terms and service. &r"
                                + output + "&c is not 'agree'...");
                        if (true) continue;
                        Bukkit.getPluginManager().disablePlugin(plugin);
                        return null;
                    }
                    //scan.reset();

                    Chat.sendConsoleMessage(
                            "&r[&a✓&r] &aSuccessfully&r validated terms and service!",
                            "&b[&r&l!&b]&7&m-----------------------------------------------------------------------------&b[&r&l!&b]"
                    );
                    break;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        this.initNms();

        ArtemisServerClient.setAPI(new ArtemisSpigotAPI());
        cc.ghast.packet.PacketManager.INSTANCE.init(plugin);

        this.api = new APIManager(this);
        this.api.init(InitializeAction.START);

        /*
         * GUI management
         */
        this.inventoryManager = new InventoryManager(plugin);
        this.inventoryManager.init();

        /*
         * Handles the player data injection and handling
         */
        this.playerDataListener = new PlayerDataListener(plugin);
        this.playerDataListener.start();

        /*
         * Bukkit notification provider (bridges the alerts from artemis
         * to messages)
         */
        this.bukkitNotificationListener = new BukkitNotificationListener();
        this.bukkitNotificationListener.start();

        /*
         * Bukkit memes for fake Buzzy buzz thing
         */
        new BukkitListener(plugin);

        /*
         * Command Manager injection
         */
        final CommandManager commandManager = new CommandManager(this);
        commandManager.init(InitializeAction.START);
        Artemis.v().getApi().injectManager(commandManager);

        return api;
    }

    public void kill() {
        this.playerDataListener.end();
        this.bukkitNotificationListener.end();

        this.api.disinit(ShutdownAction.STOP);

        ReflectBridge.kill();
    }

    private void initNms() {
        final INMS inms;
        switch (ServerUtil.getGameVersion()) {
            case V1_7_10:
                inms = new NMS_v1_7_10();
                break;
            case V1_8_9:
                inms = new NMS_v1_8();
                break;
            /*case V1_9:
                inms = new NMS_v1_9();
                break;
            case V1_11:
                inms = new NMS_v1_11();
                break;
            case V1_12:
                inms = new NMS_v1_12();
                break;
            case V1_13_2:
                inms = new NMS_v1_13_2();
                break;
            case V1_15:
            case V1_15_2:
                inms = new NMS_v1_15();
                break;
            case V1_16_5:
                inms = new NMS_v1_16_R3();
                break;*/
            default:
                Chat.sendConsoleMessage(
                        "&b[&r&l!&b]&7&m-----------------------------------------------------------------------------&b[&r&l!&b]",
                        "",
                        "&4c&l/!\\ Fatal error! ",
                        "&cFailed to hook into native bukkit services... ",
                        "&cPlease ensure you are on one of the following versions: ",
                        "&7- " + Arrays.stream(ProtocolVersion.values())
                                .filter(e -> e.isBelow(ProtocolVersion.V_1_17))
                                .map(ProtocolVersion::getServerVersion)
                                .collect(Collectors.joining(",", "&6", "&7")),
                        "",
                        "&b[&r&l!&b]&7&m-----------------------------------------------------------------------------&b[&r&l!&b]"
                );
                Server.v().getPluginManager().kill(wrappedPlugin);
                return;
        }

        NMSManager.setInms(inms);
    }

    public Plugin getPlugin() {
        return wrappedPlugin;
    }

    public APIManager getApi() {
        return api;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public BukkitNotificationListener getBukkitNotificationListener() {
        return bukkitNotificationListener;
    }
}
