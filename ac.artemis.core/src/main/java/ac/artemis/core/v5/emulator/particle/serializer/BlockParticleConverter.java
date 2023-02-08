package ac.artemis.core.v5.emulator.particle.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.serializer.AbstractDataSerializer;
import ac.artemis.core.v5.emulator.particle.BlockParticle;
import ac.artemis.core.v5.emulator.particle.Particle;
import ac.artemis.core.v5.emulator.particle.RedstoneParticle;
import ac.artemis.core.v5.emulator.particle.type.Particles;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class BlockParticleConverter extends AbstractParticleConverter {
    public BlockParticleConverter(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Particle value) {
        assert value instanceof BlockParticle : "You must provide a block particle! (got: " + value.getType() + ")";
        buf.writeVarInt(((BlockParticle) value).getBlockId());
    }

    @Override
    public Particle read(Particles type, ProtocolByteBuf buf) {
        return new BlockParticle(type, buf.readVarInt());
    }

    @Override
    public Particle copyValue(Particle value) {
        return value;
    }
}
