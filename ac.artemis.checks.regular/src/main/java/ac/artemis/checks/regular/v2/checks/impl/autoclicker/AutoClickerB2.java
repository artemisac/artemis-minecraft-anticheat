package ac.artemis.checks.regular.v2.checks.impl.autoclicker;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Drop;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.settings.CheckSetting;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.lists.EvictingLinkedList;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * @author Ghast
 * @since 21-Mar-20
 */

@Check(type = Type.AUTOCLICKER, var = "B", threshold = 20)
@Drop
public class AutoClickerB2 extends ArtemisCheck implements PacketHandler {

    public AutoClickerB2(PlayerData data, CheckInformation info) {
        super(data, info);
    }


    private double[] lastTickTimingFirstArray, lastTickTimingSecondArray, lastFlyTimingFirstArray, lastFlyTimingSecondArray;

    private double ticks, ticksFly, ticksArm, lastv1;
    private int verbose;
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

    // KURTOSIS
    @Setting(type = CheckSettings.MIN_KURTOSIS_FIRST_SAMPLE_FLY, defaultValue = "10")
    private final CheckSetting minKurtosisFlyFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MIN_KURTOSIS_SECOND_SAMPLE_FLY, defaultValue = "10")
    private final CheckSetting minKurtosisFlySecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MIN_KURTOSIS_FIRST_SAMPLE_USE, defaultValue = "10")
    private final CheckSetting minKurtosisUseFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MIN_KURTOSIS_SECOND_SAMPLE_USE, defaultValue = "10")
    private final CheckSetting minKurtosisUseSecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MAX_KURTOSIS_FIRST_SAMPLE_FLY, defaultValue = "10")
    private final CheckSetting maxKurtosisFlyFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MAX_KURTOSIS_SECOND_SAMPLE_USE, defaultValue = "10")
    private final CheckSetting maxKurtosisFlySecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MAX_KURTOSIS_FIRST_SAMPLE_USE, defaultValue = "10")
    private final CheckSetting maxKurtosisUseFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MAX_KURTOSIS_SECOND_SAMPLE_USE, defaultValue = "10")
    private final CheckSetting maxKurtosisUseSecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    // Kurtosis Delta

    @Setting(type = CheckSettings.MIN_KURTOSIS_FIRST_SAMPLE_USE_DELTA, defaultValue = "10")
    private final CheckSetting minKurtosisDeltaUseFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MAX_KURTOSIS_FIRST_SAMPLE_USE_DELTA, defaultValue = "10")
    private final CheckSetting maxKurtosisDeltaUseFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    // SKEWNESS MEAN

    @Setting(type = CheckSettings.MIN_SKEWNESS_MEAN_SAMPLE, defaultValue = "10")
    private final CheckSetting minSkewnessMeanSamples = info.getSetting(CheckSettings.MIN_SKEWNESS_MEAN_SAMPLE);

    @Setting(type = CheckSettings.MAX_SKEWNESS_MEAN_SAMPLE, defaultValue = "0.75")
    private final CheckSetting maxSkewnessMeanSamples = info.getSetting(CheckSettings.MAX_SKEWNESS_MEAN_SAMPLE);

    // SKEWNESS

    @Setting(type = CheckSettings.MIN_SKEWNESS_FIRST_SAMPLE_FLY, defaultValue = "10")
    private final CheckSetting minSkewnessFlyFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MIN_SKEWNESS_SECOND_SAMPLE_FLY, defaultValue = "10")
    private final CheckSetting minSkewnessFlySecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MIN_SKEWNESS_FIRST_SAMPLE_USE, defaultValue = "10")
    private final CheckSetting minSkewnessUseFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MIN_SKEWNESS_SECOND_SAMPLE_USE, defaultValue = "10")
    private final CheckSetting minSkewnessUseSecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MAX_SKEWNESS_FIRST_SAMPLE_FLY, defaultValue = "10")
    private final CheckSetting maxSkewnessFlyFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MAX_SKEWNESS_SECOND_SAMPLE_FLY, defaultValue = "2")
    private final CheckSetting maxSkewnessFlySecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MAX_SKEWNESS_FIRST_SAMPLE_USE, defaultValue = "10")
    private final CheckSetting maxSkewnessUseFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.MAX_SKEWNESS_SECOND_SAMPLE_USE, defaultValue = "10")
    private final CheckSetting maxSkewnessUseSecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    // VARIANCE

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting minVarianceFlyFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting minVarianceFlySecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting minVarianceUseFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting minVarianceUseSecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting maxVarianceFlyFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting maxVarianceFlySecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting maxVarianceUseFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting maxVarianceUseSecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    // SWING RATE

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting minSwingRateFlyFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting minSwingRateFlySecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting minSwingRateUseFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting minSwingRateUseSecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting maxSwingRateFlyFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting maxSwingRateFlySecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting maxSwingRateUseFirst = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting maxSwingRateUseSecond = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    // SWING RATE AVERAGE

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting minSwingRateAverage = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting maxSwingRateAverage = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    // SWING RATE DELTA USE

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting minSwingRateDeltaUse = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    @Setting(type = CheckSettings.TICKS_FOR_PAUSE, defaultValue = "10")
    private final CheckSetting maxSwingRateDeltaUse = info.getSetting(CheckSettings.TICKS_FOR_PAUSE);

    private final Deque<Double> firstSampleFlyTick = new EvictingLinkedList<>(firstSampleUseSize.getAsInt());
    private final Deque<Double> secondSampleFlyTick = new EvictingLinkedList<>(secondSampleUseSize.getAsInt());
    private final Deque<Double> firstSampleTimingTick = new EvictingLinkedList<>(firstSampleFlySize.getAsInt());
    private final Deque<Double> secondSampleTimingTick = new EvictingLinkedList<>(secondSampleFlySize.getAsInt());

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
                if (this.firstSampleFlyTick.size() >= firstSampleUseSize.getAsInt())
                    secondSampleFlyTick.add(firstSampleFlyTick.getFirst());

            }
            ticks = 0;
            // Timing tick
            ticksArm++;


            // Timing tick execution

            if (ticksFly++ >= 20) {
                // Calculation
                double tpt = ticksArm / ticksFly;
                this.ticksFly = 0;
                this.ticksArm = 0;

                if (pause) {
                    pause = false;
                }
                // Pause system
                if (tpt <= 0.2) {
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
                DescriptiveStatistics lttfStats = new DescriptiveStatistics(lastTickTimingFirstArray);
                DescriptiveStatistics lttsStats = new DescriptiveStatistics(lastTickTimingSecondArray);
                DescriptiveStatistics lftfStats = new DescriptiveStatistics(lastFlyTimingFirstArray);
                DescriptiveStatistics lftsStats = new DescriptiveStatistics(lastFlyTimingSecondArray);

                double x1;
                double x2;
                double x3;
                double x4;

                try {
                    x1 = MathUtil.round(lttfStats.getSkewness(), 4);
                    x2 = MathUtil.round(lttsStats.getSkewness(), 4);
                    x3 = MathUtil.round(lftfStats.getSkewness(), 4);
                    x4 = MathUtil.round(lftsStats.getSkewness(), 4);
                } catch (Exception e) {
                    return;
                }

                double k1 = MathUtil.round(lttfStats.getKurtosis(), 4);
                double k2 = MathUtil.round(lttsStats.getKurtosis(), 4);
                double k3 = MathUtil.round(lftfStats.getKurtosis(), 4);
                double k4 = MathUtil.round(lftsStats.getKurtosis(), 4);

                double v1 = MathUtil.round(lttfStats.getVariance(), 4);
                double v2 = MathUtil.round(lttsStats.getVariance(), 4);
                double v3 = MathUtil.round(lftfStats.getVariance(), 4);
                double v4 = MathUtil.round(lftsStats.getVariance(), 4);

                double deltaSlowdown = MathUtil.round(Math.abs(v3 - v4), 4);
                double deltaKdown = MathUtil.round(Math.abs(k3 - k4), 4);
                double kDelta = MathUtil.round(Math.abs(k1 - k2), 4);
                double xMean = MathUtil.round((x1 + x2 + x3) / 3, 4);
                double v1Delta = MathUtil.round(Math.abs(lastv1 - v1), 4);

                double swingRate1 = lttfStats.getSum() / firstSampleUseSize.getAsInt();
                double swingRate2 = lttsStats.getSum() / secondSampleUseSize.getAsInt();
                double swingRate3 = lftfStats.getMean();
                double swingRate4 = lftsStats.getMean();

                double swingRateAverage = (swingRate1 + swingRate2 + swingRate3 + swingRate4) / 4;
                double deltaSwingRate = MathUtil.delta(swingRate1, swingRate2);

                if (swingRate1 < 1 || swingRate2 < 1) {
                    debug("s1=" + swingRate1 + "%s2=" + swingRate2 + "%s3=" + swingRate3 + "%s4=" + swingRate4
                            + "%sA!=" + swingRateAverage);
                }


                int infractions = 0;
                final List<Type> infracted = new ArrayList<>();


                /*

                VIOLATION

                 */


                // FLY - SKEWNESS

                // First sample settings
                double maxSkewnessFlyFirstVar = maxSkewnessFlyFirst.getAsDouble();
                double minSkewnessFlyFirstVar = minSkewnessFlyFirst.getAsDouble();
                if (x1 > maxSkewnessFlyFirstVar || x1 < minSkewnessFlyFirstVar) {
                    infractions++;
                    infracted.add(Type.SKEWNESS_FLY_FIRST);
                }

                // Second sample settings
                double maxSkewnessFlySecondVar = maxSkewnessFlySecond.getAsDouble();
                double minSkewnessFlySecondVar = minSkewnessFlySecond.getAsDouble();

                if (x2 > maxSkewnessFlySecondVar || x2 < minSkewnessFlySecondVar) {
                    infractions++;
                    infracted.add(Type.SKEWNESS_FLY_SECOND);
                }


                // USE - SKEWNESS

                // First sample settings
                double maxSkewnessUseFirstVar = maxSkewnessUseFirst.getAsDouble();
                double minSkewnessUseFirstVar = minSkewnessUseFirst.getAsDouble();
                if (x1 > maxSkewnessUseFirstVar || x1 < minSkewnessUseFirstVar) {
                    infractions++;
                    infracted.add(Type.SKEWNESS_USE_FIRST);
                }

                // Second sample settings
                double maxSkewnessUseSecondVar = maxSkewnessUseSecond.getAsDouble();
                double minSkewnessUseSecondVar = minSkewnessUseSecond.getAsDouble();

                if (x2 > maxSkewnessUseSecondVar || x2 < minSkewnessUseSecondVar) {
                    infractions++;
                    infracted.add(Type.SKEWNESS_USE_SECOND);
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

    private enum Type {
        SKEWNESS_FLY_FIRST,
        SKEWNESS_FLY_SECOND,
        SKEWNESS_USE_FIRST,
        SKEWNESS_USE_SECOND,

    }

}
