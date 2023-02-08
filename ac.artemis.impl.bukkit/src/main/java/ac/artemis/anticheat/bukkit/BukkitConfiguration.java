package ac.artemis.anticheat.bukkit;

import ac.artemis.packet.minecraft.Minecraft;
import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.Unsafe;
import ac.artemis.packet.minecraft.config.Configuration;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.core.v4.utils.chat.Chat;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.List;

public class BukkitConfiguration implements Configuration {
    private final JavaPlugin plugin;
    private final String name;
    private File file;
    private YamlConfiguration config;

    public BukkitConfiguration(String name, JavaPlugin plugin) {
        this.plugin = plugin;
        this.name = name;
        load();
    }

    @Override
    public void load() {
        file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdir();
            } else {
                file.getAbsoluteFile().getParentFile().mkdir();
            }

            final InputStream bufferedInputStream = this.getClass().getClassLoader().getResourceAsStream(name);
            try {
                byte[] buffer = new byte[bufferedInputStream.available()];
                bufferedInputStream.read(buffer);

                final OutputStream outStream = new FileOutputStream(file);
                outStream.write(buffer);
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (Exception ex) {
            Chat.sendConsoleMessage("&cError loading configuration file " + name);
            ex.printStackTrace();
        }
    }


    @Override
    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            Chat.sendConsoleMessage("&cError saving config file " + name);
            e.printStackTrace();
        }
    }

    @Override
    public String getString(String path) {
        return getConfig().getString(path);
    }

    @Override
    public String getStringOrDefault(String path, String dflt) {
        final String var = getConfig().getString(path);

        if (var == null) {
            set(path, dflt);
            save();
        }

        return dflt;
    }

    @Override
    public int getInt(String path) {
        return getConfig().getInt(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return getConfig().getBoolean(path);
    }

    @Override
    public List<String> getStringList(String path) {
        return getConfig().getStringList(path);
    }

    @Override
    public ItemStack getItemStack(String path) {
        return Unsafe.v().fromBukkitItem(getConfig().getItemStack(path));
    }

    @Override
    public double getDouble(String path) {
        return getConfig().getDouble(path);
    }

    @Override
    public Long getLong(String path) {
        return getConfig().getLong(path);
    }

    @Override
    public <T> T get(String path) {
        final Object object = getConfig().get(path);

        if (object != null) return (T) getConfig().get(path);
        return null;
    }

    @Override
    public void set(String path, Object value) {
        getConfig().set(path, value);
    }

    public YamlConfigurationOptions getOptions() {
        return getConfig().options();
    }

    @Override
    public void setLocation(String path, Location location) {
        getConfig().set(path + ".X", location.getX());
        getConfig().set(path + ".Y", location.getY());
        getConfig().set(path + ".Z", location.getZ());
        getConfig().set(path + ".WORLD", location.getWorld().getName());
        getConfig().set(path + ".YAW", location.getYaw());
        getConfig().set(path + ".PITCH", location.getPitch());
        save();
    }

    @Override
    public Location getLocation(String path) {
        return Minecraft.v().createLocation(
                Server.v().getWorld(getConfig().getString(path + ".WORLD")),
                getConfig().getDouble(path + ".X"),
                getConfig().getDouble(path + ".Y"),
                getConfig().getDouble(path + ".Z"),
                getConfig().getLong(path + ".YAW"),
                getConfig().getLong(path + ".PITCH")
        );
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    @Override
    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
