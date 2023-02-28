package cc.ghast.packet;

import ac.artemis.packet.spigot.ArtemisSpigotApi;
import ac.artemis.packet.spigot.protocol.ProtocolRepository;
import cc.ghast.packet.compat.HookManager;
import cc.ghast.packet.chain.ChainManager;
import cc.ghast.packet.listener.ChannelListener;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

/**
 * @author Ghast
 * @since 24-Apr-20
 */

@Getter
public enum PacketManager {

    INSTANCE;

    private Plugin plugin;
    private ChainManager manager;
    private ChannelListener listener;
    private HookManager hookManager;
    private ProtocolRepository repository;
    private ArtemisSpigotApi api;

    public void init(Plugin plugin) {
        this.plugin = plugin;

        this.api = new ArtemisSpigotApi(plugin);
        ac.artemis.packet.PacketManager.setApi(api);
        // Plugin startup logic
        this.repository = new ProtocolRepository(plugin);
        repository.create();
        api.create();

        this.manager = new ChainManager();
        this.hookManager = new HookManager();
        this.listener = new ChannelListener(this);

    }

    public void destroy() {
        // Plugin shutdown logic
        repository.dispose();
        api.dispose();

        this.repository = null;
        this.api = null;
    }

    public void info(String log) {
        plugin.getLogger().info(log);
    }
    public void fatal(String log) {
        plugin.getLogger().severe(log);
    }
}
