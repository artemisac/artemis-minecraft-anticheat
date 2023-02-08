package ac.artemis.core.v5.config;

import ac.artemis.packet.minecraft.config.Configuration;

import java.lang.reflect.Field;

public abstract class Config {

    private final Configuration configuration;

    public Config(final Configuration configuration) {
        this.configuration = configuration;
    }

    public final void init() {
        for (final Field declaredField : this.getClass().getDeclaredFields()) {
            if (!declaredField.isAccessible()) {
                declaredField.setAccessible(true);
            }

            if (!declaredField.isAnnotationPresent(ConfigField.class)) {
                continue;
            }

            final ConfigField configField = declaredField.getAnnotation(ConfigField.class);

            final String name = configField.value();
            Object value = configuration.get(name);

            try {
                if (value == null) {
                    final Object var = declaredField.get(this);

                    if (var != null) {
                        configuration.set(name, var);
                        value = var;
                    }
                } else {
                    declaredField.set(this, value);
                }
            }

            catch (final Exception e) {
                continue;
            }
        }

        this.configuration.save();
    }

    public final void disinit() {
        for (final Field declaredField : this.getClass().getDeclaredFields()) {
            if (!declaredField.isAccessible()) {
                declaredField.setAccessible(true);
            }

            if (!declaredField.isAnnotationPresent(ConfigField.class)) {
                continue;
            }

            final ConfigField configField = declaredField.getAnnotation(ConfigField.class);

            final String name = configField.value();

            try {
                final Object var = declaredField.get(this);

                configuration.set(name, var);
            } catch (final IllegalAccessException e) {
                continue;
            }
        }

        this.configuration.save();
    }
}
