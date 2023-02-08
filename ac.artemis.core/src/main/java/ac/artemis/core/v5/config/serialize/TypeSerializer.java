package ac.artemis.core.v5.config.serialize;

import ac.artemis.packet.minecraft.config.Configuration;

public interface TypeSerializer<T> {
    T read(final Configuration config, final String path);

    void write(final Configuration config, final String path, final T value);
}
