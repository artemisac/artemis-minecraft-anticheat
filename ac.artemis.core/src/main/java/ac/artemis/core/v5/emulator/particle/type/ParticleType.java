package ac.artemis.core.v5.emulator.particle.type;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.serializer.AbstractDataSerializer;
import ac.artemis.core.v5.emulator.particle.Particle;
import ac.artemis.core.v5.emulator.particle.serializer.*;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Getter
public enum ParticleType {
    BASIC(BasicParticleConverter.class),
    BLOCK(BlockParticleConverter.class),
    DUST(RedstoneParticleConverter.class),
    ITEM(ItemParticleConverter.class);

    private final Class<? extends AbstractParticleConverter> serializer;
    private Constructor<? extends AbstractParticleConverter> constructor;

    
    ParticleType(Class<? extends AbstractParticleConverter> serializer) {
        this.serializer = serializer;
        try {
            this.constructor = serializer.getConstructor(PlayerData.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    
    public AbstractParticleConverter getSerializer(final PlayerData data) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return constructor.newInstance(data);
    }
}
