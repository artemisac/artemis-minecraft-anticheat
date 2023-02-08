package ac.artemis.checks.enterprise.heuristics;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.checks.enterprise.heuristics.modal.AbstractAngleHeuristicCheck;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.utils.buffer.Buffer;
import ac.artemis.core.v5.utils.buffer.StandardBuffer;

/**
 * @author Ghast
 * @since 02/08/2021
 * Artemis © 2020
 *
 * This check is relatively simple. Most auras aim for a specific middle point
 * in the enemy outbox. Thanks to our sophisticated reach system, we essentially
 * can mitigate this by checking if this middle point is the target of every attack.
 * By establishing the offset using angle * distance, we can *somewhat* get the
 * offset. Then on, we can do some buffer work for leniency and have an excellent
 * detection.
 */
@Check(type = Type.HEURISTICS, var = "I")
public final class HeuristicsI extends AbstractAngleHeuristicCheck {
    public HeuristicsI(PlayerData data, CheckInformation info) {
        super(data, info);
    }

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
            final boolean speed = Math.abs(deltaXZ) > 0.2D;

            if (!speed){
                this.debug("speed=" + deltaXZ);
                break flag;
            }

            final boolean flag = scaledReach < 0.01D;

            if (flag) {
                buffer.increaseBuffer(3.D * (1.D - scaledReach * 33.D));
            } else {
                buffer.decrementBuffer();
                break flag;
            }

            if (!buffer.flag()) {
                break flag;
            }

            this.debug("scaled=" + scaledReach);

            this.log(
                    new Debug<>("buffer", buffer.get()),
                    new Debug<>("scaled", scaledReach),
                    new Debug<>("angle", angle),
                    new Debug<>("delta", deltaXZ)
            );
        }
    }
}
