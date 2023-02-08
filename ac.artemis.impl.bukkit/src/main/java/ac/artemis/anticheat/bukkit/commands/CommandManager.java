package ac.artemis.anticheat.bukkit.commands;

import ac.artemis.anticheat.bukkit.commands.impl.*;
import ac.artemis.anticheat.bukkit.deprecated.api.command.AbstractCommand;
import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.utils.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ghast
 * @since 19-Mar-20
 */
public class CommandManager extends Manager {

    private final List<AbstractCommand> commandList = new ArrayList<>();

    public CommandManager(BukkitArtemis plugin) {
        super(plugin, "Command [Manager]");
    }

    @Override
    public void init(InitializeAction initializeAction) {
        commandList.addAll(Arrays.asList(
                new ChecksCommand(plugin),
                new EVerboseCommand(plugin)
        ));

        try {
            registerDynamicCommands(plugin);
        } catch (Exception e) {
            ServerUtil.console("&4&lCRITICAL! &cFailed to initialize commands. Contact the plugin developer");
            ServerUtil.console(Chat.spacer());
            e.printStackTrace();
            ServerUtil.console(Chat.spacer());
        }
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {

    }

    private void registerDynamicCommands(Artemis artemis) throws NoSuchFieldException, IllegalAccessException {
        if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
            Field f = SimplePluginManager.class.getDeclaredField("commandMap");
            f.setAccessible(true);

            SimpleCommandMap cmdMap = (SimpleCommandMap) f.get(Bukkit.getPluginManager());
            // REGISTER HERE
            cmdMap.register("logs", "alogs",
                    new LogsCommand(artemis).getCommandBukkit());
            cmdMap.register(ThemeManager.getCurrentTheme().getVerboseCommand(), "averbose",
                    new VerboseCommand(artemis).getCommandBukkit());
            cmdMap.register(ThemeManager.getCurrentTheme().getMainCommand(), "aartemis",
                    new ArtemisCommand(artemis).getCommandBukkit());
            cmdMap.register(ThemeManager.getCurrentTheme().getAlertsCommand(), "aalerts",
                    new AlertsCommand(artemis).getCommandBukkit());
        }
    }
}
