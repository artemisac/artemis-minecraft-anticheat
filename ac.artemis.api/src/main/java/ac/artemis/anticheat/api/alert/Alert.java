package ac.artemis.anticheat.api.alert;

import ac.artemis.anticheat.api.check.CheckInfo;

import java.util.UUID;

public interface Alert {
    /**
     * Returns the severity of the alert. An alert can either be a verbose or a violation.
     * @return Severity of alert
     */
    Severity getSeverity();

    /**
     * UUID of the player that has flagged
     * @return The UUID
     */
    UUID getUuid();

    /**
     * Information about the check in question
     * @return Information about the checkinformation
     */
    CheckInfo getCheck();

    /**
     * Violation digit
     * @return Integer representing the above
     */
    int count();

    /**
     * Value that corresponds to a Minecraft chat message
     * @return Minecraft chat message value
     */
    String toMinecraftMessage();

    /**
     * Allows you to cancel the event
     */
    void setCancelled(boolean cancelled);

    /**
     * Checks if the event is cancelled
     */
    boolean isCancelled();

}
