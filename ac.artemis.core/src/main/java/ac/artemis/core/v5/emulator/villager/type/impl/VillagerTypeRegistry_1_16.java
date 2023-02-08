package ac.artemis.core.v5.emulator.villager.type.impl;

import ac.artemis.core.v5.emulator.villager.type.VillagerType;
import ac.artemis.core.v5.emulator.villager.type.VillagerTypeRegistry;

import static ac.artemis.core.v5.emulator.villager.type.VillagerType.*;

public class VillagerTypeRegistry_1_16 extends VillagerTypeRegistry {
    @Override
    public VillagerType[] setValues() {
        return new VillagerType[] {
                DESERT,
                JUNGLE,
                PLAINS,
                SAVANNA,
                SNOW,
                SWAMP,
                TAIGA
        };
    }
}
