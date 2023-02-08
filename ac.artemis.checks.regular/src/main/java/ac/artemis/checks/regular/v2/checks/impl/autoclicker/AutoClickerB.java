package ac.artemis.checks.regular.v2.checks.impl.autoclicker;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.settings.CheckSetting;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.lists.EvictingLinkedList;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Deque;

/**
 * @author Ghast
 * @since 21-Mar-20
 */

@Check(type = Type.AUTOCLICKER, var = "B", threshold = 20)
public class AutoClickerB extends ArtemisCheck implements PacketHandler {

    private double[] lastTickTimingFirstArray, lastTickTimingSecondArray, lastFlyTimingFirstArray, lastFlyTimingSecondArray;

    private double ticks, ticksFly, ticksArm, lastv1;
    private boolean pause, done;

    @Setting(type = CheckSettings.FIRST_SAMPLE_FLY_SIZE, defaultValue = "20")
    private final CheckSetting firstSampleFlySize = info.getSetting(CheckSettings.FIRST_SAMPLE_FLY_SIZE);

    @Setting(type = CheckSettings.SECOND_SAMPLE_FLY_SIZE, defaultValue = "20")
    private final CheckSetting secondSampleFlySize = info.getSetting(CheckSettings.SECOND_SAMPLE_FLY_SIZE);

    @Setting(type = CheckSettings.FIRST_SAMPLE_USE_SIZE, defaultValue = "100")
    private final CheckSetting firstSampleUseSize = info.getSetting(CheckSettings.FIRST_SAMPLE_USE_SIZE);

    @Setting(type = CheckSettings.SECOND_SAMPLE_USE_SIZE, defaultValue = "100")
    private final CheckSetting secondSampleUseSize = info.getSetting(CheckSettings.SECOND_SAMPLE_USE_SIZE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting ticksForPause = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    private final Deque<Double> firstSampleFlyTick = new EvictingLinkedList<>(firstSampleUseSize.getAsInt());
    private final Deque<Double> secondSampleFlyTick = new EvictingLinkedList<>(secondSampleUseSize.getAsInt());
    private final Deque<Double> firstSampleTimingTick = new EvictingLinkedList<>(firstSampleFlySize.getAsInt());
    private final Deque<Double> secondSampleTimingTick = new EvictingLinkedList<>(secondSampleFlySize.getAsInt());

    public AutoClickerB(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientArmAnimation) {
            if (data.user.isDigging() || data.user.isPlaced() || !TimeUtil.hasExpired(data.user.getLastDig(), 3))
                return;
            done = false;
            // Fly tick

            if (pause) {
                pause = false;
            }
            if (ticks > ticksForPause.getAsInt()) {
                pause = true;
                debug("pause toggled for ticks x=" + ticks + " > " + ticksForPause.getAsInt());
            }

            if (!pause) {
                firstSampleFlyTick.add(ticks);

                if (this.firstSampleFlyTick.size() >= firstSampleUseSize.getAsInt()) {
                    secondSampleFlyTick.add(firstSampleFlyTick.getFirst());
                }
            }

            ticks = 0;
            // Timing tick
            ticksArm++;

            // Timing tick execution

            if (ticksFly++ >= 20) {
                // Calculation
                final double tpt = ticksArm / ticksFly;
                this.ticksFly = 0;
                this.ticksArm = 0;

                if (pause) {
                    pause = false;
                }
                // Pause system
                if (tpt <= 0.2 || tpt > 1.0) {
                    pause = true;
                }

                // Execution
                // Sampling
                if (!pause) {
                    firstSampleTimingTick.add(tpt);
                    if (firstSampleTimingTick.size() >= firstSampleFlySize.getAsInt())
                        secondSampleTimingTick.add(firstSampleTimingTick.getFirst());

                    // Actual value debugging
                    if (firstSampleTimingTick.size() >= firstSampleFlySize.getAsInt()
                            && secondSampleTimingTick.size() >= secondSampleFlySize.getAsInt()) {
                        lastTickTimingFirstArray = firstSampleTimingTick.stream().mapToDouble(Number::doubleValue).toArray();
                        lastTickTimingSecondArray = secondSampleTimingTick.stream().mapToDouble(Number::doubleValue).toArray();
                    }
                }

            }

            // Fly tick execution
            if (this.firstSampleFlyTick.size() >= firstSampleUseSize.getAsInt()
                    && secondSampleFlyTick.size() >= secondSampleUseSize.getAsInt()) {

                lastFlyTimingFirstArray = firstSampleFlyTick.stream().mapToDouble(Number::doubleValue).toArray();
                lastFlyTimingSecondArray = secondSampleFlyTick.stream().mapToDouble(Number::doubleValue).toArray();
            }

            if (lastTickTimingFirstArray != null && lastTickTimingSecondArray != null && lastFlyTimingFirstArray != null
                    && lastFlyTimingSecondArray != null) {
                final DescriptiveStatistics lttfStats = new DescriptiveStatistics(lastTickTimingFirstArray);
                final DescriptiveStatistics lttsStats = new DescriptiveStatistics(lastTickTimingSecondArray);
                final DescriptiveStatistics lftfStats = new DescriptiveStatistics(lastFlyTimingFirstArray);
                final DescriptiveStatistics lftsStats = new DescriptiveStatistics(lastFlyTimingSecondArray);

                final double x1;
                final double x2;
                final double x3;
                final double x4;

                try {
                    x1 = MathUtil.round(lttfStats.getSkewness(), 4);
                    x2 = MathUtil.round(lttsStats.getSkewness(), 4);
                    x3 = MathUtil.round(lftfStats.getSkewness(), 4);
                    x4 = MathUtil.round(lftsStats.getSkewness(), 4);
                } catch (final Exception e) {
                    return;
                }

                final double k1 = MathUtil.round(lttfStats.getKurtosis(), 4);
                final double k2 = MathUtil.round(lttsStats.getKurtosis(), 4);
                final double k3 = MathUtil.round(lftfStats.getKurtosis(), 4);
                final double k4 = MathUtil.round(lftsStats.getKurtosis(), 4);

                final double v1 = MathUtil.round(lttfStats.getVariance(), 4);
                final double v2 = MathUtil.round(lttsStats.getVariance(), 4);
                final double v3 = MathUtil.round(lftfStats.getVariance(), 4);
                final double v4 = MathUtil.round(lftsStats.getVariance(), 4);

                final double deltaSlowdown = MathUtil.round(Math.abs(v3 - v4), 4);
                final double deltaKdown = MathUtil.round(Math.abs(k3 - k4), 4);
                final double kDelta = MathUtil.round(Math.abs(k1 - k2), 4);
                final double xMean = MathUtil.round((x1 + x2 + x3) / 3, 4);
                final double v1Delta = MathUtil.round(Math.abs(lastv1 - v1), 4);

                final double swingRate1 = lttfStats.getSum() / firstSampleUseSize.getAsInt();
                final double swingRate2 = lttsStats.getSum() / secondSampleUseSize.getAsInt();
                final double swingRate3 = lftfStats.getMean();
                final double swingRate4 = lftsStats.getMean();

                final double swingRateAverage = (swingRate1 + swingRate2 + swingRate3 + swingRate4) / 4;
                final double deltaSwingRate = MathUtil.delta(swingRate1, swingRate2);

                if (swingRate1 < 1 || swingRate2 < 1) {
                    debug("s1=" + swingRate1 + "%s2=" + swingRate2 + "%s3=" + swingRate3 + "%s4=" + swingRate4
                            + "%sA!=" + swingRateAverage);
                }


                if (xMean < 0.75
                        && x4 < 2
                        && kDelta < 1.5
                        && k3 < 0.75
                        && k4 < 2.515
                        && deltaSlowdown < 1
                        && (v1 < 1.0E-4 || v1Delta < 0.01)
                        && swingRateAverage > 0.50
                        && deltaSwingRate < 0.75
                        && swingRateAverage < 1.3
                        && deltaKdown < 0.8
                        && deltaSlowdown > 0.05
                ) {
                    debug("[FLAGGED] xMean=" + xMean
                            + "%k=" + x4
                            + "%dK=" + kDelta
                            + "%k3=" + k3
                            + "%k4=" + k4
                            + "%v1=" + v1
                            + "%dS=" + deltaSlowdown
                            + "%dK=" + deltaKdown
                    );
                    log("xMean=" + xMean + "%k=" + x4
                            + "%dK=" + kDelta + "%k3=" + k3 + "%k4=" + k4 + "%v1=" + v1 + "%dS=" + deltaSlowdown);
                } else {
                    debug("%x1=" + x1 + "%x2=" + x2 + "%xMean=" + xMean + "%k=" + x4
                            + "%dK=" + kDelta + "%k3=" + k3 + "%k4=" + k4 + "%v1=" + v1 + "%dS=" + deltaSlowdown);
                }
                lastv1 = v1;
                done = true;
            }

            if (!done) {
                debug("%firstFly=" + firstSampleFlyTick.size() + " %firstTick=" + firstSampleTimingTick.size()
                        + " %secondFly=" + secondSampleFlyTick.size() + " %secondTick=" + secondSampleTimingTick.size());
            }

        } else if (packet instanceof PacketPlayClientFlying
            /*&& !((PacketPlayClientFlying) packet).isLook() && !((PacketPlayClientFlying) packet).isPos()*/) {
            if (data.user.isDigging() || data.user.isPlaced() || !TimeUtil.hasExpired(data.user.getLastDig(), 3))
                return;

            // Fly tick
            this.ticks++;
            this.ticksFly++;
            debug("ticks=" + ticks + " ticksfly=" + ticksFly);
        }
    }
}
