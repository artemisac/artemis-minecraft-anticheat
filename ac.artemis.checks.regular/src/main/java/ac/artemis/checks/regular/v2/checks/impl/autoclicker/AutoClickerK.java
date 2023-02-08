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

import java.util.Deque;
import java.util.LinkedList;

@Check(type = Type.AUTOCLICKER, var = "K")
@Experimental
public class AutoClickerK  extends ArtemisCheck implements PacketHandler {
    private long lastArmAnimation = System.currentTimeMillis(), lastDelay;
    private double buffer = 0.0;

    private final Deque<Long> samples = new LinkedList<>();

    public AutoClickerK(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientArmAnimation) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastArmAnimation;

            if (delay > 1L && delay < 140L && !data.user.isFakeDigging() && !data.user.isDigging()) {
                final long accelerations = Math.abs(delay - lastDelay);

                samples.add(accelerations);
            }

            if (samples.size() == 20) {
                final long duplicates = samples.size() - samples.stream().distinct().count();

                if (duplicates == 0L || duplicates == 20) {
                    if (++buffer > 1) {
                        this.log("d=" + duplicates);
                    }
                } else {
                    buffer = Math.max(buffer - 0.5, 0);
                }
            }

            this.lastDelay = delay;
            this.lastArmAnimation = now;
        }
    }
}
