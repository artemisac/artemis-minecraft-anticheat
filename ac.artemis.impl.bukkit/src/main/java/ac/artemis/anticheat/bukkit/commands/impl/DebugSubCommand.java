package ac.artemis.anticheat.bukkit.commands.impl;

import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.entity.BukkitPlayer;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.anticheat.bukkit.commands.impl.debug.PasteSubDebugCommand;
import ac.artemis.anticheat.bukkit.commands.impl.debug.RecordSubDebugCommand;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Ghast
 * @since 21-Mar-20
 */
public class DebugSubCommand extends ACommand {
    public DebugSubCommand(Artemis artemis) {
        super(ThemeManager.getCurrentTheme().getDebugCommand(),
                "Debugging command for the anticheat",
                ThemeManager.getCurrentTheme().getDebugPermission(),
                true,
                artemis
        );

        this.addSubCommands(
                new PasteSubDebugCommand(artemis),
                new RecordSubDebugCommand(artemis)
        );
        this.setPlayerOnly();
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        Player player = (Player) executor;
        PlayerData data = BukkitArtemis.INSTANCE.getApi()
                .getPlayerDataManager()
                .getData(new BukkitPlayer(player));
        if (args.length == 0) {
            player.sendMessage(Chat.translate("&7[&6&lDEBUG&7]&c Invalid arguments! Use /&6debug <check name> &c!"));
            return false;
        }

        switch (args.length) {
            case 1: {
                final ArtemisCheck artemisCheck = data.getChecks().stream()
                        .filter(e -> (e.info.getType().name() + e.info.getVar()).equalsIgnoreCase(args[0]))
                        .findFirst().orElse(null);
                if (artemisCheck == null) {
                    returnInvalidCheck(player);
                    return false;
                }
                if (data.staff.isDebug(artemisCheck)) {
                    data.staff.getDebug().asMap().get(data).remove(artemisCheck.getInfo());
                } else {
                    if (data.staff.getDebug().asMap().containsKey(data)) {
                        data.staff.getDebug().asMap().get(data).add(artemisCheck.getInfo());
                    } else {
                        data.staff.getDebug().put(data, new ArrayList<>(Collections.singleton(artemisCheck.getInfo())));
                    }

                }
                player.sendMessage(Chat.translate(data.staff.isDebug(artemisCheck) ? "&7[&6&lDEBUG&7]&a Enabled &6debug!" : "&7[&6&lDEBUG&7]&c Disabled &6debug!"));
                return true;
            }

            case 2: {
                final String username = args[0];
                final Player target = Bukkit.getPlayer(username);

                if (target == null) {
                    player.sendMessage(Chat.translate("&7[&6&lDEBUG&7]&c Invalid &ruser&c, please do /artemis debug <user> <check>"));
                    return true;
                }

                final PlayerData targetData = BukkitArtemis.INSTANCE.getApi()
                        .getPlayerDataManager()
                        .getData(new BukkitPlayer(target));

                if (targetData == null) {
                    player.sendMessage(Chat.translate("&7[&6&lDEBUG&7]&c Invalid &rplayerdata&c, please do /artemis debug <user> <check>"));
                    return true;
                }

                final ArtemisCheck artemisCheck = data.getChecks().stream()
                        .filter(e -> (e.info.getType().name() + e.info.getVar()).equalsIgnoreCase(args[1]))
                        .findFirst().orElse(null);

                if (artemisCheck == null) {
                    player.sendMessage(Chat.translate("&7[&6&lDEBUG&7]&c Invalid &rcheck&c, please do /artemis debug <user> <check>"));
                    return true;
                }

                final boolean toggle = targetData.staff.isDebug(data, artemisCheck);

                if (toggle) {
                    targetData.staff.getDebug().asMap().get(data).remove(artemisCheck.getInfo());
                }

                else {
                    if (targetData.staff.getDebug().asMap().containsKey(data)) {
                        targetData.staff.getDebug().asMap().get(data).add(artemisCheck.getInfo());
                    } else {
                        targetData.staff.getDebug().put(data, new ArrayList<>(Collections.singleton(artemisCheck.getInfo())));
                    }
                }

                player.sendMessage(Chat.translate(targetData.staff.isDebug(data, artemisCheck) ? "&7[&6&lDEBUG&7]&a Enabled &6debug!" : "&7[&6&lDEBUG&7]&c Disabled &6debug!"));
                return true;
            }


        }

        return true;
    }

    private void returnInvalidCheck(Player player) {
        player.sendMessage(Chat.translate(ConfigManager.getSettings().getString("message.invalid-check")));
    }
}
