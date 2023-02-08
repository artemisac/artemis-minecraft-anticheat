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
import java.util.concurrent.atomic.AtomicInteger;

@Check(type = Type.AUTOCLICKER, var = "I")
@Experimental
public class AutoClickerI  extends ArtemisCheck implements PacketHandler {
    private long lastArmAnimation = System.currentTimeMillis();

    private final Deque<Long> animationDeque = new LinkedList<>();

    public AutoClickerI(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientArmAnimation) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastArmAnimation;

            if (!data.user.isDigging() && !data.user.isFakeDigging()) {
                animationDeque.add(delay);
            }

            if (animationDeque.size() == 5) {
                final AtomicInteger doubles = new AtomicInteger(0);
                final AtomicInteger triples = new AtomicInteger(0);

                animationDeque.stream().filter(d -> d == 1L).forEach(d -> doubles.incrementAndGet());
                animationDeque.stream().filter(d -> d == 0L).forEach(d -> triples.incrementAndGet());

                if (doubles.get() > 5 || triples.get() > 5) {
                    this.log("d=" + doubles.get() + " t=" + triples.get());
                }
            }

            this.lastArmAnimation = now;
        }
    }
}
