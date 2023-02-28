package ac.artemis.packet.minecraft.plugin;

import ac.artemis.packet.minecraft.Wrapped;

import java.io.File;
import java.util.List;

public interface Plugin extends Wrapped {
    File getDataFolder();

    String getVersion();

    List<String> getAuthors();

    void rename(final String name);

    void hide(final boolean hide);
}
