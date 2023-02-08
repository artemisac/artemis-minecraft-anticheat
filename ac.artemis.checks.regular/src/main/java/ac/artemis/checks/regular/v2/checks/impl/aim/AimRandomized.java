package ac.artemis.checks.regular.v2.checks.impl.aim;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.movement.ComplexMovementCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.PlayerMovement;

import java.util.Deque;
import java.util.LinkedList;

@Check(type = Type.AIM, var = "Randomization")
public class AimRandomized extends ComplexMovementCheck {
    private final Deque<Float> pitchSamples = new LinkedList<>();

    private double buffer = 0.0;
    private double lastAverage = 0.0;

    public AimRandomized(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handleMovement(final PlayerMovement from, final PlayerMovement to) {
        final long now = System.currentTimeMillis();

        final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
        final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

        final boolean cinematic = data.getCombat().isCinematic();
        final boolean action = now - data.combat.getLastAttack() < 500L || now - data.user.lastPlace < 500L;

        if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 40.f && deltaPitch < 30.f && !cinematic && action) {
            pitchSamples.add(deltaPitch);
        }

        if (pitchSamples.size() == 20) {
            final double deviation = MathUtil.getStandardDeviation(pitchSamples);
            final double average = pitchSamples.stream().mapToDouble(d -> d).average().orElse(0.0);

            final double deviationSquared = Math.sqrt(deviation);
            final double averageDelta = Math.abs(average - lastAverage);

            if (deviationSquared > 6.f && averageDelta > 1.f) {
                if (++buffer > 5) {
                    log("d=" + deviationSquared + " a=" + averageDelta);
                }
            } else {
                buffer = Math.max(buffer - 0.25, 0);
            }

            debug("d=" + deviationSquared + " a=" + averageDelta);

            lastAverage = average;
            pitchSamples.clear();
        }
    }
}
