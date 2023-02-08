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
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;
import ac.artemis.core.v4.utils.lists.EvictingArrayList;
import ac.artemis.core.v4.utils.time.TimeUtil;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;

/**
 * @author Ghast
 * @since 21-Mar-20
 */

@Check(type = Type.AUTOCLICKER, var = "C", threshold = 10)
@Experimental(stage = Stage.PRE_RELEASE)
public class AutoClickerC  extends ArtemisCheck implements PacketHandler {

    public AutoClickerC(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private final List<Double> rates = new EvictingArrayList<>(100);
    private double ticks;
    private int verbose;
    private double lastVariance, lastKurtosis;

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientArmAnimation) {
            if (invalid()) return;

            this.rates.add(ticks);
            this.ticks = 0;

            if (this.rates.size() >= 100) {
                final DescriptiveStatistics stats = new DescriptiveStatistics(rates.stream().mapToDouble(Number::doubleValue).toArray());
                final double kurtosis = stats.getKurtosis();
                final double sum = stats.getSum();
                final double variance = stats.getVariance();

                final double deltaK = Math.abs(kurtosis - lastKurtosis);
                final double deltaV = Math.abs(variance - lastVariance);


                if (kurtosis < -0.05 && sum > 150 && sum < 300 && variance < 0.7215 && deltaV > 0.1) {
                    if (verbose++ > 3) {
                        log("k=" + kurtosis + " vb=" + verbose + " sum=" + sum + "%" + variance
                                + " kd=" + deltaK + " vd=" + deltaV);
                    }
                } else {
                    if (verbose > 0) verbose--;
                }

                this.debug("k=" + kurtosis + " vb=" + verbose + " sum=" + sum + "%" + variance);

                this.lastKurtosis = kurtosis;
                this.lastVariance = variance;
            }

        } else if (packet instanceof PacketPlayClientFlying) {
            if (invalid()) return;
            this.ticks++;
        }
    }


    private boolean invalid() {
        return data.user.isDigging() || data.user.isPlaced() || !TimeUtil.hasExpired(data.user.getLastDig(), 1);
    }
}
