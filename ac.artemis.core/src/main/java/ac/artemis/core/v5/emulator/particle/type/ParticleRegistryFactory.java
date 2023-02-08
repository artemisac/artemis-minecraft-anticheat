package ac.artemis.core.v5.emulator.particle.type;

import ac.artemis.core.v5.emulator.particle.type.impl.ParticleRegistry_1_16;
import ac.artemis.core.v5.utils.interf.Factory;
import ac.artemis.packet.protocol.ProtocolVersion;

public class ParticleRegistryFactory implements Factory<ParticleRegistry> {
    private ProtocolVersion version;

    public ParticleRegistryFactory setData(final ProtocolVersion version) {
        this.version = version;
        return this;
    }

    @Override
    public ParticleRegistry build() {
        assert version != null : "Version cannot be set to null!";
        return new ParticleRegistry_1_16();
    }
}
