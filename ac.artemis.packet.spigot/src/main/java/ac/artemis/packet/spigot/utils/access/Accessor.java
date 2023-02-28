package ac.artemis.packet.spigot.utils.access;

import org.bukkit.plugin.Plugin;

public abstract class Accessor {
    protected final Plugin plugin;

    public Accessor(Plugin plugin) {
        this.plugin = plugin;
    }

    public abstract void create();
    public abstract void dispose();
}
