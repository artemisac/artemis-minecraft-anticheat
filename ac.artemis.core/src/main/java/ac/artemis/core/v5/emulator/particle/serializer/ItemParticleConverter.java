package ac.artemis.core.v5.emulator.particle.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.serializer.AbstractDataSerializer;
import ac.artemis.core.v5.emulator.particle.BlockParticle;
import ac.artemis.core.v5.emulator.particle.ItemParticle;
import ac.artemis.core.v5.emulator.particle.Particle;
import ac.artemis.core.v5.emulator.particle.type.Particles;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class ItemParticleConverter extends AbstractParticleConverter {
    public ItemParticleConverter(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Particle value) {
        assert value instanceof ItemParticle : "You must provide an item particle! (got: " + value.getType() + ")";
        buf.writeItem(((ItemParticle) value).getItemStack());
    }

    @Override
    public Particle read(Particles type, ProtocolByteBuf buf) {
        return new ItemParticle(type, buf.readItem());
    }

    @Override
    public Particle copyValue(Particle value) {
        return value;
    }

}
