package ac.artemis.checks.enterprise.aura;

import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.rotation.SimpleRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.SimpleRotation;
import lombok.val;

import java.util.Deque;
import java.util.LinkedList;

@Check(type = Type.AURA, var = "Dynamic")
public class AuraDynamic extends SimpleRotationCheck {

    private double lastAverageYaw = 0.0, lastAveragePitch = 0.0;
    private double buffer = 0.0;

    private final Deque<Float> samplesYaw = new LinkedList<>();
    private final Deque<Float> samplesPitch = new LinkedList<>();

    public AuraDynamic(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handleRotation(final SimpleRotation from, final SimpleRotation to) {
        /*
         * We need to get the ticks from the action manager so we can compare the current time
         * versus the last recorded attack. This is done to minimize load on the server.
         */
        final long now = System.currentTimeMillis();

        /*
         * Get the deltaYaw / deltaPitch from the rotation update and get it ready for sampling.
         * We need to verify them first before adding them to the lists to minimize margin of error
         */
        final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
        final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

        /*
         * As mentioned above we need to make sure the player is attacking. This shortens the
         * margin of error and makes the sample rotations much much more specific
         */
        final boolean attacking = now - data.combat.lastAttack < 4;

        /*
         * We cannot detect every kill-aura through verification means. This is here to help us with the auras
         * that seem to be bypassing our aim checks. Even if it's very unlikely it's possible and we don't want that.
         */
        analyze:{
            if (deltaYaw == 0.0 || deltaPitch == 0.0 || deltaYaw > 30.f || deltaPitch > 30.f || !attacking) break analyze;

            /*
             * After verifying the rotation, we need to add it to the sample list. This is done so we only run the code
             * once and not many times over and over again. Plus it helps us make everything better for debugging and we can
             * control data flow a lot easier. Make sure to not add any wrong data here since it may mess up the check.
             */
            samplesYaw.add(deltaYaw);
            samplesPitch.add(deltaPitch);

            /*
             * Instead of checking each of the samples individually we're checking them together for the sheer purpose
             * of simplicity. By doing this, we minimize the chances of a code fuck-up and it makes better more clear.
             */
            if (samplesYaw.size() + samplesPitch.size() == 40) {
                /*
                 * We're getting the outliers from the rotations because we want to limit the check to a very accurate
                 * form of data. This is done to limit players from violating with very inconsistent, but yet consistent aim.
                 */
                val outliersYaw = MathUtil.getOutliers(samplesYaw);
                val outliersPitch = MathUtil.getOutliers(samplesPitch);

                /*
                 * The deviation is the distance between every rotation and the average of the summed rotations.
                 * We're getting this to check aim inconsistency that is present within kill-auras.
                 */
                final double deviationYaw = MathUtil.getStandardDeviation(samplesYaw);
                final double deviationPitch = MathUtil.getStandardDeviation(samplesPitch);

                /*
                 * This is the average of the rotations. We're using this to verify consistency in samples.
                 * Meaning the consistency of the current and the previous samples. Generally a good practice.
                 */
                final double averageYaw = samplesYaw.stream().mapToDouble(d -> d).average().orElse(0.0);
                final double averagePitch = samplesYaw.stream().mapToDouble(d -> d).average().orElse(0.0);

                /*
                 * This is the difference of the average and the last average samples from the checks.
                 * As mentioned above, this is used in verifying and validating rotations and consistency.
                 */
                final double differenceYaw = Math.abs(averageYaw - lastAverageYaw);
                final double differencePitch = Math.abs(averagePitch - lastAveragePitch);

                /*
                 * We're finally compiling the outliers from the rotations at once to properly make a check with
                 * them to filter out any rotations that may have been too inconsistent to analyze.
                 */
                final int outlierX = outliersYaw.a().size() + outliersYaw.b().size();
                final int outlierY = outliersPitch.a().size() + outliersPitch.b().size();

                /*
                 * These are some magic values I came up with through debugging. Do not change them unless you
                 * know what you're doing. Make sure it's consistent.
                 */
                if (deviationYaw > 7.f && deviationPitch > 7.f && differenceYaw < 1.5
                        && differencePitch < 1.5 && outlierX < 20 && outlierY < 20) {
                    buffer += 2.25;

                    if (buffer > 5.5) log();
                } else {
                    buffer = Math.max(buffer - 1.25, 0);
                }

                /*
                 * We're clearing the samples from the lists to ensure that we do not overflow on samples.
                 * If we did not do this then we would end up with a non functional check and huge memory leak.
                 */
                samplesYaw.clear();
                samplesPitch.clear();

                /*
                 * We're parsing the current data so we can use it in the future check run. Make sure to do this
                 * or the check will not be functional or same to run normally.
                 */
                this.lastAverageYaw = averageYaw;
                this.lastAveragePitch = averagePitch;
            }
        }
    }
}
