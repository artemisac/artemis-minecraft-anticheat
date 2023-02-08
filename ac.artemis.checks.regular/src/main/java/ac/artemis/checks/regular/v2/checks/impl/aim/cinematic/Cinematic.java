package ac.artemis.checks.regular.v2.checks.impl.aim.cinematic;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.settings.CheckSetting;
import ac.artemis.core.v4.check.templates.rotation.SimpleRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.graphing.GraphUtil;
import ac.artemis.core.v4.utils.position.SimpleRotation;
import ac.artemis.core.v4.utils.time.TimeUtil;

import java.util.ArrayList;

@Check(type = Type.AIM, var = "Cinematic")
public class Cinematic extends SimpleRotationCheck {

    private final ArrayList<Double> yawSamples = new ArrayList<>();
    private final ArrayList<Double> pitchSamples = new ArrayList<>();

    @Setting(type = CheckSettings.CINEMATIC_THRESHOLD, defaultValue = "1000")
    private final CheckSetting thresholdCinematic = info.getSetting(CheckSettings.CINEMATIC_THRESHOLD);
    @Setting(type = CheckSettings.CINEMATIC_RATIO, defaultValue = "400")
    private final CheckSetting thresholdRatio = info.getSetting(CheckSettings.CINEMATIC_RATIO);

    private double lastDeltaYaw, lastDeltaPitch;
    private long lastSmooth, lastHighRate;

    public Cinematic(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handleRotation(final SimpleRotation from, final SimpleRotation to) {
        final long now = packet.getTimestamp();

        final double deltaYaw = Math.abs(to.getYaw() - from.getYaw());
        final double deltaPitch = Math.abs(to.getPitch() - from.getPitch());

        final double differenceYaw = Math.abs(deltaYaw - lastDeltaYaw);
        final double differencePitch = Math.abs(deltaPitch - lastDeltaPitch);

        final double joltYaw = Math.abs(differenceYaw - deltaYaw);
        final double joltPitch = Math.abs(differencePitch - deltaPitch);

        final boolean cinematic = (now - lastHighRate > thresholdCinematic.getAsInt())
                || now - lastSmooth < thresholdRatio.getAsInt();

        debug("lastSmooth=" + TimeUtil.elapsed(lastSmooth) + " lastHigh=" + TimeUtil.elapsed(lastHighRate));

        if (joltYaw > 1.0 && joltPitch > 1.0) {
            this.lastHighRate = now;
        }

        if (deltaPitch > 0.0 && deltaYaw > 0.0) {
            yawSamples.add(deltaYaw);
            pitchSamples.add(deltaPitch);
        }

        if (yawSamples.size() == 20 && pitchSamples.size() == 20) {
            // Get the cerberus/positive graph of the sample-lists
            final GraphUtil.GraphResult resultsYaw = GraphUtil.getGraph(yawSamples);
            final GraphUtil.GraphResult resultsPitch = GraphUtil.getGraph(pitchSamples);

            // Negative values
            final int negativesYaw = resultsYaw.getNegatives();
            final int negativesPitch = resultsPitch.getNegatives();

            // Positive values
            final int positivesYaw = resultsYaw.getPositives();
            final int positivesPitch = resultsPitch.getPositives();

            // Cinematic camera usually does this on *most* speeds and is accurate for the most part.
            if (positivesYaw > negativesYaw || positivesPitch > negativesPitch) {
                this.lastSmooth = now;
            }

            // Debug the values
            debug("y=+" + positivesYaw + "-" + negativesYaw + " p=+" + positivesPitch + "-" + negativesPitch);

            yawSamples.clear();
            pitchSamples.clear();
        }

        data.getCombat().setCinematic(cinematic);

        this.lastDeltaYaw = deltaYaw;
        this.lastDeltaPitch = deltaPitch;
    }
}
