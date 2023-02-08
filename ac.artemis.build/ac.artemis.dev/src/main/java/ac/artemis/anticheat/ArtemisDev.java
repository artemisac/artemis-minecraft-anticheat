package ac.artemis.anticheat;

import ac.artemis.packet.minecraft.plugin.Plugin;
import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;

public final class ArtemisDev extends JavaPlugin implements Plugin {

    public static String LICENSE = "2150cdfb-c0c8-430d-b958-e1d0ab536ea0";

    @Override
    public void onEnable() {
        // Plugin startup logic
        BukkitArtemis.INSTANCE.load(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        BukkitArtemis.INSTANCE.kill();
    }

    @Override
    public <T> T v() {
        return (T) this;
    }

    @Override
    public String getVersion() {
        return this.getDescription().getVersion();
    }

    @Override
    public List<String> getAuthors() {
        return this.getDescription().getAuthors();
    }

    @Override
    public void rename(String s) {
        try {
            Field pluginName = PluginDescriptionFile.class.getDeclaredField("name");
            pluginName.setAccessible(true);
            pluginName.set(this.getDescription(), s);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hide(boolean b) {
        try {
            Field pluginsList = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
            pluginsList.setAccessible(true);
            List<Plugin> updater = (List<Plugin>) pluginsList.get(Bukkit.getPluginManager());

            if (b) {
                if (updater.contains(this))
                    return;

                updater.add(this);
            } else {
                updater.remove(this);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
