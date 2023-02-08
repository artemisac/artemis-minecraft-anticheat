package ac.artemis.checks.enterprise.timer;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.lists.EvictingArrayList;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;

import java.util.List;

/**
 * @author Ghast
 * @since 04/12/2020
 * Artemis © 2020
 */

@Check(type = Type.TIMER, var = "Bad", threshold = 20)
@ClientVersion
public class TimerGladUrBad extends ArtemisCheck implements PacketHandler, PacketExcludable {
    public TimerGladUrBad(final PlayerData data, final CheckInformation info) {
        super(data, info);
        this.setCompatiblePackets(
                PacketPlayClientFlying.class
        );
    }

    private final List<Long> samples = new EvictingArrayList<>(50);
    private long lastFlyingTime;
    private float buffer;

    @Override
    public void handle(final GPacket packet) {
        final long now = packet.getTimestamp();
        final long delta = now - lastFlyingTime;

        this.samples.add(delta);

        if (samples.size() >= 45) {
            final double average = samples.stream().mapToDouble(Number::doubleValue).average().orElse(0.0);
            final double speed = 50 / average;

            final double deviation = MathUtil.getStandardDeviation(samples);

            flag: {
                final boolean flag = speed <= 0.82 && deviation < 50.0;

                if (!flag) {
                    this.buffer *= 0.75;
                    break flag;
                }

                if (buffer++ <= 35) {
                    break flag;
                }

                this.log(
                        new Debug<>("speed", speed),
                        new Debug<>("deviation", deviation)
                );
            }

            this.debug("float=%.2f std=%.2f buffer=%.2f", speed, deviation, buffer);
        }

        this.lastFlyingTime = now;
    }


}
