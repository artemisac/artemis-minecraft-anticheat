package ac.artemis.anticheat.bukkit.commands.impl;

import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.timings.TimingsManager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.packet.minecraft.Server;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

/**
 * @author Ghast
 * @since 21-Mar-20
 */
public class TimingsCommand extends ACommand {
    public TimingsCommand(Artemis artemis) {
        super("timings", "Artemis dev command to view timings", "artemis.dev", true, artemis);
        this.setPlayerOnly();
    }

    private static final String[] FORMAT = {
            "&8&m+------------------------------------------+",
            /*"&b&lCPU&7:&b %cpu%%",
            "&b&lRAM&7:&b %ram%&7/&b%max_ram%",*/
            "&b&lTimings&7:",
            "&7&l-> &bPacket time&7:&b %packet_time%",
            "&7&l-> &bHandler time&7:&b %handler_time%",
            "&7&l-> &bJoin time&7:&b %join_time%",
            "&8&m+------------------------------------------+"
    };

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        //SystemInfo info = Artemis.INSTANCE.getApi().getSystemManager().getSystemInfo();
        final TimingsManager timingsManager = BukkitArtemis.INSTANCE.getApi().getTimingsManager();

        /*int cpu = info.getHardware().getProcessor().getLogicalProcessorCount();
        long ram = info.getHardware().getMemory().getAvailable() / 1000000;
        long total = info.getHardware().getMemory().getTotal() / 1000000;
        long used = total - ram;-*/
        CompletableFuture.runAsync(() -> {
            final double packetTime = timingsManager.getAverageTimePacket();
            final double handlerTime = timingsManager.getAverageTimeHandler();
            final double joinTime = timingsManager.getJoinTiming().getAverage();

            final String[] format = FORMAT;
            for (int i = 0; i < format.length; i++) {
                format[i] = Chat.translate(format[i]
                        /*.replace("%cpu%", Integer.toString(cpu))
                        .replace("%ram%", Long.toString(used))
                        .replace("%max_ram%", Long.toString(total))*/
                        .replace("%packet_time%", Double.toString(packetTime))
                        .replace("%handler_time%", Double.toString(handlerTime))
                        .replace("%join_time%", Double.toString(joinTime))
                );
            }

            Server.v().getScheduler().runTask(() -> executor.sendMessage(format));
        });

        return true;
    }


}
