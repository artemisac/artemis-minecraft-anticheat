package ac.artemis.core.v4.check;


import ac.artemis.anticheat.api.alert.Severity;
import ac.artemis.anticheat.api.check.Check;
import ac.artemis.anticheat.api.check.type.Stage;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.data.utils.StaffEnums;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.core.v5.logging.model.Ban;
import ac.artemis.core.v5.logging.model.Log;
import ac.artemis.packet.spigot.utils.ServerUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author Ghast
 * @since 15-Mar-20
 */

@Getter
@Setter
public abstract class ArtemisCheck implements Check {
    public PlayerData data;
    public CheckInformation info;
    private long lastAlert;
    private float verbose;
    private boolean supposeAlert;
    public ArtemisCheck(PlayerData data, CheckInformation info) {
        this.data = data;
        this.info = info;
    }

    /**
     * @param value Corresponds to the amount of verbose you wish to call at once.
     * @param args  Corresponds to the debug information. TODO Make all debug a list of Pair<String, String> for
     *              proper serialization
     */
    public void log(float value, Debug... args) {
        this.verbose(info.getVisualName(), (float) value, args);
    }

    /**
     * @param value Corresponds to the amount of verbose you wish to call at once.
     * @param args  Corresponds to the debug information. TODO Make all debug a list of Pair<String, String> for
     *              proper serialization
     */
    @Deprecated
    public void log(float value, String args) {
        this.verbose(info.getVisualName(), (float) value);
    }

    /**
     * @param args Corresponds to the debug information. TODO Make all debug a list of Pair<String, String> for
     *             proper serialization
     */
    public void log(Debug... args) {
        this.verbose(info.getVisualName(), 1.0F, args);
    }

    /**
     * @param args Corresponds to the debug information.
     *             proper serialization
     */
    @Deprecated
    public void log(String debug, Debug... args) {
        this.verbose(info.getVisualName(), 1.0F, args);
    }


    @Deprecated
    public void log(){
        this.verbose(info.getVisualName(), 1.0F);
    }

    /**
     * @param args Corresponds to the debug information.
     *             proper serialization
     */
    @Deprecated
    public void log(String var, String debug, Debug... args) {
        this.verbose(var, 1.0F, args);
    }

    /**
     * Corresponds to the debug information.
     *             proper serialization
     */
    @Deprecated
    public void log(String var, String debug) {
        this.verbose(var, 1.0F);
    }

    /**
     * Corresponds to the debug information.
     *             proper serialization
     */
    @Deprecated
    public void log(String var, Object... debug) {
        this.verbose(var, 1.0F);
    }

    public void log(String var, float value, Debug<?>... args){
        this.verbose(var, value, args);
    }

    @Deprecated
    public void log(float value) {
        this.verbose("", value);
    }

    public void decrease(float n) {
        this.verbose = Math.max(0, verbose - n);
    }


    private void verbose(String var, float value, Debug<?>... args) {

        // If info is setback, set the value to true
        if (info.isSetback()) {
            data.user.setSetBackX();
        }

        // Increase the verbose
        if (info.getStage().isOrAbove(Stage.RELEASE)) {
            this.verbose += value;
        }

        Log log = new Log(data, this.getInfo(), Severity.VERBOSE, System.currentTimeMillis(), verbose, args);


        // Check if the verbose is high enough for a violation
        if (verbose >= info.getMaxVb()) {

            // Suppose the alert for the next message
            this.supposeAlert = true;

            // Increase the violation. If it does not exist, create it. Add the debug processor to it too!
            log = data.getViolations()
                    .computeIfAbsent(this, v -> new Log(data, this.getInfo(), Severity.VIOLATION,
                            System.currentTimeMillis(), 0, args))
                    .setCountX(data.getViolations().get(this).getCount() + 1)
                    .setTimestampX(System.currentTimeMillis());

            // Make sure the user has met the violation threshold
            if (log.getCount() >= info.getMaxVl()
                    // Make sure the check is bannable
                    && info.isBannable()
                    // Make sure the user is not already banned, this prevents any sort of issue
                    && !data.staff.isBanned()) {
                // Add user to the ban queue
                //Artemis.INSTANCE.getApi().getBanManager().addToQueue(data);
                // Set user as banned
                final Ban ban = new Ban(data.getPlayer().getName(), data.getPlayerID(), System.currentTimeMillis());
                Artemis.v()
                        .getApi()
                        .getAlertManager()
                        .executeBan(ban);
                data.staff.setBanned(!ban.isCancelled());
                return;
            }

            // If check does decrease in verbose, don't reset the verbose
            if (!info.isNoDrop()) {
                this.verbose = 0;
            }
        }

        if (!TimeUtil.elapsed(lastAlert, info.getDelayBetweenAlerts())) {
            return;
        }

        final StaffEnums.StaffAlerts alerts = info.getStage().isOrBelow(Stage.FALSING)
                ? StaffEnums.StaffAlerts.EXPERIMENTAL_VERBOSE
                : supposeAlert ? StaffEnums.StaffAlerts.ALERTS : StaffEnums.StaffAlerts.VERBOSE;

        Artemis.v().getApi().getAlertManager().sendAlert(alerts, log);
        Artemis.v().getApi().getAlertManager().getNotificationProvider().provide(log);

        if (!log.isCancelled()) {
            this.lastAlert = System.currentTimeMillis();
        }

        this.supposeAlert = false;
    }



    public void debug(String debug, Object... args) {
        this.debug(String.format(debug, args));
    }


    public void debug(String... args) {
        if (ConfigManager.isDebugger()) {
            final StringBuilder builder = new StringBuilder("&7[&6&lDEBUG&7]&f &6");
            builder.append(data.getPlayer().getName())
                    .append(" &f&l-> &6")
                    .append(info.getType().name())
                    .append(" &f&l-> &r");

            if (args.length != 0)
                builder.append(args[0]);

            final String finalMsg = Chat.translate(builder.toString());

            for (Map.Entry<PlayerData, List<CheckInformation>> playerDataListEntry : data.staff.debug.asMap().entrySet()) {
                if (!playerDataListEntry.getValue().contains(this.getInfo()))
                    continue;

                playerDataListEntry.getKey().getPlayer().sendMessage(finalMsg);
            }

            for (Map.Entry<PlayerData, List<CheckInformation>> playerDataListEntry : data.staff.logDebug.asMap().entrySet()) {
                if (!playerDataListEntry.getValue().contains(this.getInfo()))
                    continue;

                playerDataListEntry.getKey().staff.getLog().add("["+ Date.from(Instant.now()).toLocaleString() + " | " + info.getType().name() + " " + info.getVar() + "] " + args[0]);
            }

        }
    }

    public void debug(String args) {
        this.debug(args, "x");
    }

    public boolean isDebug(){
        return data.staff.isDebug(this);
    }


    public boolean isNull(CheckType... types) {
        for (CheckType type : types){
            switch (type) {
                case POSITION:
                    if (isNullLocation()) return true;
                    break;
                case ROTATION:
                    if (isNullRotation()) return true;
                    break;
                case VELOCITY:
                    if (isNullVelocity()) return true;
                    break;
                case MOVEMENT:
                    if (isNullMovement()) return true;
                    break;
                default:
                    continue;
            }
        }
        return false;
    }

    private boolean preCheck() {
        //System.out.println("DATA IS NULL ");
        return data == null || data.movement == null;
    }

    public boolean isNullLocation() {
        return (preCheck() || data.movement.getLocation() == null || data.movement.getLastLocation() == null);
    }

    public boolean isNullVelocity() {
        return (preCheck() || data.movement.getVelocity() == null || data.movement.getLastVelocity() == null);
    }

    public boolean isNullRotation() {
        return (preCheck() || data.movement.getRotation() == null || data.movement.getLastRotation() == null);
    }

    public boolean isNullMovement() {
        return (preCheck() || data.movement.getMovement() == null || data.movement.getLastMovement() == null);
    }

    public boolean isExempt(final ExemptType exemptType) {
        return data.getExemptManager().isExempt(exemptType);
    }



    public boolean isExempt(final Function<PlayerData, Boolean> customExempt) {
        return data.getExemptManager().isExempt(customExempt);
    }

    public boolean isExempt(final ExemptType... exemptTypes) {
        return data.getExemptManager().isExempt(exemptTypes);
    }

    public ExemptType[] exemptTypes(){
        Set<ExemptType> exemptTypes = new HashSet<>();

        for (ExemptType type : ExemptType.values()) {
            if (type.getFunction().apply(data)) exemptTypes.add(type);
        }

        return exemptTypes.toArray(new ExemptType[exemptTypes.size()]);
    }


    private void execute(Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }

    @Override
    public boolean canCheck(){
        final boolean flag = info.isEnabled()
                && info.getCompatibleClientVersions().contains(data.getVersion())
                && info.getCompatibleServerVersions().contains(ServerUtil.getGameVersion())
                && info.isCompatibleNMS();
        return flag;
    }
}
