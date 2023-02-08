package ac.artemis.checks.regular.v2.checks.impl.timer;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.settings.CheckSetting;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;
import ac.artemis.core.v4.utils.lists.EvictingArrayList;
import ac.artemis.core.v4.utils.maths.MathUtil;

import java.util.List;

/**
 * @author Ghast, Green
 * @since 21-Mar-20
 * Artemis © 2020
 *
 * "Ask, and it shall be given you; seek, and ye
 * shall find; knock, and it shall be opened unto you:
 * For every one that asketh receiveth; and he that seeketh
 * findeth; and to him that knocketh it shall be opened.
 *
 * But beware, All hope abandon ye who enter here."
 *
 * Hours wasted on understanding this: 25
 *
 * This check just does its own thing. Don't question it. Don't clean it up.
 *
 */
@Check(type = Type.TIMER, var = "Average", threshold = 10)
public class TimerAverage  extends ArtemisCheck implements PacketHandler, PacketExcludable {

    private final List<Long> delays = new EvictingArrayList<>(50);
    private final List<Long> delaysLong = new EvictingArrayList<>(250);
    private long lastPacketTime;
    private int buffer;

    @Setting(type = CheckSettings.AVERAGE, defaultValue = "1.12")
    private final CheckSetting minAverage = info.getSetting(CheckSettings.AVERAGE);

    @Setting(type = CheckSettings.MAX_STREAK_FOR_VL, defaultValue = "2")
    private final CheckSetting streaksNeeded = info.getSetting(CheckSettings.MAX_STREAK_FOR_VL);

    public TimerAverage(PlayerData data, CheckInformation info) {
        super(data, info);
        this.setCompatiblePackets(
                PacketPlayClientFlying.class,
                GPacketPlayClientLook.class,
                GPacketPlayClientPosition.class,
                GPacketPlayClientPositionLook.class
        );
    }

    @Override
    public void handle(final GPacket packet) {
        final boolean condition = System.currentTimeMillis() - data.movement.getLastDelayedMovePacket() > 220L
                && !data.user.isLagging()
                && !data.user.isOnCooldown()
                && !isNull(CheckType.POSITION);
        if (!condition) {
            return;
        }

        this.delays.add(System.currentTimeMillis() - lastPacketTime);

        if (delays.size() == 50) {
            final double average = 50.0 / delays.stream()
                    .mapToDouble(Number::doubleValue)
                    .average()
                    .orElse(0.f);
            final double averageLong = 50.0 / delaysLong.stream()
                    .mapToDouble(Number::doubleValue)
                    .average()
                    .orElse(0.f);
            final double fluctuation = MathUtil.getFluctuation(delaysLong.stream()
                    .mapToDouble(Number::doubleValue)
                    .toArray()
            );

            process: {
                final boolean flag = average >= minAverage.getAsDouble() && fluctuation < 1.1;

                if (!flag) {
                    this.buffer = 0;
                    break process;
                }

                if (++buffer <= streaksNeeded.getAsInt())
                    break process;

                this.log(
                        new Debug<>("s", MathUtil.roundToPlace(average, 2)),
                        new Debug<>("aVLong", averageLong),
                        new Debug<>("fluct", fluctuation)
                );
            }

            this.debug("s=" + MathUtil.roundToPlace(average, 2) + " aVLong=" + averageLong + " fluct=" + fluctuation);
        }

        this.lastPacketTime = System.currentTimeMillis();
    }
}
