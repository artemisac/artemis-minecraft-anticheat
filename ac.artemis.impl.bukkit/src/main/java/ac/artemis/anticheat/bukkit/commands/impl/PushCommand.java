package ac.artemis.anticheat.bukkit.commands.impl;

import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.language.Lang;
import org.bukkit.command.CommandSender;

public class PushCommand extends ACommand {
    public PushCommand(Artemis artemis) {
        super("push", "Pushes all the data to the backend", ThemeManager.getCurrentTheme().getMainPermission(), true, artemis);
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        BukkitArtemis.INSTANCE.getApi().getAlertManager().getNotificationProvider().push();
        executor.sendMessage(Chat.translate(ThemeManager.getCurrentTheme().getPrefix() + " &a" + Lang.CMD_PUSH_SUCCESS));
        return false;
    }
}
