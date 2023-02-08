package ac.artemis.anticheat.bukkit.commands.impl;

import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import org.bukkit.command.CommandSender;

/**
 * @author Ghast
 * @since 21-Mar-20
 */
public class ArtemisCommand extends ACommand {
    public ArtemisCommand(Artemis artemis) {
        super(ThemeManager.getCurrentTheme().getMainCommand(),
                "Main command for the anticheat",
                ThemeManager.getCurrentTheme().getMainPermission(),
                false,
                artemis);
        this.addSubCommands(
                new BanSubCommand(artemis),
                new DebugSubCommand(artemis),
                new TimingsCommand(artemis),
                new ConfigSubCommand(artemis),
                new BanwaveSubCommand(artemis),
                new ReloadSubCommand(artemis),
                new PushCommand(artemis)
        );
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        executor.sendMessage(Chat.translateAndConvert(ThemeManager.getCurrentTheme().getHelpMessage()));
        return true;
    }
}
