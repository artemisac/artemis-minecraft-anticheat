package ac.artemis.checks.enterprise.aim;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.rotation.SimpleRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.SimpleRotation;
import com.google.common.collect.Lists;
import lombok.val;

import java.util.Deque;
import java.util.LinkedList;

@Check(type = Type.AIM, var = "Stats")
public class AimStats extends SimpleRotationCheck {
    private int buffer = 0;

    private final Deque<Float> samplesYaw = new LinkedList<>();
    private final Deque<Float> samplesPitch = new LinkedList<>();

    public AimStats(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handleRotation(final SimpleRotation from, final SimpleRotation to) {
        final long now = System.currentTimeMillis();

        final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
        final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

        final boolean action = now - data.combat.lastAttack < 120L || now - data.user.lastPlace < 120L;

        /*
         * This check essentially checks for rotations that had been made with a wrongful randomization. That means that
         * there were almost no rotations through a sample size of 120 that could be considered outliers and yet the
         * duplicate amount is really small. In statistics that's what we call an improbability. In minecraft every rotation
         * is constant to a sensitivity, which means that there's only an N amount of possible rotations that can be made. Meaning
         * at no circumstance should there be less than 30~ duplicates, no matter how much someone fucks up their aim.
         */
        handle: {
            if (deltaYaw == 0.0 || deltaPitch == 0.0 || deltaYaw > 30.f || deltaPitch > 30.f || !action) break handle;

            /*
             * We need to store the rotations in a sample list. I could possibly make this check without needing
             * samples but it takes a lot more time and a lot more code to do and generally it is not something I think
             * is necessary for this is run on. Keep in mind we already have a check that is there to validate the math
             * and the linearsy of rotations through mathematical means. No need to reinvent the wheal.
             */
            samplesYaw.add(deltaYaw);
            samplesPitch.add(deltaPitch);

            /*
             * We're directly listening to 120 rotations on both the X and the Y axis and thus their combined total
             * is 240 (120 + 120). Keep in mind that this is going to get filled up extremely fast since rotations
             * are called every 50ms which means every tick. Kill auras will flag this check a lot without being able to do much.
             */
            if (samplesYaw.size() + samplesPitch.size() == 240) {
                /*
                 * We're getting the outliers through the q1,q2 rule, which gives us both the high and the low
                 * outliers. For this sort of check we do not need to make any direct calls to either so we will end up
                 * using the combined total. Do not use any other math but my own since most people don't even know what an outlier is
                 */
                val outliersYaw = MathUtil.getOutliers(samplesYaw);
                val outliersPitch = MathUtil.getOutliers(samplesPitch);

                /*
                 * We will also be checking duplicates for this check since this is the magic variable in this check.
                 * It returns how many of the rotations were actually the same exact number. Most kill-auras end up
                 * using a math.random which in turn makes their aim extremely randomized without a single duplicate.
                 */
                final double duplicatesYaw = MathUtil.getDuplicates(samplesYaw);
                final double duplicatesPitch = MathUtil.getDuplicates(samplesPitch);

                /*
                 * As mentioned above, we're using the combined total of the outliers. This is done to check how many
                 * of the rotations did not match the other ones from the dataset. If a player is legit they should be
                 * high if they have a duplicate count smaller than 20. This is extremely dynamic too.
                 */
                final int outliersX = outliersYaw.a().size() + outliersYaw.b().size();
                final int outliersY = outliersPitch.a().size() + outliersPitch.b().size();

                /*
                 * We do not even need a buffer for this check. We're checking if the player had almost no
                 * duplicates relative to the sample-size for both yaw and pitch, and the outliers simply filter the
                 * rest out without any difficulty. This is probably the best way to make this check out right now.
                 */
                if (duplicatesYaw < 15 && duplicatesPitch <= 9 && outliersX < 30 && outliersY < 30) {
                    if (++buffer > 1) log();
                } else {
                    buffer = 0;
                }

                samplesYaw.clear();
                samplesPitch.clear();
            }
        }
    }
}
