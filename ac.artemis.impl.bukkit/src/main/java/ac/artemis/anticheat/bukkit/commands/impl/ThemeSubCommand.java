package ac.artemis.anticheat.bukkit.commands.impl;

import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.core.Artemis;
import org.bukkit.command.CommandSender;

/**
 * @author Ghast
 * @since 21-Mar-20
 */
public class ThemeSubCommand extends ACommand {
    public ThemeSubCommand(Artemis artemis) {
        super("theme",
                "Artemis command to change the theme",
                "artemis.theme",
                true,
                artemis);
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        return true;
    }

}
