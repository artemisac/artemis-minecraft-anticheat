package ac.artemis.checks.regular.v2.checks.impl.aim;

import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.rotation.SimpleRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.SimpleRotation;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

@Check(type = Type.AIM, var = "InvalidSmooth")
@Experimental
public class AimInvalidSmooth extends SimpleRotationCheck {
    private final Deque<Float> yawSamples = new LinkedList<>();
    private final Deque<Float> pitchSamples = new LinkedList<>();

    @Setting(type = CheckSettings.MIN_LEVEL_THRESHOLD, defaultValue = "90")
    public double minimumLevel = info.getSetting(CheckSettings.MIN_LEVEL_THRESHOLD).getAsDouble();

    public AimInvalidSmooth(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handleRotation(final SimpleRotation from, final SimpleRotation to) {
        final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
        final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

        final boolean cinematic = data.getCombat().isCinematic();

        if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f && !cinematic) {
            this.yawSamples.add(deltaYaw);
            this.pitchSamples.add(deltaPitch);
        }

        flag: {
            if (yawSamples.size() < 128 || pitchSamples.size() < 128)
                break flag;

            final AtomicInteger level = new AtomicInteger(0);

            this.yawSamples.stream().filter(delta -> delta != 0.0 && delta < 0.001).forEach(delta -> level.incrementAndGet());
            this.pitchSamples.stream().filter(delta -> delta != 0.0 && delta < 0.001).forEach(delta -> level.incrementAndGet());

            final double averageYaw = yawSamples.stream().mapToDouble(d -> d).average().orElse(0.0);
            final double averagePitch = pitchSamples.stream().mapToDouble(d -> d).average().orElse(0.0);

            final boolean invalid = averageYaw < 1.1 && averageYaw > 0.0 || averagePitch <= 0.01;

            if (invalid && level.get() >= minimumLevel){
                this.log("y=" + averageYaw + " p=" + averagePitch + " l=" + level.get());
            }

            this.yawSamples.clear();
            this.pitchSamples.clear();
        }
    }
}
