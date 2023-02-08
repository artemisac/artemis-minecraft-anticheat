package ac.artemis.core.v5.emulator.villager.type;

import ac.artemis.core.v5.emulator.villager.type.impl.VillagerTypeRegistry_1_16;
import ac.artemis.core.v5.utils.interf.Factory;
import ac.artemis.packet.protocol.ProtocolVersion;

public class VillagerTypeRegistryFactory implements Factory<VillagerTypeRegistry> {
    private ProtocolVersion version;

    public VillagerTypeRegistryFactory setVersion(ProtocolVersion version) {
        this.version = version;
        return this;
    }

    @Override
    public VillagerTypeRegistry build() {
        assert version != null : "Version cannot be set to null!";
        return new VillagerTypeRegistry_1_16();
    }
}
