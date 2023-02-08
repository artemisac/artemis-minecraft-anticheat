package ac.artemis.checks.regular.v2.checks.impl.autoclicker;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.anticheat.api.check.type.Stage;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import ac.artemis.core.v4.utils.time.TimeUtil;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author Elevated
 * @since 15-Apr-20
 */

@Check(type = Type.AUTOCLICKER, var = "H", threshold = 5)
@Experimental(stage = Stage.FALSING)
public class AutoClickerH  extends ArtemisCheck implements PacketHandler {

    public AutoClickerH(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private long lastSwing;
    private double lastDeviation, lastAverage;
    private int vl;
    private final Deque<Long> clickSamples = new LinkedList<>();

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientArmAnimation) {
            // Get swing delay
            final long now = System.currentTimeMillis();
            final long delay = now - this.lastSwing;

            boolean digging = !TimeUtil.elapsed(data.user.getLastDig(), 250L)
                    || data.user.isFakeDigging() || data.user.isDigging();

            if (digging)
                return;

            // Making sure the delay is big enough, isn't digging and the sample got added and the size is 20
            if (delay > 1L && data.combat.getLastCps() > 6 && delay < 200L && this.clickSamples.add(delay) && this.clickSamples.size() == 30) {
                // Get the average
                final double average = this.clickSamples.stream().mapToDouble(Long::doubleValue).average().orElse(0.0);

                // Standard deviation
                double stdDeviation = 0.0;

                // Get all clicks and get the true standardDeviation.
                for (Long click : this.clickSamples) {
                    stdDeviation += Math.pow(click.doubleValue() - average, 2);
                }

                // Divide by size (https://en.wikipedia.org/wiki/Standard_deviation)
                stdDeviation /= this.clickSamples.size();

                // Get the square root of the deviation (https://en.wikipedia.org/wiki/Standard_deviation) and the ascension.
                final double sqrtDeviation = Math.sqrt(stdDeviation);

                final double deltaDeviation = Math.abs(sqrtDeviation - lastDeviation);
                final double deltaAverage = Math.abs(average - this.lastAverage);

                final double delta = Math.abs(deltaDeviation - deltaAverage);

                // Technically impossible
                if (sqrtDeviation < 30.d && deltaDeviation <= 4 && deltaAverage < 11.d && delta > 0.5) {
                    if (++vl > 3) {
                        log(2, "sqrtDev=" + sqrtDeviation
                                + " %dDev=" + deltaDeviation + " %dAv=" + deltaAverage + " %digging=" + digging);
                    }
                } else {
                    vl = 0;
                }

                // Pass and clear.
                this.lastDeviation = sqrtDeviation;
                this.lastAverage = average;
                this.clickSamples.clear();
            }

            // Set last swing timestamp
            this.lastSwing = now;
        }
    }
}
