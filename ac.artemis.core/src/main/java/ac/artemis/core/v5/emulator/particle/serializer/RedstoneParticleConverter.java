package ac.artemis.core.v5.emulator.particle.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.serializer.AbstractDataSerializer;
import ac.artemis.core.v5.emulator.particle.Particle;
import ac.artemis.core.v5.emulator.particle.RedstoneParticle;
import ac.artemis.core.v5.emulator.particle.type.Particles;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class RedstoneParticleConverter extends AbstractParticleConverter {
    public RedstoneParticleConverter(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Particle value) {
        assert value instanceof RedstoneParticle : "You must provide a redstone particle! (got: " + value.getType() + ")";
        final RedstoneParticle particle = (RedstoneParticle) value;
        buf.writeFloat(particle.getRed());
        buf.writeFloat(particle.getGreen());
        buf.writeFloat(particle.getBlue());
        buf.writeFloat(particle.getAlpha());
    }

    @Override
    public Particle read(Particles type, ProtocolByteBuf buf) {
        final float red = buf.readFloat();
        final float green = buf.readFloat();
        final float blue = buf.readFloat();
        final float alpha = buf.readFloat();

        return new RedstoneParticle(type, red, green, blue, alpha);
    }

    @Override
    public Particle copyValue(Particle value) {
        return value;
    }
}
