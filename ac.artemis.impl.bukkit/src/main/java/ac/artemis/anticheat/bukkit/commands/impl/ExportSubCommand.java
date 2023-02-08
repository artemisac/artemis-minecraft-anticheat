package ac.artemis.anticheat.bukkit.commands.impl;

import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.entity.BukkitPlayer;
import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Ghast
 * @since 21-Mar-20
 */
public class ExportSubCommand extends ACommand {
    public ExportSubCommand(Artemis artemis) {
        super("export",
                "Toggles verbose for Artemis",
                ThemeManager.getCurrentTheme().getMainPermission(),
                false,
                artemis);
        this.setPlayerOnly();
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        Player player = (Player) executor;
        PlayerData data = BukkitArtemis.INSTANCE.getApi().getPlayerDataManager().getData(new BukkitPlayer(player));
        if (args.length == 0) {
            player.sendMessage(Chat.translate("&7[&6&lDEBUG&7]&c Invalid arguments! Use /&6artemis export <check> &c!"));
            return true;
        }

        return true;
    }



    private void returnInvalidCheck(Player player) {
        player.sendMessage(Chat.translate("&7[&6&lDEBUG&7]&c Invalid processor! Make sure it is a correct one!"));
    }
}
