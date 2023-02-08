package ac.artemis.core.v5.emulator.villager.profession.impl;

import ac.artemis.core.v5.emulator.villager.profession.VillagerProfession;
import ac.artemis.core.v5.emulator.villager.profession.VillagerProfessionRegistry;
import static ac.artemis.core.v5.emulator.villager.profession.VillagerProfession.*;

public class VillagerProfessionRegistry_1_16 extends VillagerProfessionRegistry {
    @Override
    public VillagerProfession[] setValues() {
        return new VillagerProfession[] {
                NONE,
                ARMORER,
                BUTCHER,
                CARTOGRAPHER,
                CLERIC,
                FARMER,
                FISHERMAN,
                FLETCHER,
                LEATHERWORKER,
                LIBRARIAN,
                MASON,
                NITWIT,
                SHEPHERD,
                TOOLSMITH,
                WEAPONSMITH
        };
    }
}
