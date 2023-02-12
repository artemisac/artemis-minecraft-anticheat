package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.particle.Particle;
import ac.artemis.core.v5.emulator.particle.type.ParticleRegistry;
import ac.artemis.core.v5.emulator.particle.type.ParticleRegistryFactory;
import ac.artemis.core.v5.emulator.particle.type.Particles;
import ac.artemis.core.v5.emulator.particle.type.impl.ParticleRegistry_1_16;
import ac.artemis.core.v5.emulator.pose.Pose;
import cc.ghast.packet.buffer.ProtocolByteBuf;

import java.lang.reflect.InvocationTargetException;

public class ParticleDataSerializer extends AbstractDataSerializer<Particle> {
    public ParticleDataSerializer(PlayerData data) {
        super(data);
        this.particleRegistry = new ParticleRegistryFactory()
                .setData(data.getVersion())
                .build();
    }

    private final ParticleRegistry particleRegistry;

    @Override
    public void write(ProtocolByteBuf buf, Particle value) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        buf.writeVarInt(particleRegistry.index(value.getType()));
        value.getType().getSerializer(data).write(buf, value);
    }

    @Override
    public Particle read(ProtocolByteBuf buf) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final Particles type = particleRegistry.get(buf.readVarInt());
        return type.getSerializer(data).read(type, buf);
    }

    @Override
    public Particle copyValue(Particle value) {
        return value;
    }
}
