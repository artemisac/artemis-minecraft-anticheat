package ac.artemis.core.v5.emulator.villager.profession;

import ac.artemis.core.v5.utils.interf.Factory;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.core.v5.emulator.villager.profession.impl.VillagerProfessionRegistry_1_16;

public class VillagerProfessionRegistryFactory implements Factory<VillagerProfessionRegistry> {
    private ProtocolVersion version;

    public VillagerProfessionRegistryFactory setVersion(ProtocolVersion version) {
        this.version = version;
        return this;
    }

    @Override
    public VillagerProfessionRegistry build() {
        assert version != null : "Version cannot be set to null!";
        return new VillagerProfessionRegistry_1_16();
    }
}
