package ac.artemis.core.v5.logging.model;

import ac.artemis.anticheat.api.alert.Alert;
import ac.artemis.anticheat.api.alert.Severity;
import ac.artemis.anticheat.api.check.CheckInfo;
import ac.artemis.anticheat.api.check.type.Stage;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.debug.writer.DebugWriter;
import ac.artemis.core.v4.check.debug.writer.DebugWriterFactory;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.lag.LagManager;
import ac.artemis.core.v4.theme.ThemeManager;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author Ghast
 * @since 16-Mar-20
 */
@Getter
@Setter
public class Log implements Alert {
    private static final DebugWriter debugWriter = new DebugWriterFactory().build();

    @SerializedName("uuid")
    private UUID uuid;

    @SerializedName("username")
    private String username;

    @SerializedName("checkName")
    private String checkName;

    @SerializedName("checkVar")
    private String checkVar;

    @SerializedName("timestamp")
    private Long timestamp; // Why marshall values? Because screw you! GSON!

    @SerializedName("verbose")
    private Float count;

    @SerializedName("debug")
    private List<Debug<?>> debug;

    private transient PlayerData data;
    private transient CheckInfo checkInfo;
    private transient Severity severity;
    private transient boolean cancelled;

    public Log(final PlayerData data, final CheckInfo checkInfo, final Severity severity,
               final long timestamp, final float count, final Debug<?>... debug) {
        this(data, checkInfo, severity, timestamp, count, Arrays.asList(debug));
    }

    public Log(final PlayerData data, final CheckInfo checkInfo, final Severity severity,
               final long timestamp, final float count, final List<Debug<?>> debug) {
        this.data = data;
        this.username = data.getPlayer().getName();
        this.checkInfo = checkInfo;
        this.uuid = data.getPlayerID();
        this.checkName = checkInfo.getVisualCategory();
        this.checkVar = checkInfo.getVisualName();
        this.severity = severity;
        this.timestamp = timestamp;
        this.count = count;
        this.debug = Collections.unmodifiableList(debug);
    }

    public Log setTimestampX(final Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Log setCountX(final float count) {
        if (count <= 0) return this;
        this.count = count;
        return this;
    }

    @Override
    public Severity getSeverity() {
        return severity;
    }

    @Override
    public CheckInfo getCheck() {
        return checkInfo;
    }

    @Override
    public int count() {
        return count.intValue();
    }

    public Log copy() {
        return new Log(data, checkInfo, severity, timestamp, count, debug);
    }

    @Override
    public String toMinecraftMessage() {
        final String debug = debugWriter.write(this.debug);
        return editMessage(severity == Severity.VIOLATION
                ? ThemeManager.getCurrentTheme().getViolationMessage()
                : ThemeManager.getCurrentTheme().getVerboseMessage(),
                checkVar, count.intValue(), debug);
    }

    private String editMessage(final String s, final String var, final int count, final String args) {
        return s.replace("%user%", data.getPlayer().getName())
                .replace("%check%", checkInfo.getVisualCategory())
                .replace("%type%", var.replace("Simple", "").replace("Complex", "X"))
                .replace("%ids%", args)
                .replace("%ping%", Integer.toString(LagManager.getPing(data.getPlayer())))
                .replace("%vl%", checkInfo.getStage().isOrBelow(Stage.FALSING)
                        ? "EXPERIMENTAL" : Integer.toString(Math.round(count))
                );
    }
}
