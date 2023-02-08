package ac.artemis.core.v5.emulator.datawatcher.registry;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataWatcherRegistry;
import ac.artemis.core.v5.emulator.datawatcher.serializer.*;

public class DataWatcherRegistry_1_8 extends DataWatcherRegistry {
    public DataWatcherRegistry_1_8(PlayerData data) {
        super(data);
    }

    @Override
    public AbstractDataSerializer<?>[] setValues() {
        return new AbstractDataSerializer[]{
                new ByteDataSerializer(data),
                new ShortDataSerializer(data),
                new IntDataSerializer(data),
                new FloatDataSerializer(data),
                new StringDataSerializer(data),
                new ItemStackSerializer(data),
                new BlockPosSerializer(data),
                new RotationsDataSerializer(data)
        };
    }
}
