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
import ac.artemis.core.v5.utils.OldMathUtil;
import ac.artemis.core.v5.utils.buffer.Buffer;
import ac.artemis.core.v5.utils.buffer.StandardBuffer;

import java.util.List;

@Check(type = Type.HEURISTICS, var = "IV")
@Experimental(stage = Stage.PRE_RELEASE)
public final class HeuristicsIV extends AbstractAngleHeuristicCheck {
    public HeuristicsIV(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private final List<Integer> deltas = new EvictingLinkedList<>(20);
    private final List<Integer> angles = new EvictingLinkedList<>(20);

    private final Buffer buffer = new StandardBuffer(5)
            .setMax(20)
            .setMin(0)
            .setValue(-5);

    @Override
    public void handle(AngleModal modal) {
        final double deltaXZ = modal.getDeltaXZ();
        final double angle = modal.getAngle();

        flag: {
            final boolean speed = Math.abs(deltaXZ) > 0.1D;

            if (Math.abs(data.sensitivity.deltaX) > 2D && Math.abs(data.sensitivity.deltaY) > 0.5D) {
                this.deltas.add((int) Math.round(data.sensitivity.deltaX));
                this.angles.add((int) (angle / .15D / data.sensitivity.sensitivityX));
            }

            if (!speed){
                break flag;
            }

            final double factor = OldMathUtil.getStandardDeviation(angles) / OldMathUtil.getStandardDeviation(deltas) + 1E-4;

            this.debug(" factor=" + factor);

            final float deltaPitch = Math.abs(data.prediction.getLastPitch() - data.prediction.getPitch());

            if (Math.abs(factor) < 1E-3 && deltaPitch > 5.F) {
                this.buffer.incrementBuffer();

                if (buffer.flag()) {
                    this.log(
                            new Debug<>("factor", factor)
                    );
                }
            } else {
                this.buffer.decreaseBuffer(0.125);
            }
        }
    }
}
