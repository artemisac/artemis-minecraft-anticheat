package ac.artemis.anticheat.api.check;

import ac.artemis.anticheat.api.check.type.Stage;
import ac.artemis.anticheat.api.check.type.Type;

public interface CheckInfo {
    /**
     * Type of the Check
     * Eg: KILLAURA
     */
    Type getType();

    /**
     * Variable attached of the check
     * Eg: A, Friction, MotionXZ
     */
    String getVar();

    /**
     * Category variable
     * eg: KillAura visual
     */
    String getVisualCategory();

    /**
     * Category variable
     * eg: KillAura visual
     */
    String getVisualName();

    /**
     * Whether the verbose should decrease over time
     */
    boolean isNoDrop();

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
    Stage getStage();

    /**
     * Maximum amount of verboses before a violation. This value is hardcoded
     */
    int getMaxVb();

    int getDelayBetweenAlerts();

    /**
     * Maximum amount of violations before the execution of a ban. This value is configurable
     * @see ConfigManager#getChecks()
     */
    int getMaxVl();

    /**
     * Configurable option whether the check is enabled or not.
     */
    boolean isEnabled();

    /**
     * Configurable option whether the check can ban or not.
     */
    boolean isBannable();

    /**
     * Configurable option whether the check setbacks or not.
     */
    boolean isSetback();

    /**
     * Hardcoded option which requires the need of NMS
     * @deprecated Since the use of NMS collisions in the handlers
     */
    @Deprecated
    boolean isCompatibleNMS();

    /**
     * Type of the Check
     * Eg: KILLAURA
     */
    void setType(Type type);

    /**
     * Variable attached of the check
     * Eg: A, Friction, MotionXZ
     */
    void setVar(String var);

    /**
     * Category variable
     * eg: KillAura visual
     */
    void setVisualCategory(String visualCategory);

    /**
     * Category variable
     * eg: KillAura visual
     */
    void setVisualName(String visualName);

    /**
     * Whether the verbose should decrease over time
     */
    void setNoDrop(boolean noDrop);

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
    void setStage(Stage stage);

    /**
     * Maximum amount of verboses before a violation. This value is hardcoded
     */
    void setMaxVb(int maxVb);

    void setDelayBetweenAlerts(int delayBetweenAlerts);

    /**
     * Maximum amount of violations before the execution of a ban. This value is configurable
     * @see ConfigManager#setChecks()
     */
    void setMaxVl(int maxVl);

    /**
     * Configurable option whether the check is enabled or not.
     */
    void setEnabled(boolean enabled);

    /**
     * Configurable option whether the check can ban or not.
     */
    void setBannable(boolean bannable);

    /**
     * Configurable option whether the check setbacks or not.
     */
    void setSetback(boolean setback);

    /**
     * Saves the check configuration to files
     */
    void save();
}
