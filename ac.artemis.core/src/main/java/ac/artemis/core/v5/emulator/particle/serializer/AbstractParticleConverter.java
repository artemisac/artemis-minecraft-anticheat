package ac.artemis.core.v5.emulator.particle.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataSerializer;
import ac.artemis.core.v5.emulator.particle.Particle;
import ac.artemis.core.v5.emulator.particle.type.Particles;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public abstract class AbstractParticleConverter implements DataSerializer<Particle> {
    protected final PlayerData data;

    public AbstractParticleConverter(PlayerData data) {
        this.data = data;
    }

    public abstract Particle read(Particles type, ProtocolByteBuf buf);
}
