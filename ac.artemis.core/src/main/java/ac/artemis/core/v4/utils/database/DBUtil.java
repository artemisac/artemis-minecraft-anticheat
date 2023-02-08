package ac.artemis.core.v4.utils.database;

import java.io.Closeable;

/**
 * @Author Alpha
 * @Since 17/02/2020
 * Reaper Industries LLC © 2020
 */
public class DBUtil {

    public static void close(Closeable... closeables) {
        try {
            for (Closeable closeable : closeables) if (closeable != null) closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(AutoCloseable... closeables) {
        try {
            for (AutoCloseable closeable : closeables) if (closeable != null) closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
