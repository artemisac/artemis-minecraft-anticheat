package ac.artemis.core.v5.utils;

import lombok.experimental.UtilityClass;

import java.io.InputStream;

@UtilityClass
public class ClassUtil {
    public boolean isClassExist(final String name) {
        try {
            Class.forName(name);
            return true;
        } catch (final Throwable e) {
            return false;
        }
    }

    public InputStream getFileFromLoader(final Class<?> clazz, final String name) {
        return clazz.getClassLoader().getResourceAsStream(name);
    }
}
