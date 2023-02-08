package ac.artemis.core;

import ac.artemis.packet.minecraft.plugin.Plugin;
import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.core.v4.api.APIManager;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;


public class ArtemisMain extends JavaPlugin implements Plugin {

    private File dataFolderLoader;
    private APIManager apiManager;

    public ArtemisMain(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    public ArtemisMain() {
    }

    /**
     * Method called when plugin is initialized; This directly launches the API manager without causing any issues
     *
     * @see JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {
        this.dataFolderLoader = this.getDataFolder();
        this.apiManager = BukkitArtemis.INSTANCE.load(this);

    }

    /**
     * Method called when plugin is disitialized. This directly disables the anticheat without any important issues.
     *
     * @see JavaPlugin#onDisable()
     */
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
