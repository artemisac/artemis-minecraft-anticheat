package ac.artemis.anticheat.bukkit.utils.command;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Ghast
 * @since 04-Apr-20
 */
public class CommandUtil {
    public static PluginCommand getCommand(String name, Plugin plugin) {
        PluginCommand command = null;

        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);
            command = c.newInstance(name, plugin);
        } catch (SecurityException | NoSuchMethodException
                | InvocationTargetException | InstantiationException
                | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return command;
    }
}
