package ac.artemis.core.v4.theme;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.config.Configuration;
import ac.artemis.packet.minecraft.plugin.Plugin;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.theme.annotations.Placeholder;
import ac.artemis.core.v4.theme.exceptions.ThemeNotFoundException;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v4.utils.chat.StringUtil;
import ac.artemis.core.v4.utils.reflection.AnnotationUtil;
import ac.artemis.core.v5.language.Lang;
import lombok.Getter;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class ThemeManager extends Manager {

    private final Plugin plugin;
    private final Deque<AbstractTheme> themes = new LinkedList<>();
    @Getter
    private static AbstractTheme currentTheme;

    public ThemeManager(Artemis artemis) {
        super(artemis, "Theme [Manager]");
        this.plugin = artemis.getPlugin();
    }

    @Override
    public void init(InitializeAction initializeAction) {

        // Initialize Theme Directory
        File dir = Arrays.stream(Objects.requireNonNull(plugin.getDataFolder().listFiles())).filter(file -> file.getName().equalsIgnoreCase("themes")).findFirst().orElse(null);
        if (dir == null) {
            File file = new File(plugin.getDataFolder(), "themes");
            if (!file.exists()) file.mkdir();
            Chat.sendConsoleMessage("&cINVALID_DIRECTORY: FAILED TO LOAD THEMES | LOADING NEW THEME");
            Configuration temp = Server.v().getConfig("themes/artemis.yml");
            Configuration tempDev = Server.v().getConfig("themes/artemisdev.yml");
            temp.save();
            tempDev.save();
            themes.add(resolveTheme("artemis.yml", temp));
            themes.add(resolveTheme("artemisdev.yml", tempDev));
            setTheme(ConfigManager.getSettings().getString("general.theme"));
            init(InitializeAction.RELOAD);
            return;
        }

        // Initialize all themes
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            AbstractTheme theme = resolveTheme(file.getName(), Server.v().getConfig("themes/" + file.getName()));
            themes.add(theme);
            Chat.sendConsoleMessage("&r[&a✓&r] " + Lang.INIT_THEME + " " + file.getName());
        }

        // Set default theme
        setTheme(ConfigManager.getSettings().getString("general.theme"));

        if (ConfigManager.getSettings().getBoolean("general.remove-from-plugins")) removeSelf();
        else {
            renameSelf(ConfigManager.getSettings().getString("general.prefix-in-plugins"));
        }
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {
        ConfigManager.getSettings().load();
        ConfigManager.getSettings().set("general.theme", currentTheme.getId());
    }

    public void setTheme(String name) {
        currentTheme = themes.stream().filter(t -> t.getId().equalsIgnoreCase(name)).findFirst().orElseThrow(
                () -> new ThemeNotFoundException(name));
        themes.remove(currentTheme);
        themes.addFirst(currentTheme);
    }

    public void setNextTheme() {
        currentTheme = themes.pollFirst();
        themes.addLast(currentTheme);
    }

    public void setPreviousTheme() {
        currentTheme = themes.pollLast();
        themes.addFirst(currentTheme);
    }

    public List<AbstractTheme> getThemes() {
        return new ArrayList<>(themes);
    }

    public Deque<AbstractTheme> getThemeQueue() {
        return themes;
    }


    private AbstractTheme resolveTheme(String id, Configuration config) {
        AbstractTheme theme = new AbstractTheme(id);
        // COLORS
        String[] colors = {"main", "secondary", "brackets"};
        for (String s : colors) registerColor(s, theme, config);


        String colorMain = Chat.translate(config.getString("colors.main"));
        String colorSecondary = Chat.translate(config.getString("colors.secondary"));
        String colorBrackets = Chat.translate(config.getString("colors.brackets"));

        String[] messages = {"violation", "verbose", "ban", "banwave"};
        for (String s : messages) registerMessage(s, theme, config);
        // VIOLATIONS
        String prefix = translateValues(Chat.translate(config.getString("general.prefix")), colorMain,
                colorSecondary, colorBrackets, "");
        String violationMsg = translateValues(Chat.translate(config.getString("messages.violation")),
                colorMain, colorSecondary, colorBrackets, prefix);
        String hoverCommand = config.getString("messages.violation-hover-command");
        String verboseMsg = translateValues(Chat.translate(config.getString("messages.verbose")),
                colorMain, colorSecondary, colorBrackets, prefix);
        String hoverVerboseCommand = config.getString("messages.verbose-hover-command");
        String banMsg = translateValues(Chat.translate(config.getString("messages.ban")), colorMain,
                colorSecondary, colorBrackets, prefix);

        // HELP
        List<String> helpMsg = translateValues(Chat.translate(config.getStringList("messages.help-message")),
                colorMain, colorSecondary, colorBrackets, prefix);

        // COMMANDS

        String[] commands = {"alerts", "verbose", "main", "debug"};
        String commandPrefix = Chat.translate(config.getString("commands.prefix"));
        for (String s : commands) registerCommand(s, theme, config, commandPrefix);

        registerPlaceholders(theme);
        return theme
                // VIOLATIONS
                .setPrefix(prefix)
                .setViolationMessage(violationMsg)
                .setVerboseMessage(verboseMsg)
                .setViolationHover(hoverCommand)
                .setBanMessage(banMsg)
                .setNoPermission(translateValues(Chat.translate(config.getString("messages.no-permission")),
                        colorMain, colorSecondary, colorBrackets, prefix))
                // HELP
                .setHelpMessage(helpMsg)

                // COMMANDS
                .setVerboseToggleMessage(config.getString("messages.verbose-toggled"))
                .setViolationToggleMessage(config.getString("messages.alerts-toggled"))
                .setJoinMessage(translateValues(Chat.translate(config.getString("messages.join-message")),
                        colorMain, colorSecondary, colorBrackets, prefix))
                ;
    }

    private void registerCommand(String s, AbstractTheme theme, Configuration configuration, String commandPrefix) {
        try {
            Field fieldPerm = AbstractTheme.class.getDeclaredField(s.toLowerCase() + "Permission");
            Field fieldCmd = AbstractTheme.class.getDeclaredField(s.toLowerCase() + "Command");

            fieldPerm.setAccessible(true);
            fieldCmd.setAccessible(true);

            fieldPerm.set(theme, getPermission(s, commandPrefix, configuration));
            fieldCmd.set(theme, getCommand(s, configuration));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerMessage(String message, AbstractTheme theme, Configuration configuration) {
        try {
            Field fieldMessage = AbstractTheme.class.getDeclaredField(message + "Message");
            fieldMessage.setAccessible(true);
            fieldMessage.set(theme, Chat.translate(configuration.getString("messages." + message)));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerColor(String color, AbstractTheme theme, Configuration configuration) {
        try {
            Field fieldColor = AbstractTheme.class.getDeclaredField(color.toLowerCase() + "Color");
            fieldColor.setAccessible(true);
            fieldColor.set(theme, Chat.translate(configuration.getString("colors." + color)));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerPlaceholders(AbstractTheme theme) {
        try {
            Field[] fields = AbstractTheme.class.getDeclaredFields();
            // For each field
            for (Field field : fields) {

                // If the field is a class

                if (!(field.getType().equals(String.class) && !field.isAnnotationPresent(Placeholder.class))) continue;

                field.setAccessible(true);

                // If the field has the placeholder markers and has more than two
                Object obj = field.get(theme);
                if (obj == null) continue;
                String var = obj.toString();
                if (StringUtil.countChar(var, '%') > 2) {

                    boolean start = false;
                    StringBuilder builder = new StringBuilder();

                    // Grab all values
                    for (char chr : var.toCharArray()) {

                        // If char is beginning/end of the placeholder, start/end it
                        if (chr != '%') {
                            if (!start) continue;
                            builder.append(chr);
                            continue;
                        }
                        if (start) {
                            start = false;

                            if (builder.length() == 0) continue;

                            String name = builder.toString();
                            Set<Field> possible = AnnotationUtil.findFields(AbstractTheme.class, Placeholder.class);

                            for (Field target : possible) {
                                if (!target.getType().equals(String.class)) continue;

                                target.setAccessible(true);
                                String targetName = target.getAnnotation(Placeholder.class).value();

                                if (!targetName.equalsIgnoreCase(name)) continue;

                                Object obj2 = target.get(theme);
                                if (obj2 == null) continue;
                                String value = obj2.toString();
                                var = var.replace("%" + name + "%", value);
                            }
                            builder = new StringBuilder();
                        } else {
                            start = true;
                        }

                    }
                }
                field.set(theme, var);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private String getPermission(String command, String commandPrefix, Configuration configuration) {
        return configuration.getString("commands." + command + "-command.permission").replace("%prefix%", commandPrefix);
    }

    private String getCommand(String command, Configuration configuration) {
        return configuration.getString("commands." + command + "-command.value");
    }

    private String translateValues(String s, String main, String secondary, String brackets, String prefix) {
        return s.replace("%main%", main)
                .replace("%secondary%", secondary)
                .replace("%brackets%", brackets)
                .replace("%prefix%", prefix);
    }

    private List<String> translateValues(List<String> list, String main, String secondary, String brackets, String prefix) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, translateValues(list.get(i), main, secondary, brackets, prefix));
        }
        return list;
    }

    private void renameSelf(String newName) {
        plugin.rename(newName);
        /*try {
            Field pluginName = PluginDescriptionFile.class.getDeclaredField("name");
            pluginName.setAccessible(true);
            pluginName.set(plugin.getDescription(), newName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }*/
    }

    private void removeSelf() {
        plugin.hide(true);
        /*try {
            Field pluginsList = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
            pluginsList.setAccessible(true);
            List<Plugin> updater = (List<Plugin>) pluginsList.get(Bukkit.getPluginManager());
            updater.remove(plugin);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }*/
    }

}
