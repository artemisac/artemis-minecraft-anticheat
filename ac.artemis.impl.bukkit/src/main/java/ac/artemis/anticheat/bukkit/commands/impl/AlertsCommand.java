package ac.artemis.anticheat.bukkit.commands.impl;


import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.anticheat.bukkit.entity.BukkitPlayer;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.data.utils.StaffEnums;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Ghast
 * @since 19-Mar-20
 */
public class AlertsCommand extends ACommand {
    public AlertsCommand(Artemis artemis) {
        super(ThemeManager.getCurrentTheme().getAlertsCommand(),
                "Command to toggle anticheat alerts",
                ThemeManager.getCurrentTheme().getAlertsPermission(),
                false,
                artemis
        );
        this.setPlayerOnly();
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        Player player = (Player) executor;
        PlayerData data = artemis.getApi().getPlayerDataManager()
                .getData(new BukkitPlayer(player));
        if (data == null) return false;

        data.staff.setStaffAlert(data.staff.getStaffAlert()
                .equals(StaffEnums.StaffAlerts.ALERTS) ? StaffEnums.StaffAlerts.NONE : StaffEnums.StaffAlerts.ALERTS);
        final boolean mode = (data.staff.getStaffAlert().equals(StaffEnums.StaffAlerts.ALERTS));
        String message = (mode ? "&a" : "&c") + ThemeManager.getCurrentTheme().getViolationToggleMessage();
        message = message.replace("%mode%", mode ? "on" : "off")
                .replace("%prefix%", ThemeManager.getCurrentTheme().getPrefix());

        player.sendMessage(Chat.translate(message));
        return true;
    }

}
