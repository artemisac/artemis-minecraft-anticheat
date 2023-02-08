package ac.artemis.anticheat.bukkit.commands.impl;

import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.core.Artemis;
import org.bukkit.command.CommandSender;

public class ReportsCommand extends ACommand {

    public ReportsCommand(Artemis artemis) {
        super("reports", "Shows all potential cheaters!", "artemis.reports", true, artemis);
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {

        return false;
    }
}
