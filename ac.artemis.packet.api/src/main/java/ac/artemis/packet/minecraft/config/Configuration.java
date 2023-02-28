package ac.artemis.packet.minecraft.config;

import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.minecraft.world.Location;

import java.util.List;

public interface Configuration {
    void load();

    void save();

    String getString(String path);

    String getStringOrDefault(String path, String dflt);

    int getInt(String path);

    boolean getBoolean(String path);

    List<String> getStringList(String path);

    ItemStack getItemStack(String path);

    double getDouble(String path);

    Long getLong(String path);

    <T> T get(String path);

    void set(String path, Object value);

    void setLocation(String path, Location location);

    Location getLocation(String path);

    java.io.File getFile();
}
