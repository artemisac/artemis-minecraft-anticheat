package ac.artemis.anticheat.bukkit.commands.impl;

import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import org.bukkit.command.CommandSender;

public class BanwaveSubCommand extends ACommand {
    public BanwaveSubCommand(Artemis artemis) {
        super(ThemeManager.getCurrentTheme().getBanwaveCommand(),
                "Executes the Artemis banwave", ThemeManager.getCurrentTheme().getBanwavePermission(), true, artemis);
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        executor.sendMessage(Chat.translate(ThemeManager.getCurrentTheme().getBanwaveMessage().replace("%count%", Integer.toString(0))));
        return true;
    }
}
