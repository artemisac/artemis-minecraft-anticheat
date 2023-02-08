package ac.artemis.anticheat.sdk;

import java.util.UUID;

public interface FlushListener {
    /**
     * Minecraft servers work on a queued flushing basis in order to limit the quantity of synchronized
     * and thread locked packets from overloading the netty pipeline due to the constant flushing. Flushing
     * can quickly become a performance impacting process, hence it is favorable to keep it limited; Furthermore,
     * flushing benefits better server-client synchronization and ensures that *most the time* packets fall under
     * the same tick. This of course can differ from time to time.
     *
     * This specific method should be called *before* the flushing occurs
     *
     * @param uuid UUID of the player who's pipeline was flushed
     */
    default void onPreFlush(final UUID uuid) {};

    /**
     * Minecraft servers work on a queued flushing basis in order to limit the quantity of synchronized
     * and thread locked packets from overloading the netty pipeline due to the constant flushing. Flushing
     * can quickly become a performance impacting process, hence it is favorable to keep it limited; Furthermore,
     * flushing benefits better server-client synchronization and ensures that *most the time* packets fall under
     * the same tick. This of course can differ from time to time.
     *
     * This specific method should be called *after* the flushing occurs
     *
     * @param uuid UUID of the player who's pipeline was flushed
     */
    default void onPostFlush(final UUID uuid) {};
}
