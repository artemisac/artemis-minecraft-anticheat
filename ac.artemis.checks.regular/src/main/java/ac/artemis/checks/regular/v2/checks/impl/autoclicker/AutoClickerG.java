package ac.artemis.checks.regular.v2.checks.impl.autoclicker;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;
import ac.artemis.core.v4.utils.time.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Elevated
 * @since 15-Apr-20
 */

@Check(type = Type.AUTOCLICKER, var = "G", threshold = 5)
@Experimental
public class AutoClickerG  extends ArtemisCheck implements PacketHandler {

    public AutoClickerG(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private double cps, lastCps;
    private int ticks, vl;

    private final List<Double> cpsSamples = new ArrayList<>();

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientArmAnimation) {

            boolean digging = !TimeUtil.elapsed(data.user.getLastDig(), 250L)
                    || data.user.isFakeDigging() || data.user.isDigging();

            if (!digging) {
                this.cps++;
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            if (++this.ticks == 20) {
                // Making sure the impl is clicking on a cps that is acceptable and isn't digging.
                if (this.cps > 0) {
                    this.cpsSamples.add(cps);
                    // Get the average cps and the ratio of the clicks
                    final double cpsAverage = this.cpsSamples.stream().mapToDouble(Number::doubleValue).average().orElse(0.0);
                    final double ratio = cpsAverage / this.cps;

                    // If the cps is > 8 (current) and the average is also >= 8
                    if (this.cps > 8 && cpsAverage >= 8) {
                        // Impossible ratio consistency
                        if (ratio > 0.99) {
                            if (++vl > 5) {
                                log("vl=" + vl + " %ratio=" + ratio + " %cps=" + cps + " %av=" + cpsAverage);
                            }
                        } else {
                            vl = 0;
                        }
                    }
                }
                ticks = 0;
                cps = 0;
            }
        }
    }
}
