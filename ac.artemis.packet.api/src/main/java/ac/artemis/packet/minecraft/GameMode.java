package ac.artemis.packet.minecraft;

/**
 * The wrapped GameMode enum to circumvent bukkit and offer support for alternate versions.
 */
public enum GameMode {
    /**
     * Creative game mode.
     */
    CREATIVE,
    /**
     * Survival game mode.
     */
    SURVIVAL,
    /**
     * Adventure game mode.
     */
    ADVENTURE,
    /**
     * Spectator game mode.
     */
    SPECTATOR;

    /**
     * Gets GameMode id.
     *
     * @return the id
     */
    public int getId() {
        return ordinal();
    }
}
