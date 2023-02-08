package ac.artemis.checks.regular.v2.checks.impl.autoclicker;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.graphing.GraphUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Elevated
 * @since 15-Apr-20
 */

@Check(type = Type.AUTOCLICKER, var = "F", threshold = 5)
public class AutoClickerF  extends ArtemisCheck implements PacketHandler {

    public AutoClickerF(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private double cps, lastCps;
    private int ticks, vl;

    private final List<Double> cpsSamples = new ArrayList<>();

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientArmAnimation) {
            if (!data.user.isDigging() && !data.user.isFakeDigging()) {
                this.cps++;
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            if (++this.ticks == 20) {
                // Make sure the impl is clicking on an appropriate cps and then sample.
                if (this.cps > 9) {
                    this.cpsSamples.add(cps);
                    // If the samples count is 10
                    if (this.cpsSamples.size() == 10) {
                        // Get the graphical result of the impl's clicks
                        final GraphUtil.GraphResult results = GraphUtil.getGraph(cpsSamples);

                        // Get the negatives from the graph
                        final int negatives = results.getNegatives();
                        final double average = this.cpsSamples.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

                        final double deltaCps = Math.abs(average - this.cps);

                        // Impossible.
                        if (negatives == 1 && deltaCps <= 1) {
                            if (++vl > 2) {
                                log("vl=" + vl + " %deltaCPS=" + deltaCps + " %av=" + average + " %cps=" + cps);
                            }
                        } else {
                            vl = 0;
                            decrease(0.05f);
                        }
                        // Clear the samples/
                        this.cpsSamples.clear();
                    }
                }
                this.ticks = 0;
                this.cps = 0;
            }
        }
    }
}
