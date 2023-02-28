package ac.artemis.packet.minecraft.scheduler;

import ac.artemis.packet.minecraft.Wrapped;

public interface Scheduler extends Wrapped {
    /**
     * Runs a specific task on the main server thread
     *
     * @param runnable Runnable task to be completed
     */
    void runTask(final Runnable runnable);

    /**
     * Runs a specific task on the main server thread asynchronously
     *
     * @param runnable Runnable task to be completed
     * @param delay Delay before starting task
     * @param interval Interval between task repetitions
     */
    void runTaskTimerAsynchronously(Runnable runnable, long delay, long interval);

    void scheduleAsyncRepeatingTask(Runnable runnable, long delay, long interval);
}
