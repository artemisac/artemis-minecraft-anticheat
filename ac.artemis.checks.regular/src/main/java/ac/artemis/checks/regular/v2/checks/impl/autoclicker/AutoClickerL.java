package ac.artemis.checks.regular.v2.checks.impl.autoclicker;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import ac.artemis.core.v4.utils.lists.EvictingLinkedList;
import ac.artemis.core.v4.utils.maths.MathUtil;

@Check(type = Type.AUTOCLICKER, var = "L")
public class AutoClickerL  extends ArtemisCheck implements PacketHandler {
    private long lastArmAnimation = System.currentTimeMillis();
    private final EvictingLinkedList<Double> samples = new EvictingLinkedList<>(20);

    private double buffer = 0.0;

    public AutoClickerL(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientArmAnimation) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastArmAnimation;

            final double cps = data.getCombat().getCps();

            if (delay > 0L && delay < 170L && !this.isExempt(ExemptType.INTERACT, ExemptType.TELEPORT)) {
                samples.add(cps);
            }

            if (samples.size() > 10) {
                final double average = samples.stream().mapToDouble(d -> d).average().orElse(0.0);
                final double deviation = MathUtil.getStandardDeviation(samples);

                if (average % 1.0 == 0.0 && average > 5 && deviation < 30.d) {
                    if (++buffer > 5) {
                        this.log("a= " + average + " d=" + deviation);
                    }
                } else {
                    buffer = Math.max(buffer - 1.2, 0.0);
                }
            }

            this.lastArmAnimation = now;
        }
    }
}
