package ac.artemis.checks.enterprise.heuristics;

import ac.artemis.anticheat.api.check.type.Stage;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.checks.enterprise.heuristics.modal.AbstractAngleHeuristicCheck;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.lists.EvictingLinkedList;
import ac.artemis.core.v5.utils.MathUtil;
import ac.artemis.core.v5.utils.OldMathUtil;
import ac.artemis.core.v5.utils.buffer.Buffer;
import ac.artemis.core.v5.utils.buffer.StandardBuffer;
import lombok.val;

import java.util.List;

/**
 * @author Ghast
 * @since 02/08/2021
 * Artemis © 2020
 *
 * I have no idea what went through my mind whilst making this check. It doesn't seem to
 * false too often though I'll keep it in heuristic stage. It seems the skewness of the
 * deltas in contrast to the distance seems to be close, indicating a relationship between
 * the changes, which would be characteristic of an aura. The closer a target is, the lower
 * the radius is, the lower the aim changes are. This simple rule of equivalence wouldn't
 * apply for a human as their mouse movements will be consistent as to be able to remain
 * on the target's hit-box, not the target's middle-point.
 */
@Check(type = Type.HEURISTICS, var = "II")
@Experimental(stage = Stage.PRE_RELEASE)
public final class HeuristicsII extends AbstractAngleHeuristicCheck {
    public HeuristicsII(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private final List<Double> samples = new EvictingLinkedList<>(20);
    private final List<Double> deviation = new EvictingLinkedList<>(20);
    private final List<Integer> deltas = new EvictingLinkedList<>(20);
    private final List<Integer> angles = new EvictingLinkedList<>(20);

    private double lastAngle;

    private final Buffer buffer = new StandardBuffer(15)
            .setMax(20)
            .setMin(0)
            .setValue(-5);

    @Override
    public void handle(AngleModal modal) {
        final double deltaXZ = modal.getDeltaXZ();
        final double scaledReach = modal.getScaledAngle();
        final double angle = modal.getAngle();

        flag: {
            final boolean speed = Math.abs(deltaXZ) > 0.1D;

            if (Math.abs(data.sensitivity.deltaX) > 0.5D) {
                this.deltas.add((int) Math.round(data.sensitivity.deltaX));
                this.angles.add((int) (angle / .15D / data.sensitivity.sensitivityX));
            }

            this.samples.add(scaledReach);

            final double deviation = MathUtil.getStandardDeviation(samples);
            this.deviation.add(deviation);

            if (!speed){
                //this.debug("speed=" + deltaXZ);
                break flag;
            }

            final double selfDerivative = MathUtil.getStandardDeviation(this.deviation);

            final double fluctuation = MathUtil.getFluctuation(samples);
            final double skew = MathUtil.getSkewness(samples);
            val outliers = MathUtil.getOutliers(samples);
            final int outlierCount = outliers.getX().size() + outliers.getY().size();
            final int duplicates = MathUtil.getDuplicates(deltas);
            final int out = MathUtil.getOutliers2(deltas);
            final double deltaAngle = Math.abs(lastAngle - angle);
            final double scalar = data.sensitivity.deltaX / deltaAngle;
            final double kurtosis = MathUtil.getKurtosis(samples);
            final double factor = MathUtil.getStandardDeviation(angles) / MathUtil.getStandardDeviation(deltas) + 1E-4;

            this.debug("deviation=" + deviation
                    + " fluct=" + fluctuation
                    + " skew=" + skew
                    + " outliers=" + outlierCount
                    + " kurtosis=" + kurtosis
                    + " selfDeriv=" + selfDerivative
                    + " dups=" + duplicates
                    + " dups2=" + out
                    + " scalar=" + scalar
                    + " factor=" + factor
            );

            if (Math.abs(skew) < 1E-4 && skew != 0) {
                this.log(
                        new Debug<>("skew", skew)
                );
            }
        }

        this.lastAngle = angle;
    }
}
