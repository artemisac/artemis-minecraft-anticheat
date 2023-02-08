package ac.artemis.core.v4.theme;

import ac.artemis.core.v4.theme.annotations.Placeholder;
import lombok.Getter;

import java.util.List;

@Getter
public class AbstractTheme {

    // Local
    private final String id;

    // Colors
    @Placeholder("main")
    private String mainColor;
    @Placeholder("secondary")
    private String secondaryColor;
    @Placeholder("brackets")
    private String bracketsColor;

    // Prefix, suffix and pre-made
    @Placeholder("prefix")
    private String prefix;

    // Messages
    private String violationMessage;
    private String verboseMessage;
    private String violationHover;
    //private final String preReleaseVerboseMsg;


    private String joinMessage;

    private String violationToggleMessage;
    private String verboseToggleMessage;

    private String banMessage;
    private String banwaveMessage;
    private List<String> helpMessage;

    private String playerOnlyCommandMessage, consoleOnlyCommandMessage;
    private String noPermissionMessage;

    private String verboseCommand = "verbose";
    private String alertsCommand = "alerts";
    private String mainCommand = "main";
    private String debugCommand = "debug";
    private String databaseCommand = "storage";
    private String banwaveCommand = "banwave";
    private String storageCommand = "storage";

    private String verbosePermission = "artemis.verbose";
    private String alertsPermission = "artemis.alerts";
    private String mainPermission = "artemis.admin";
    private String debugPermission = "artemis.debug";
    private String databasePermission = "artemis.storage";
    private String banwavePermission = "artemis.banwave";
    private String storagePermission = "artemis.storage";

    public AbstractTheme(String id) {
        this.id = id;
    }

    public AbstractTheme setMainColor(String mainColor) {
        this.mainColor = mainColor;
        return this;
    }

    public AbstractTheme setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
        return this;
    }

    public AbstractTheme setBracketsColor(String bracketsColor) {
        this.bracketsColor = bracketsColor;
        return this;
    }

    public AbstractTheme setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public AbstractTheme setViolationMessage(String violationMessage) {
        this.violationMessage = violationMessage;
        return this;
    }

    public AbstractTheme setVerboseMessage(String verboseMessage) {
        this.verboseMessage = verboseMessage;
        return this;
    }

    public AbstractTheme setViolationHover(String violationHover) {
        this.violationHover = violationHover;
        return this;
    }

    public AbstractTheme setBanMessage(String banMessage) {
        this.banMessage = banMessage;
        return this;
    }

    public AbstractTheme setHelpMessage(List<String> helpMessage) {
        this.helpMessage = helpMessage;
        return this;
    }

    public AbstractTheme setPlayerOnlyCommand(String playerOnlyCommand) {
        this.playerOnlyCommandMessage = playerOnlyCommand;
        return this;
    }

    public AbstractTheme setConsoleOnlyCommand(String consoleOnlyCommand) {
        this.consoleOnlyCommandMessage = consoleOnlyCommand;
        return this;
    }

    public AbstractTheme setNoPermission(String noPermission) {
        this.noPermissionMessage = noPermission;
        return this;
    }

    public AbstractTheme setVerboseCommand(String verboseCommand) {
        this.verboseCommand = verboseCommand;
        return this;
    }

    public AbstractTheme setAlertsCommand(String alertsCommand) {
        this.alertsCommand = alertsCommand;
        return this;
    }

    public AbstractTheme setMainCommand(String mainCommand) {
        this.mainCommand = mainCommand;
        return this;
    }

    public AbstractTheme setDebugSubCommand(String debugSubCommand) {
        this.debugCommand = debugSubCommand;
        return this;
    }

    public AbstractTheme setVerbosePermission(String verbosePermission) {
        this.verbosePermission = verbosePermission;
        return this;
    }

    public AbstractTheme setAlertsPermission(String alertsPermission) {
        this.alertsPermission = alertsPermission;
        return this;
    }

    public AbstractTheme setMainPermission(String mainPermission) {
        this.mainPermission = mainPermission;
        return this;
    }

    public AbstractTheme setDebugPermission(String debugPermission) {
        this.debugPermission = debugPermission;
        return this;
    }

    public AbstractTheme setDbSubCommand(String databaseSubCommand) {
        this.databaseCommand = databaseSubCommand;
        return this;
    }

    public AbstractTheme setDbPermission(String databasePermission) {
        this.databasePermission = databasePermission;
        return this;
    }

    public AbstractTheme setViolationToggleMessage(String violationToggleMessage) {
        this.violationToggleMessage = violationToggleMessage;
        return this;
    }

    public AbstractTheme setVerboseToggleMessage(String verboseToggleMessage) {
        this.verboseToggleMessage = verboseToggleMessage;
        return this;
    }


    public AbstractTheme setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
        return this;
    }
}
