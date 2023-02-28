package cc.ghast.packet.wrapper.bukkit;

import lombok.Getter;

@Getter
public enum GameMode {
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3),
    HARDCORE(8),
    UNDEFINED(-1)
    ;

    private final int id;

    GameMode(int id) {
        this.id = id;
    }

    public boolean equals(org.bukkit.GameMode gameMode) {
        return gameMode.name().toUpperCase().equalsIgnoreCase(this.name().toUpperCase());
    }

    public static GameMode getById(int id) {
        for (GameMode gm : values()) {
            if (gm.getId() == id) {
                return gm;
            }
        }
        return SURVIVAL;
    }
}
