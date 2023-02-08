package ac.artemis.anticheat.bukkit.commands.impl;

import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import org.bukkit.command.CommandSender;

/**
 * @author Ghast
 * @since 25/03/2021
 * Artemis © 2021
 */
public class ReloadSubCommand extends ACommand {
    public ReloadSubCommand(Artemis artemis) {
        super("reload", "Reloads artemis!", ThemeManager.getCurrentTheme().getMainPermission(),
                true, artemis);
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        BukkitArtemis.INSTANCE.getApi().getCheckManager().reloadChecks();
        executor.sendMessage(Chat.translate(ThemeManager.getCurrentTheme().getPrefix()
                + ThemeManager.getCurrentTheme().getSecondaryColor() + " Reloaded all checks!"));
        return false;
    }
}
