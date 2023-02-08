package ac.artemis.anticheat.bukkit.commands.impl.debug;

import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.entity.BukkitPlayer;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Ghast
 * @since 26/02/2021
 * Artemis © 2021
 */
public class RecordSubDebugCommand extends ACommand {
    public RecordSubDebugCommand(Artemis artemis) {
        super("record",
                "Debugging command for the anticheat",
                ThemeManager.getCurrentTheme().getDebugPermission(),
                true,
                artemis
        );
        this.setPlayerOnly();
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        final Player player = (Player) executor;
        final PlayerData data = BukkitArtemis.INSTANCE
                .getApi()
                .getPlayerDataManager()
                .getData(new BukkitPlayer(player));

        if (args.length == 0) {
            player.sendMessage(Chat.translate("&7[&6&lDEBUG&7]&c Invalid arguments! Use /&6debug record <check name> &c!"));
            return false;
        }

        if (args.length > 1) {
            final String username = args[0];
            final Player target = Bukkit.getPlayer(username);

            if (target == null) {
                player.sendMessage(Chat.translate("&7[&6&lDEBUG&7]&c Invalid &ruser&c, please do /artemis debug record <user> <check>"));
                return true;
            }

            final PlayerData targetData = BukkitArtemis.INSTANCE
                    .getApi()
                    .getPlayerDataManager()
                    .getData(new BukkitPlayer(target));

            if (targetData == null) {
                player.sendMessage(Chat.translate("&7[&6&lDEBUG&7]&c Invalid &rplayerdata&c, please do /artemis debug record <user> <check>"));
                return true;
            }

            final ArtemisCheck artemisCheck = data.getChecks().stream()
                    .filter(e -> (e.info.getType().name() + e.info.getVar()).equalsIgnoreCase(args[1]))
                    .findFirst().orElse(null);

            if (artemisCheck == null) {
                player.sendMessage(Chat.translate("&7[&6&lDEBUG&7]&c Invalid &rcheck&c, please do /artemis debug record <user> <check>"));
                return true;
            }

            final boolean toggle = targetData.staff.isLogDebug(data, artemisCheck);

            if (toggle) {
                targetData.staff.getLogDebug().asMap().get(data).remove(artemisCheck.getInfo());
            }

            else {
                if (targetData.staff.getLogDebug().asMap().containsKey(data)) {
                    targetData.staff.getLogDebug().asMap().get(data).add(artemisCheck.getInfo());
                } else {
                    targetData.staff.getLogDebug().put(data, new ArrayList<>(Collections.singleton(artemisCheck.getInfo())));
                }
            }

            player.sendMessage(Chat.translate(targetData.staff.isLogDebug(targetData, artemisCheck)
                    ? "&7[&6&lDEBUG&7]&a Enabled &6recorded debug!! Do /&rartemis debug export&6 to export!"
                    : "&7[&6&lDEBUG&7]&c Disabled &6recorded debug! Do /&rartemis debug export&6 to export!")
            );
            return true;
        }

        else {
            final ArtemisCheck artemisCheck = data.getChecks().stream()
                    .filter(e -> (e.info.getType().name() + e.info.getVar()).equalsIgnoreCase(args[0]))
                    .findFirst().orElse(null);
            if (artemisCheck == null) {
                returnInvalidCheck(player);
                return false;
            }

            if (data.staff.isLogDebug(artemisCheck)) {
                data.staff.getLogDebug().asMap().get(data).remove(artemisCheck.getInfo());
                if (data.staff.getLogDebug().asMap().get(data).isEmpty()) {
                    data.staff.getLogDebug().asMap().remove(data);
                }
            } else {
                if (data.staff.getLogDebug().asMap().containsKey(data)) {
                    data.staff.getLogDebug().asMap().get(data).add(artemisCheck.getInfo());
                } else {
                    data.staff.getLogDebug().put(data, new ArrayList<>(Collections.singleton(artemisCheck.getInfo())));
                }

            }

            player.sendMessage(Chat.translate(data.staff.isLogDebug(artemisCheck)
                    ? "&7[&6&lDEBUG&7]&a Enabled &6recorded debug!! Do /&rartemis debug export&6 to export!"
                    : "&7[&6&lDEBUG&7]&c Disabled &6recorded debug! Do /&rartemis debug export&6 to export!"
            ));
            return true;
        }
    }

    private void returnInvalidCheck(Player player) {
        player.sendMessage(Chat.translate(ConfigManager.getSettings().getString("message.invalid-check")));
    }
}
