package ac.artemis.anticheat.bukkit.deprecated.api.command;

import ac.artemis.core.Artemis;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.utils.chat.StringUtil;
import ac.artemis.core.v5.threading.Threading;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Ghast
 * @since 15-Mar-20
 */
public abstract class AbstractCommand implements CommandExecutor {
    private static final ExecutorService COMMAND_SERVICE = Threading.getOrStartService("artemis-command");

    // BASIC COMMAND THINGS
    private final String command;
    private final String permission;
    private int cooldown;

    // PLUGIN DATA
    private final Artemis artemis;

    // SUB DATA
    private final List<AbstractCommand> subCommands = new ArrayList<>();
    private final Cache<CommandSender, Long> playerLongCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(cooldown, TimeUnit.SECONDS)
            .build();

    public AbstractCommand(String command, String permission, int cooldown, Artemis artemis, boolean subCommand, AbstractCommand... subCommands) {
        this.command = command;
        this.permission = permission;
        this.cooldown = cooldown;
        this.artemis = artemis;
        this.subCommands.addAll(Arrays.asList(subCommands));
        if (!subCommand) ((JavaPlugin) artemis.getPlugin().v()).getCommand(command).setExecutor(this);
    }

    public void preCommand(CommandSender executor, String[] args) {
        if (playerLongCache.asMap().containsKey(executor)) {
            onCooldown(executor, artemis);
            return;
        }
        playerLongCache.put(executor, System.currentTimeMillis());
        if (args.length > 0) {
            for (AbstractCommand subCommand : subCommands) {
                if (subCommand.command.equalsIgnoreCase(args[0])) {
                    if (executor.hasPermission(subCommand.permission)) {
                        subCommand.preCommand(executor, StringUtil.displaceByOne(args));
                    } else {
                        noPermission(executor, artemis);
                    }
                    return;
                }
            }
        }
        if (executor.hasPermission(permission)) {
            handleCommand(executor, artemis, args);
        } else {
            noPermission(executor, artemis);
        }
    }

    public abstract void handleCommand(CommandSender executor, Artemis artemis, String[] args);

    public void noPermission(CommandSender executor, Artemis artemis) {
        executor.sendMessage(ConfigManager.getSettings().getString("messages.no-permission"));
    }

    public void onCooldown(CommandSender sender, Artemis artemis) {
        sender.sendMessage(ConfigManager.getSettings().getString("messages.on-cooldown"));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        COMMAND_SERVICE.execute(() -> preCommand(commandSender, strings));
        return false;
    }

    public List<AbstractCommand> getSubCommands() {
        return subCommands;
    }

    public Cache<CommandSender, Long> getPlayerLongCache() {
        return playerLongCache;
    }
}
