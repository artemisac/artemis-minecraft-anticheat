package ac.artemis.anticheat.bukkit.commands.impl;

import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.theme.AbstractTheme;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.features.logs.FetchedLog;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LogsCommand extends ACommand {
    public LogsCommand(Artemis artemis) {
        super("logs", "Provides logs for da peeps", "artemis.logs", false, artemis);
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        final AbstractTheme theme = ThemeManager.getCurrentTheme();

        if (args.length == 0) {
            executor.sendMessage(Chat.translate(theme.getPrefix() + " &cYou must specify a username&8:&c /logs &7<&cusername&c7>"));
            return false;
        }

        final String username = args[0];
        executor.sendMessage(Chat.translate(theme.getPrefix() + " &7Looking up username &e" + username + "&7..."));

        CompletableFuture.runAsync(() -> {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);

            if (offlinePlayer == null) {
                executor.sendMessage(Chat.translate(theme.getPrefix() + " &cUser &e" + username + "&c does not exist!"));
                return;
            }

            final List<FetchedLog> fetchedLogList = BukkitArtemis.INSTANCE.getApi().getAlertManager()
                    .getNotificationProvider()
                    .getLogs(offlinePlayer.getUniqueId());

            if (fetchedLogList.isEmpty()) {
                executor.sendMessage(Chat.translate(theme.getPrefix() + " &cUser &e" + username + "&c does not have any logs!"));
                return;
            }

            executor.sendMessage(Chat.translate(theme.getPrefix() + theme.getMainColor()
                    + " Found " + theme.getSecondaryColor() + fetchedLogList.size() + theme.getMainColor()
                    + " logs for user " + theme.getSecondaryColor() + username + theme.getMainColor() + "!"));
            for (FetchedLog fetchedLog : fetchedLogList) {
                executor.sendMessage(Chat.translate("    " + theme.getMainColor()
                        + fetchedLog.getCheck().getType().getCorrectName() + " "
                        + theme.getBracketsColor() + "(" + theme.getSecondaryColor() + fetchedLog.getCheck().getVar()
                        + theme.getBracketsColor() + ") [" + theme.getMainColor() + "x" + theme.getSecondaryColor()
                        + fetchedLog.getCount()) + theme.getBracketsColor() + "]");
            }
        });
        return false;
    }
}
