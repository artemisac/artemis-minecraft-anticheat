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

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author Elevated
 * @since 15-Apr-20
 */

@Check(type = Type.AUTOCLICKER, var = "E", threshold = 5)
@Experimental(stage = Stage.FALSING)
public class AutoClickerE  extends ArtemisCheck implements PacketHandler {
    private final Deque<Long> swingSamples = new LinkedList<>();

    public AutoClickerE(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private long lastSwing;
    private double lastAverage;
    private double vb;

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientArmAnimation) {
            final long now = System.currentTimeMillis();
            final long delay = now - this.lastSwing;

            // Make sure the impl isn't digging, make sure the delay got added to the sample and the sample size is 20
            if (!data.user.isDigging() && this.swingSamples.add(delay) && this.swingSamples.size() == 20) {
                // Get the delay average
                double average = this.swingSamples.stream().mapToLong(l -> l).average().orElse(0.0);

                // Get the swing deviation
                double totalSwings = this.swingSamples.stream().mapToLong(change -> change).asDoubleStream().sum();
                double mean = totalSwings / this.swingSamples.size();
                double deviation = this.swingSamples.stream().mapToLong(change -> change).mapToDouble(change -> Math.pow(change - mean, 2)).sum();

                // Impossible (technically)
                if (Math.sqrt(deviation) < 150.0 && average > 100.0 && Math.abs(average - this.lastAverage) <= 5.0) {
                    if (vb++ > 2) {
                        log("deviation=" + deviation + " mean=" + mean + " average=" + average + " sqrt=" + Math.sqrt(deviation));
                    }
                } else {
                    vb = vb > 0 ? vb - 0.125 : 0;
                }

                // Pass average and clear list
                this.lastAverage = average;
                this.swingSamples.clear();
            }

            // Set last swing-timestamp
            this.lastSwing = now;
        }
    }
}
