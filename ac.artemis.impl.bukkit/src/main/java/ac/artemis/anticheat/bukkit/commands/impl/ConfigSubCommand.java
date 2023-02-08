package ac.artemis.anticheat.bukkit.commands.impl;

import ac.artemis.anticheat.bukkit.gui.MainController;
import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigSubCommand extends ACommand {
    public ConfigSubCommand(Artemis artemis) {
        super("config", "config", ThemeManager.getCurrentTheme().getMainPermission(), true, artemis);
        this.setPlayerOnly();
        this.setCooldown(3);
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        Player player = (Player) executor;

        if (true) {
            player.sendMessage(Chat.translate("&cThis command is currently disabled for a rewamp!"));
            return false;
        }

        MainController.openInventory(player);
        return false;
    }
}
