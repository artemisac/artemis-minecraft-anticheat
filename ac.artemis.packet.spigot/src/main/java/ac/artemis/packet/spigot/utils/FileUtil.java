package ac.artemis.packet.spigot.utils;

import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class FileUtil {
    public File[] getFiles(final File file) {
        if (!file.exists()) {
            file.getParentFile().mkdir();
        }

        return file.getParentFile().listFiles();
    }
}
