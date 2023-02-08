package ac.artemis.core.v5.emulator.datawatcher.registry;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataWatcherRegistry;
import ac.artemis.core.v5.emulator.datawatcher.serializer.*;

public class DataWatcherRegistry_1_13 extends DataWatcherRegistry {
    public DataWatcherRegistry_1_13(PlayerData data) {
        super(data);
    }

    @Override
    public AbstractDataSerializer<?>[] setValues() {
        return new AbstractDataSerializer[]{
                new ByteDataSerializer(data),
                new VarIntDataSerializer(data),
                new FloatDataSerializer(data),
                new StringDataSerializer(data),
                new TextComponentDataSerializer(data),
                new ItemStackSerializer(data),
                new BooleanDataSerializer(data),
                new RotationsDataSerializer(data),
                new BlockPosSerializer(data),
                new OptionalBlockPosSerializer(data),
                new DirectionDataSerializer(data),
                new OptionalUUIDSerializer(data),
                new OptionalBlockStateSerializer(data),
                new CompoundTagDataSerializer(data),
                null, //new ParticleDataSerializer(data), Free up performance for useless thing
                null, //new VillagerDataSerializer(data),
                new OptionalVarIntDataSerializer(data),
                new PoseDataSerializer(data)
        };
    }
}
