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
public class VerboseCommand extends ACommand {
    public VerboseCommand(Artemis artemis) {
        super(ThemeManager.getCurrentTheme().getVerboseCommand(),
                "Toggles verbose for Artemis",
                ThemeManager.getCurrentTheme().getVerbosePermission(),
                false,
                artemis);
        this.setPlayerOnly();
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        final Player player = (Player) executor;
        final PlayerData data = artemis.getApi().getPlayerDataManager().getData(new BukkitPlayer(player));

        if (data == null) return false;

        if (args.length > 0) {
            if ("self".equalsIgnoreCase(args[0])) {
                data.staff.setStaffAlert(
                        !data.staff.getStaffAlert().equals(StaffEnums.StaffAlerts.NONE)
                                ? StaffEnums.StaffAlerts.NONE
                                : StaffEnums.StaffAlerts.VERBOSE_SELF
                );

                final boolean mode = (data.staff.getStaffAlert().equals(StaffEnums.StaffAlerts.VERBOSE_SELF));
                String message = (mode ? "&a" : "&c") + ThemeManager.getCurrentTheme().getVerboseToggleMessage();

                message = message.replace("%mode%", mode ? "on" : "off")
                        .replace("%prefix%", ThemeManager.getCurrentTheme().getPrefix());

                player.sendMessage(Chat.translate(message + " [SELF MODE]"));
                return true;
            }
            player.sendMessage(Chat.translate("&cWhat are you doing step bro?????"));
            return true;
        }

        data.staff.setStaffAlert(data.staff.getStaffAlert().equals(StaffEnums.StaffAlerts.VERBOSE)
                ? StaffEnums.StaffAlerts.NONE
                : StaffEnums.StaffAlerts.VERBOSE);
        BukkitArtemis.INSTANCE.getBukkitNotificationListener().setAlerts(new BukkitPlayer(player));

        final boolean mode = (data.staff.getStaffAlert().equals(StaffEnums.StaffAlerts.VERBOSE));
        String message = (mode ? "&a" : "&c") + ThemeManager.getCurrentTheme().getVerboseToggleMessage();

        message = message.replace("%mode%", mode ? "on" : "off")
                .replace("%prefix%", ThemeManager.getCurrentTheme().getPrefix());


        player.sendMessage(Chat.translate(message));
        return true;
    }

}
