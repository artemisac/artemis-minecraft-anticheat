package ac.artemis.checks.regular.v2.checks.impl.aura;

import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.settings.CheckSetting;
import ac.artemis.core.v4.check.templates.rotation.SimpleRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.SimpleRotation;
import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;

@Check(type = Type.AURA, var = "Inconsistent")
@Experimental
public class AuraInconsistent extends SimpleRotationCheck {
    private final LinkedList<Float> samples = Lists.newLinkedList();

    public AuraInconsistent(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Setting(type = CheckSettings.MAX_STREAK, defaultValue = "2")
    private final CheckSetting bufferSetting = this.getInfo().getSetting(CheckSettings.MAX_STREAK);

    private float buffer;

    @Override
    public void handleRotation(final SimpleRotation from, final SimpleRotation to) {
        final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
        final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

        if (deltaYaw > 0.0 && deltaPitch > 0.0) {
            this.samples.add(deltaPitch);
        }

        if (samples.size() == 20) {
            final List<Double> outliersLow = MathUtil.getOutliers(samples).a();
            final List<Double> outliersHigh = MathUtil.getOutliers(samples).b();

            final int combined = outliersLow.size() + outliersHigh.size();

            if (combined > 15 || combined == 0) {
                if (buffer++ > bufferSetting.getAsDouble()) {
                    this.log("c=" + combined);
                }
            } else {
                this.buffer = buffer > 0 ? buffer - 0.25F : 0F;
            }

            debug("Combined=" + combined + " low=" + outliersLow.size() + " high=" + outliersHigh.size());

            this.samples.clear();
        }
    }
}
