package ac.artemis.checks.enterprise.heuristics;

import ac.artemis.anticheat.api.check.type.Stage;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.checks.enterprise.heuristics.modal.AbstractAngleHeuristicCheck;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.lists.EvictingLinkedList;
import ac.artemis.core.v5.utils.buffer.Buffer;
import ac.artemis.core.v5.utils.buffer.StandardBuffer;

import java.util.List;

/**
 * @author Ghast
 * @since 02/08/2021
 * Artemis © 2020
 *
 * I have no idea what went through my mind whilst making this check. This is entirely
 * debugged values. I regret making this. It looks like auto-clicker b but hey, it somewhat
 * works? The fuck I know about statistics. This is horrendous.
 */
@Check(type = Type.HEURISTICS, var = "III")
@Experimental(stage = Stage.PRE_RELEASE)
public final class HeuristicsIII extends AbstractAngleHeuristicCheck {
    public HeuristicsIII(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private final List<Double> samples = new EvictingLinkedList<>(20);
    private final List<Double> deviation = new EvictingLinkedList<>(20);
    private final List<Integer> deltas = new EvictingLinkedList<>(20);
    private double lastAngle;

    private final Buffer buffer = new StandardBuffer(5)
            .setMax(20)
            .setMin(0)
            .setValue(-5);

    @Override
    public void handle(AngleModal modal) {
//        final double deltaXZ = modal.getDeltaXZ();
//        final double scaledReach = modal.getScaledAngle();
//        final double angle = modal.getAngle();
//
//        flag: {
//            final boolean speed = Math.abs(deltaXZ) > 0.1D;
//
//            if (Math.abs(data.sensitivity.deltaX) > 0.5D) {
//                this.deltas.add((int) Math.round(data.sensitivity.deltaX));
//            }
//            this.samples.add(scaledReach);
//
//            final double deviation = OldMathUtil.getStandardDeviation(samples);
//            this.deviation.add(deviation);
//
//
//            if (!speed){
//                break flag;
//            }
//
//            if (samples.isEmpty())
//                break flag;
//
//            final double selfDerivative = MathUtil.getStandardDeviation(this.deviation);
//            final double fluctuation = MathUtil.getFluctuation(samples);
//            final double skew = MathUtil.getSkewness(samples);
//            val outliers = MathUtil.getOutliers(samples);
//            final int outlierCount = outliers.getX().size() + outliers.getY().size();
//            final int duplicates = MathUtil.getDuplicates(deltas);
//            final int out = MathUtil.getOutliers2(deltas);
//
//            final double deltaAngle = Math.abs(lastAngle - angle);
//
//            final double scalar = data.sensitivity.deltaX / deltaAngle;
//
//            final double kurtosis = MathUtil.getKurtosis(samples);
//
//            this.debug("deviation=" + deviation
//                    + " fluct=" + fluctuation
//                    + " skew=" + skew
//                    + " outliers=" + outlierCount
//                    + " kurtosis=" + kurtosis
//                    + " selfDeriv=" + selfDerivative
//                    + " dups=" + duplicates
//                    + " dups2=" + out
//                    + " scalar=" + scalar
//            );
//
//            final boolean flag = outlierCount > 5
//                    && out > 15
//                    && scalar > 1000
//                    && selfDerivative < 0.1
//                    && duplicates > 0
//                    && Math.abs(skew) < 0.2D;
//
//            if (flag) {
//                this.buffer.incrementBuffer();
//
//                if (buffer.flag()) {
//                    this.log(
//                            new Debug<>("deviation", deviation),
//                            new Debug<>("fluct", fluctuation),
//                            new Debug<>("skew", skew),
//                            new Debug<>("outliers", outlierCount),
//                            new Debug<>("kurtosis", kurtosis),
//                            new Debug<>("selfDeriv", selfDerivative),
//                            new Debug<>("dups", duplicates),
//                            new Debug<>("dups2", out),
//                            new Debug<>("scalar", scalar)
//                    );
//                }
//            } else {
//                this.buffer.decreaseBuffer(0.125);
//            }
//        }
//
//        this.lastAngle = angle;
    }
}
