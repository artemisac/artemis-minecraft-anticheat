package cc.ghast.packet;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Ghast
 * @since 19/08/2020
 * Artemis © 2020
 */
public class PacketPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PacketManager.INSTANCE.init(this);
    }

    @Override
    public void onDisable() {
        PacketManager.INSTANCE.destroy();
    }
}
