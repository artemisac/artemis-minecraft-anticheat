package ac.artemis.anticheat.bukkit.commands.impl.debug;

import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.anticheat.bukkit.entity.BukkitPlayer;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v4.utils.hastebin.Hastebin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Ghast
 * @since 26/02/2021
 * Artemis © 2021
 */
public class PasteSubDebugCommand extends ACommand {
    public PasteSubDebugCommand(Artemis artemis) {
        super("export",
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

        if (!data.staff.isLogging()) {
            player.sendMessage(Chat.translate("&7[&6&lDEBUG&7]&c You are not logging anything!"));
            return true;
        }

        final String[] payload = data.staff.getLog().toArray(new String[0]);
        player.sendMessage(Chat.translate("&7[&6&lDEBUG&7]&a Attempting to paste to Hastebin"));
        Hastebin.paste(payload, data.getPlayer());
        data.staff.getLog().clear();
        return true;
    }
}
