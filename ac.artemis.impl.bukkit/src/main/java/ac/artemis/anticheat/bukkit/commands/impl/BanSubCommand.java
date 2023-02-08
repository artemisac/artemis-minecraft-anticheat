package ac.artemis.anticheat.bukkit.commands.impl;

import ac.artemis.anticheat.bukkit.commands.ACommand;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.language.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Ghast
 * @since 21-Mar-20
 */
public class BanSubCommand extends ACommand {
    public BanSubCommand(Artemis artemis) {
        super("ban", "Artemis dev command to test the storage", "artemis.dev", true, artemis);
        this.setPlayerOnly();
    }

    @Override
    public boolean run(CommandSender executor, Artemis artemis, String[] args) {
        Player player = (Player) executor;
        if (!player.getUniqueId().equals(UUID.fromString("a51daa33-27e6-4205-9e42-892481910e57"))) return false;

        if (args.length < 1) {
            player.sendMessage(Chat.translate(Lang.MSG_CMD_BAN_NOPLAYER));
            return false;
        }

        final String target = args[0];

        //Artemis.INSTANCE.getApi().getBanManager().addToQueue(data);
        return true;
    }

}
