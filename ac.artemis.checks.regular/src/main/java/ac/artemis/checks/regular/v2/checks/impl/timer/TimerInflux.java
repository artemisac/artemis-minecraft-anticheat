package ac.artemis.checks.regular.v2.checks.impl.timer;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.lists.EvictingLinkedList;
import ac.artemis.core.v5.utils.MathUtil;
import ac.artemis.core.v5.utils.buffer.Buffer;
import ac.artemis.core.v5.utils.buffer.StandardBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.server.PacketPlayServerPosition;


@Check(type = Type.TIMER, var = "Influx", threshold = 20)
public class TimerInflux extends ArtemisCheck implements PacketHandler {

    private final EvictingLinkedList<Long> samples = new EvictingLinkedList<>(50);
    private final Buffer buffer = new StandardBuffer(49)
            .setMax(100)
            .setMin(0)
            .setValue(-5);

    private long previousTick;

    public TimerInflux(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            if (this.isExempt(ExemptType.JOIN, ExemptType.TPS, ExemptType.TELEPORT)) {
                this.samples.clear();
                return;
            }

            final long currentTick = packet.getTimestamp();

            final long tickDelta = currentTick - previousTick;

            /*
             * This is your basic average timer check with influx packet compensation.
             * It's about the easiest and most stable way to make an average check without having too many issues.
             * You can say this isn't stable, but hey, I ran it inside my Verus fork on production for a UHC server just fine.
             */
            if (tickDelta > 1) {
                this.samples.add(tickDelta);
            }

            /*
             * This timer check uses an evicting list because we need to have a real time average and not one
             * that will lag behind. It also allows the check to flag faster :P
             */
            if (this.samples.isFull()) {
                final double tickDeltaAverage = MathUtil.getAverage(this.samples);

                /*
                 * Here a buffer is used just to make sure the small chance of falses are filtered out.
                 */
                if (tickDeltaAverage < 49.25) {
                    buffer.incrementBuffer();

                    if (buffer.flag()) {
                        this.log(
                                new Debug<>("average", tickDeltaAverage),
                                new Debug<>("buffer", buffer.get()),
                                new Debug<>("delta", tickDelta)
                        );
                    }
                } else {
                    buffer.decrementBuffer();
                }

                this.debug("average=" + tickDeltaAverage + " tickDelta=" + tickDelta);
            }

            this.previousTick = currentTick;
        } else if (packet instanceof PacketPlayServerPosition) {
            samples.clear();
        }
    }
}
