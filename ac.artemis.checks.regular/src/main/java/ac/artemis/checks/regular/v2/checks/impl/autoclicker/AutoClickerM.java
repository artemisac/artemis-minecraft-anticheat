package ac.artemis.checks.regular.v2.checks.impl.autoclicker;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import com.google.common.collect.Lists;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;

import java.util.LinkedList;

@Check(type = Type.AUTOCLICKER, var = "M")
public class AutoClickerM  extends ArtemisCheck implements PacketHandler {
    private int ticks = 0, lastTicks = 0;
    private double lastAverage, lastDeviation, lastKurtosis, lastSkewness, buffer;

    private final LinkedList<Double> samples = Lists.newLinkedList();

    public AutoClickerM(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientArmAnimation) {
            final boolean valid = ticks < 4 && lastTicks < 4 && !data.user.isDigging() && !data.user.isPlaced();

            if (valid) {
                final double delta = Math.abs(ticks - lastTicks);

                samples.add(delta);
            }

            if (samples.size() == 10) {
                final double average = samples.stream().mapToDouble(d -> d).average().orElse(0.0);
                final double deviation = MathUtil.getStandardDeviation(samples);

                final double kurtosis = new Kurtosis().evaluate(samples.stream().mapToDouble(Number::doubleValue).toArray());
                final double skewness = new Skewness().evaluate(samples.stream().mapToDouble(Number::doubleValue).toArray());

                if (average < 2.5 & average > 1.5 && average == lastAverage && deviation == lastDeviation && kurtosis == lastKurtosis && skewness == lastSkewness && deviation < 30.d) {
                    if (++buffer > 4) {
                        this.log(
                                new Debug<>("deviation", deviation),
                                new Debug<>("average", average),
                                new Debug<>("kurtosis", kurtosis),
                                new Debug<>("skewness", skewness)
                        );
                    }
                } else {
                    buffer = Math.max(buffer - 0.45, 0);
                }

                this.lastKurtosis = kurtosis;
                this.lastSkewness = skewness;
                this.lastAverage = average;
                this.lastDeviation = deviation;
            }

            this.lastTicks = ticks;
            this.ticks = 0;
        } else if (packet instanceof PacketPlayClientFlying) {
            ++ticks;
        }
    }
}
