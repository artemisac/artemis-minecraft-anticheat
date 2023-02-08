package ac.artemis.anticheat.api.alert;


import java.util.UUID;

public interface Punishment {
    /**
     * Returns the in-game username of the player getting banned. Practical to run commands and stuff.
     * @return In-game username of the player
     */
    String getUsername();

    /**
     * Returns the UUID of the player. No way in hell would we let anything go without it's UUID.
     * @return UUID
     */
    UUID getUuid();

    /**
     * Computed ban ID of the ban. The format is as follows: YYY-??????-???, year is computed as currentYear - 1900.
     * In java, such year corresponds to Calendar.getInstance().getTime().getYear();
     * @return String ban id
     */
    String getBanId();

    /**
     * Precise time at which the ban was called.
     * @return Precise time in Epoch
     */
    long getTimestamp();

    /**
     * Allows you to cancel the event
     */
    void setCancelled(boolean cancelled);

    /**
     * Checks if the event is cancelled
     */
    boolean isCancelled();
}
