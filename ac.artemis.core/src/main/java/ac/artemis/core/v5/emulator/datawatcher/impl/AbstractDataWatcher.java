package ac.artemis.core.v5.emulator.datawatcher.impl;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataWatcherReader;
import ac.artemis.core.v5.emulator.datawatcher.DataWatcherRegistry;
import ac.artemis.core.v5.emulator.datawatcher.DataWatcherRegistryFactory;
import ac.artemis.core.v5.emulator.datawatcher.serializer.AbstractDataSerializer;

public abstract class AbstractDataWatcher implements DataWatcherReader {
    protected final PlayerData data;
    private final DataWatcherRegistry registry;

    public AbstractDataWatcher(PlayerData data) {
        this.data = data;
        this.registry = new DataWatcherRegistryFactory()
                .setData(data)
                .build();
    }

    public AbstractDataSerializer<?> getSerializer(int index) {
        return registry.get(index);
    }
}
