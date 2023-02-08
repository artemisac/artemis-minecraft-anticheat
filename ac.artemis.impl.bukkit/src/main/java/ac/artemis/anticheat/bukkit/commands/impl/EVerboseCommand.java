package ac.artemis.anticheat.bukkit.commands.impl;


import ac.artemis.anticheat.bukkit.entity.BukkitPlayer;
import ac.artemis.anticheat.bukkit.deprecated.api.command.AbstractCommand;
import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.data.utils.StaffEnums;
import ac.artemis.core.v4.utils.chat.Chat;
import org.bukkit.Art;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Ghast
 * @since 19-Mar-20
 */
public class EVerboseCommand extends AbstractCommand {
    public EVerboseCommand(Artemis artemis) {
        super("everbose", "artemis.verbose-experimental", 3, artemis, false);
    }

    @Override
    public void handleCommand(CommandSender executor, Artemis artemis, String[] args) {
        if (executor instanceof Player) {
            Player player = (Player) executor;
            PlayerData data = artemis.getApi().getPlayerDataManager().getData(new BukkitPlayer(player));
            data.staff.setStaffAlert(StaffEnums.StaffAlerts.EXPERIMENTAL_VERBOSE);
            player.sendMessage(Chat.translate(data.staff.getStaffAlert().equals(StaffEnums.StaffAlerts.EXPERIMENTAL_VERBOSE)
                    ? "&cToggled experimental verbose on" : "&cToggled experimental verbose off"));
            BukkitArtemis.INSTANCE.getBukkitNotificationListener().setAlerts(data.getPlayer());
        }
    }

}
