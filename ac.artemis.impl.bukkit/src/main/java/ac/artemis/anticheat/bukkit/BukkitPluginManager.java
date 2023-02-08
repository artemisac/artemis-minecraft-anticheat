package ac.artemis.anticheat.bukkit;

import ac.artemis.packet.minecraft.AbstractWrapper;
import ac.artemis.packet.minecraft.plugin.Plugin;
import ac.artemis.packet.minecraft.plugin.PluginManager;

public class BukkitPluginManager extends AbstractWrapper<org.bukkit.plugin.PluginManager> implements PluginManager {
    public BukkitPluginManager(org.bukkit.plugin.PluginManager wrapper) {
        super(wrapper);
    }

    @Override
    public void kill(Plugin plugin) {
        wrapper.disablePlugin(plugin.v());
    }
}
