package ac.artemis.anticheat.bukkit;

import ac.artemis.packet.minecraft.AbstractWrapper;
import ac.artemis.packet.minecraft.scheduler.Scheduler;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitScheduler extends AbstractWrapper<org.bukkit.scheduler.BukkitScheduler> implements Scheduler {
    private final JavaPlugin plugin;

    public BukkitScheduler(org.bukkit.scheduler.BukkitScheduler wrapper, JavaPlugin plugin) {
        super(wrapper);
        this.plugin = plugin;
    }

    @Override
    public void runTask(Runnable runnable) {
        wrapper.runTask(plugin, runnable);
    }

    @Override
    public void runTaskTimerAsynchronously(Runnable runnable, long l, long l1) {
        wrapper.runTaskTimerAsynchronously(plugin, runnable, l, l1);
    }

    @Override
    public void scheduleAsyncRepeatingTask(Runnable runnable, long l, long l1) {
        wrapper.scheduleAsyncRepeatingTask(plugin, runnable, l, l1);
    }
}
