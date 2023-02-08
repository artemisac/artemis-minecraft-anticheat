package ac.artemis.core.v4.check.settings;

import ac.artemis.anticheat.api.check.CheckInfo;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.check.annotations.*;
import ac.artemis.core.v4.check.exceptions.InvalidCheckNameException;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.anticheat.api.check.type.Stage;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.packet.protocol.ProtocolVersion;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 * @author Ghast
 * @since 15-Mar-20
 */

@Getter
@Setter
public class CheckInformation implements CheckInfo {

    /**
     * Type of the Check
     * Eg: KILLAURA
     */
    private Type type;

    /**
     * Variable attached of the check
     * Eg: A, Friction, MotionXZ
     */
    private String var;

    /**
     * Category variable
     * eg: KillAura visual
     */
    private String visualCategory;

    /**
     * Category variable
     * eg: KillAura visual
     */
    private String visualName;

    /**
     * Whether the verbose should decrease over time
     */
    private boolean noDrop;

    /**
     * Stage of the check.
     * Eg: EXPERIMENTAL, RELEASE
     * The following are available:
     * EXPERIMENTING: Will output logs to experimental verbose
     * @see Stage#EXPERIMENTING
     * TESTING: Will output logs to experimental verbose
     * @see Stage#TESTING
     * FALSING: Will output logs to experimental verbose
     * @see Stage#FALSING
     * PRE_RELEASE: Will output logs to verbose but won't stack. Alerts only
     * @see Stage#PRE_RELEASE
     * RELEASE: Will output logs and will stack alerts for potential violations
     * @see Stage#RELEASE
     */
    private Stage stage;

    /**
     * Maximum amount of verboses before a violation. This value is hardcoded
     */
    private int maxVb, delayBetweenAlerts;

    /**
     * Maximum amount of violations before the execution of a ban. This value is configurable
     * @see ConfigManager#getChecks()
     */
    private int maxVl;

    /**
     * Rate at which the decay happens. This is a modulo hence needs to be 2 for once every
     * 2 ticks etc...
     */
    private int decay;

    /**
     * Configurable option whether the check is enabled or not.
     */
    private boolean enabled;

    /**
     * Configurable option whether the check can ban or not.
     */
    private boolean bannable;

    /**
     * Configurable option whether the check setbacks or not.
     */
    private boolean setback;

    /**
     * Hardcoded option which requires the need of NMS
     * @deprecated Since the use of NMS collisions in the handlers
     */
    @Deprecated
    private boolean compatibleNMS;

    /**
     * Versions compatible with the check. This value is hardcoded
     */
    private List<ProtocolVersion> compatibleClientVersions;

    /**
     * Server versions compatible with the check. This value is hardcoded
     * @deprecated Since the slow and tedious addition of the Artemis Packet API, this will be rendered as useless
     * since no bottlenecks caused by ViaVersion will occur.
     */
    private List<ProtocolVersion> compatibleServerVersions;

    /**
     * Map of all the settings. This is as efficient I can imagine it being. You guys can debate whether or not
     * a string hashmap would be best since it's only being queried on object instantiation.
     * Todo Probably make this cleaner ngl
     */
    private EnumMap<CheckSettings, CheckSetting> settings;

    /**
     * Parent class of the Artemis check ever in-case it needs to be used for iteration.
     * @see ArtemisCheck
     */
    private final Class<? extends ArtemisCheck> parent;

    public CheckInformation(Class<? extends ArtemisCheck> clazz) {
        this.parent = clazz;
        this.init();
    }

    public void init() {
        // Annotation MUST be present when instantiating a CheckInformation.
        final boolean invalid = !parent.isAnnotationPresent(Check.class);
        if (invalid) {
            throw new InvalidCheckNameException();
        }

        // Set a local variable for practical use
        Check check = parent.getAnnotation(Check.class);

        // Format the check name adequately for Config iteration
        String checkName = check.type().getCategory().name().toLowerCase() + "." + check.type().name().toLowerCase() + "." + check.var().toLowerCase();

        // Set the type
        this.type = check.type();

        // Set the var
        this.var = check.var();

        // Establish visuals
        this.visualName = (String) computeIfAbsent(checkName + ".name", check.var());
        this.visualCategory = (String) computeIfAbsent(checkName + ".category", check.type().getCorrectName());

        // If the annotation is present, the check shouldn't drop in verbose
        this.noDrop = parent.isAnnotationPresent(Drop.class);

        this.decay = noDrop ? parent.getAnnotation(Drop.class).decay() : 1;

        // If the experimental annotation is present, set the stage to it's value. If not, set to Release by default.
        this.stage = parent.isAnnotationPresent(Experimental.class) ?
                stage = parent.getAnnotation(Experimental.class).stage() : Stage.RELEASE;

        // If the ClientVersion annotation is present, only make it's values compatible. If not, make all versions compatible
        this.compatibleClientVersions = parent.isAnnotationPresent(ClientVersion.class)
                ? Arrays.asList(parent.getAnnotation(ClientVersion.class).version())
                : Arrays.asList(ProtocolVersion.values());

        // If the ServerVersion annotation is present, only make it's values compatible. If not, make all versions compatible
        this.compatibleServerVersions = parent.isAnnotationPresent(ServerVersion.class)
                ? Arrays.asList(parent.getAnnotation(ServerVersion.class).version())
                : Arrays.asList(ProtocolVersion.values());

        // If the annotation is not present / if the NMS manager does exist, then the check is compatible with NMS
        this.compatibleNMS = !parent.isAnnotationPresent(NMS.class)
                || Artemis.v().getApi().getNmsManager().isNmsHooked();

        // If the setback annotation is present, the check is then only eligible for setbacks
        final boolean setbackAvailable = parent.isAnnotationPresent(Setback.class);
        if (setbackAvailable) {
            this.setback = (boolean) computeIfAbsent(checkName + ".setback", true);
        }

        // Recreate the settings map on every instantiation to prevent setting dupes
        this.settings = new EnumMap<>(CheckSettings.class);

        // For each declared field, if one is declared by the setting annotation, add it to the map
        for (Field field : parent.getDeclaredFields()) {
            // Prevent false adding on non-annotated values
            if (!field.isAnnotationPresent(Setting.class)) continue;

            // Locally cache it as it's used more than once
            Setting setting = field.getAnnotation(Setting.class);

            // Simplify writing
            CheckSettings typeName = setting.type();

            // Config yada
            Object object = computeIfAbsent(checkName + "." + ".settings." + typeName.getFriendlyName().toLowerCase(),
                    setting.defaultValue());
            this.settings.put(typeName, new CheckSetting(object));
        }

        // Set the maximum amount of vls before ban
        this.maxVl = computeIfAbsent(checkName + ".max-vls", 1);

        // Set the maximum amount of vls before ban
        this.maxVb = computeIfAbsent(checkName + ".max-vbs", check.threshold());

        // Set if the check is bannable
        this.bannable = computeIfAbsent(checkName + ".bannable", check.bannable());

        // Set if the check is enabled
        this.enabled = computeIfAbsent(checkName + ".enabled", check.enabled());

        // Set the delay between alerts
        this.delayBetweenAlerts = computeIfAbsent(checkName + ".alertDelay", 5000);
    }


    /**
     * Todo document this shit
     * @param s Path of the config value
     * @param defaultValue Default value if not present
     * @return Object found in the config
     */
    private <T> T computeIfAbsent(String s, T defaultValue) {
        T var = ConfigManager.getChecks().get(s);
        if (var == null) {
            ConfigManager.getChecks().set(s, defaultValue);
            // Not efficient but works better than most the shit ¯\_(ツ)_/¯
            ConfigManager.getChecks().save();
            return defaultValue;
        }
        return var;
    }

    public CheckSetting getSetting(CheckSettings type) {
        return this.settings.get(type);
    }

    public void save() {
        // Annotation MUST be present when instantiating a CheckInformation.
        final boolean invalid = !parent.isAnnotationPresent(Check.class);
        if (invalid) {
            throw new InvalidCheckNameException();
        }

        // Set a local variable for practical use
        Check check = parent.getAnnotation(Check.class);

        // Format the check name adequately for Config iteration
        String checkName = check.type().getCategory().name().toLowerCase() + "." + check.type().name().toLowerCase() + "." + check.var().toLowerCase();

        this.setConfig(checkName + ".name", visualName);
        this.setConfig(checkName + ".category", visualCategory);

        // If the setback annotation is present, the check is then only eligible for setbacks
        final boolean setbackAvailable = parent.isAnnotationPresent(Setback.class);
        if (setbackAvailable) {
            this.setConfig(checkName + ".setback", setback);
        }

        settings.forEach((setting, value) -> {
            this.setConfig(checkName + ".settings." + setting.getFriendlyName().toLowerCase(), value.getValue());
        });

        // Set the maximum amount of vls before ban
        this.setConfig(checkName + ".max-vls", maxVl);

        // Set if the check is bannable
        this.setConfig(checkName + ".bannable", bannable);

        // Set if the check is enabled
        this.setConfig(checkName + ".enabled", enabled);

        // Set the delay between alerts
        this.setConfig(checkName + ".alertDelay", delayBetweenAlerts);

        ConfigManager.getChecks().save();
        ConfigManager.getChecks().load();
    }

    private void setConfig(String path, Object value) {
        ConfigManager.getChecks().set(path, value);
    }

    public CheckInformation setSettingAndReturn(CheckSettings type, Object obj) {
        this.settings.put(type, new CheckSetting(obj));
        return this;
    }

    public void setSetting(CheckSettings type, Object obj) {
        this.settings.put(type, new CheckSetting(obj));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CheckInformation that = (CheckInformation) o;

        if (type != that.type) return false;
        if (var != null ? !var.equals(that.var) : that.var != null) return false;
        return parent != null ? parent.equals(that.parent) : that.parent == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (var != null ? var.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }
}
