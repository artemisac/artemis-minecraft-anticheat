package ac.artemis.core.v5.emulator.villager.type;

import lombok.Getter;

@Getter
public enum VillagerType {
    DESERT("desert"),
    JUNGLE("jungle"),
    PLAINS("plains"),
    SAVANNA("savanna"),
    SNOW("snow"),
    SWAMP("swamp"),
    TAIGA("taiga");

    private final String biomeName;

    VillagerType(String biomeName) {
        this.biomeName = biomeName;
    }
}
