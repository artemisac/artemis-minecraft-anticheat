package ac.artemis.anticheat.bukkit.commands;

import ac.artemis.core.Artemis;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.StringUtil;
import ac.artemis.anticheat.bukkit.utils.command.CommandUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Art;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Ghast
 * @since 04-Apr-20
 */
public abstract class ACommand implements CommandExecutor {
    private final String command, permission;
    private final List<ACommand> subCommands;
    private Cache<CommandSender, Long> playerLongCache;
    private boolean playerOnly = false, consoleOnly = false;
    private Artemis artemis;
    private PluginCommand commandBukkit;

    public ACommand(String name, String description, String permission, boolean subCommand, Artemis artemis) {
        this.command = name;
        this.permission = permission;
        this.artemis = artemis;
        this.subCommands = new ArrayList<>();
        this.playerLongCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(50, TimeUnit.MILLISECONDS)
                .build();

        if (!subCommand) {
            this.commandBukkit = CommandUtil.getCommand(name, artemis.getPlugin().v());
            this.commandBukkit.setExecutor(this);
        }
    }

    public ACommand setPlayerOnly() {
        this.playerOnly = true;
        return this;
    }

    public ACommand setConsoleOnly() {
        this.consoleOnly = true;
        return this;
    }

    public ACommand addSubCommands(ACommand... subCommands) {
        this.subCommands.addAll(Arrays.asList(subCommands));
        return this;
    }

    public ACommand setCooldown(int i) {
        this.playerLongCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(i, TimeUnit.SECONDS)
                .build();
        return this;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (playerOnly && !(commandSender instanceof Player)) return notPlayer(commandSender);
        if (consoleOnly && !(commandSender instanceof ConsoleCommandSender)) return notConsole(commandSender);
        if (!commandSender.hasPermission(permission)) return noPermission(commandSender);

        if (strings.length > 0) {
            for (ACommand cmd : subCommands) {
                if (cmd.getCommand().equalsIgnoreCase(strings[0])) {
                    return cmd.onCommand(commandSender, command, s, StringUtil.displaceByOne(strings));
                }
            }
        }
        run(commandSender, artemis, strings);
        playerLongCache.put(commandSender, System.currentTimeMillis());
        return true;
    }

    public abstract boolean run(CommandSender executor, Artemis artemis, String[] args);

    public boolean notPlayer(CommandSender sender) {
        sender.sendMessage(ThemeManager.getCurrentTheme().getPlayerOnlyCommandMessage());
        return false;
    }

    public boolean notConsole(CommandSender sender) {
        sender.sendMessage(ThemeManager.getCurrentTheme().getConsoleOnlyCommandMessage());
        return false;
    }

    public boolean noPermission(CommandSender sender) {
        sender.sendMessage(ThemeManager.getCurrentTheme().getNoPermissionMessage());
        return false;
    }

    public String getCommand() {
        return command;
    }

    public PluginCommand getCommandBukkit() {
        return commandBukkit;
    }
}
