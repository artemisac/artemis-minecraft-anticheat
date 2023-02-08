package ac.artemis.core.v5.emulator.villager;

import ac.artemis.core.v5.emulator.villager.profession.VillagerProfession;
import ac.artemis.core.v5.emulator.villager.type.VillagerType;
import lombok.Getter;

@Getter
public class VillagerData {
    private final VillagerType type;
    private final VillagerProfession profession;
    private final int level;

    public VillagerData(VillagerType type, VillagerProfession profession, int level) {
        this.type = type;
        this.profession = profession;
        this.level = level;
    }
}
