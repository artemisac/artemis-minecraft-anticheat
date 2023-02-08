package ac.artemis.anticheat.bukkit.commands.impl;

import ac.artemis.anticheat.bukkit.entity.BukkitPlayer;
import ac.artemis.anticheat.bukkit.deprecated.AB;
import ac.artemis.anticheat.bukkit.deprecated.api.command.AbstractCommand;
import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.chat.Chat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ghast
 * @since 23-Oct-19
 * Ghast CC © 2019
 */
public class ChecksCommand extends AbstractCommand {

    public ChecksCommand(Artemis artemis) {
        super("mychecks", "artemis.checks", 5, artemis, false);
    }

    @Override
    public void handleCommand(CommandSender executor, Artemis artemis, String[] args) {
        if (!(executor instanceof Player)) return;
        Player player = (Player) executor;
        PlayerData data = artemis.getApi().getPlayerDataManager().getData(new BukkitPlayer(player));
        AtomicInteger size = new AtomicInteger();

        data.getCheckManager().forEach(cm -> cm.getAbstractChecks().forEach(check -> {
            if (check == null || check.info == null || check.info.getType() == null) return;
            size.incrementAndGet();
            player.sendMessage(check.info.isEnabled() ?
                    Chat.translate("&7&l-> &a" + check.info.getType().name() + " &7(&a" + check.info.getVar() + "&7)") :
                    Chat.translate("&7&l-> &c" + check.info.getType().name() + " &7(&c" + check.info.getVar() + "&7)")
            );
        }));
        player.sendMessage(Chat.translate(AB.MAIN_COLOR
                + "There is currently &a"
                + size.get()
                + AB.MAIN_COLOR
                + " checks!"));
    }


}
