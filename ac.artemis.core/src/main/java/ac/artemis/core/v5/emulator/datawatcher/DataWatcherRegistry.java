package ac.artemis.core.v5.emulator.datawatcher;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.serializer.AbstractDataSerializer;
import ac.artemis.core.v5.utils.template.Registry;

public abstract class DataWatcherRegistry extends Registry<AbstractDataSerializer<?>> {
    protected final PlayerData data;

    public DataWatcherRegistry(PlayerData data) {
        this.data = data;
    }
}
