package ac.artemis.core.v5.emulator.particle.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataSerializer;
import ac.artemis.core.v5.emulator.datawatcher.serializer.AbstractDataSerializer;
import ac.artemis.core.v5.emulator.particle.BasicParticle;
import ac.artemis.core.v5.emulator.particle.BlockParticle;
import ac.artemis.core.v5.emulator.particle.Particle;
import ac.artemis.core.v5.emulator.particle.type.Particles;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class BasicParticleConverter extends AbstractParticleConverter {
    public BasicParticleConverter(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Particle value) {
        assert value instanceof BasicParticle : "You must provide a basic particle! (got: " + value.getType() + ")";
    }

    @Override
    public Particle read(Particles type, ProtocolByteBuf buf) {
        return new BasicParticle(type);
    }

    @Override
    public Particle copyValue(Particle value) {
        return value;
    }
}
