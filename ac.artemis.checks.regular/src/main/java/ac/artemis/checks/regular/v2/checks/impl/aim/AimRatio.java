package ac.artemis.checks.regular.v2.checks.impl.aim;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.settings.CheckSetting;
import ac.artemis.core.v4.check.templates.rotation.SimpleRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.SimpleRotation;

import java.util.Deque;
import java.util.LinkedList;

@Check(type = Type.AIM, var = "Inconsistent")
public class AimRatio extends SimpleRotationCheck {

    private final Deque<Float> yawSamples = new LinkedList<>();
    private final Deque<Float> pitchSamples = new LinkedList<>();

    @Setting(type = CheckSettings.RATIO_COMBINED_THRESHOLD, defaultValue = "0")
    private final CheckSetting threshold = info.getSetting(CheckSettings.RATIO_COMBINED_THRESHOLD);

    private int buffer = 0;

    public AimRatio(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handleRotation(final SimpleRotation from, final SimpleRotation to) {
        final long now = System.currentTimeMillis();

        final float deltaYaw = Math.abs(from.getYaw() - to.getYaw());
        final float deltaPitch = Math.abs(from.getPitch() - to.getPitch());

        final boolean action = now - data.getCombat().getLastAttack() < 300L || now - data.user.lastPlace < 300L;
        final boolean cinematic = data.getCombat().isCinematic();

        if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f && !cinematic && action) {
            this.yawSamples.add(deltaYaw);
            this.pitchSamples.add(deltaPitch);
        }

        if (yawSamples.size() == 20 && pitchSamples.size() == 20) {
            final long distinctYaw = yawSamples.stream().distinct().count();
            final long distinctPitch = pitchSamples.stream().distinct().count();

            final long duplicates = (yawSamples.size() - distinctYaw) + (pitchSamples.size() - distinctPitch);

            if (duplicates < threshold.getAsDouble()) {
                if (++buffer > 6) {
                    this.log("d=" + duplicates);
                }
            }

            this.yawSamples.clear();
            this.pitchSamples.clear();
        }
    }
}
