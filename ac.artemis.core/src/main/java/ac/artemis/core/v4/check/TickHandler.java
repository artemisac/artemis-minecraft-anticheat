package ac.artemis.core.v4.check;

public interface TickHandler {
    /**
     * Method called every tick. This is called using the BukkitScheduler#Async. This should not cause any bottleneck
     * nor lag. This is extremely useful in 1.9+ I believe. I may have gotten all of it wrong. To inspect!
     */
    void tick();
}
