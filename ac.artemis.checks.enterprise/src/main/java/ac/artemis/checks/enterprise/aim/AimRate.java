package ac.artemis.checks.enterprise.aim;

import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.rotation.SimpleRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.SimpleRotation;
import lombok.val;

import java.util.Deque;
import java.util.LinkedList;

@Check(type = Type.AIM, var = "Rate")
public class AimRate extends SimpleRotationCheck {
    private float lastDeltaYaw = 0.0f, lastDeltaPitch = 0.0f;

    private final Deque<Float> samplesYaw = new LinkedList<>();
    private final Deque<Float> samplesPitch = new LinkedList<>();

    public AimRate(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handleRotation(final SimpleRotation from, final SimpleRotation to) {
        /*
         * These are some basic exemptions. To go in the details, flying entities have the potential to strike
         * around a specific player and causing havoc. It's preferable to exempt such scenario. Furthermore,
         * teleports provide an aim change to players. Hence, it would make no sense to include such scenario.
         * Respawn have the same pattern. Gamemode creative allows for flight and extended reach, hence it's
         * preferable to exempt it too. No movement would henceforth mean the target is simply not moving neither,
         * causing a 100% success rate. Finally, combat must be engaged. It would make 0 sense to have this flag
         * outside of combat.
         */
        final boolean exempt = this.isExempt(
                ExemptType.FLIGHT,
                ExemptType.TELEPORT,
                ExemptType.RESPAWN,
                ExemptType.GAMEMODE,
                ExemptType.MOVEMENT,
                ExemptType.NOT_COMBAT
        );

        if (exempt) return;

        final long now = System.currentTimeMillis();

        final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
        final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

        /*
         * This is checking similarity through the current and the bast rotation delta. The more consistent
         * the aim, the smaller this number is going to be. We also need this to get the rate of change.
         */
        final float differenceYaw = Math.abs(deltaYaw - lastDeltaYaw);
        final float differencePitch = Math.abs(deltaPitch - lastDeltaPitch);

        /*
         * This is a simple mathematical trick to reverse the rate of a rotation. We're basically checking
         * the difference between the past two rotations versus the actual rotation. This is basically pseudo math
         * but we're not using this for any specific calculations other than statistical analysis so it doesn't care
         */
        final float joltX = Math.abs(deltaYaw - differenceYaw);
        final float joltY = Math.abs(deltaPitch - differencePitch);

        /*
         * As mentioned above, we need to make sure that we shorten the sampling rotations the most we can since
         * it generally is good practice and better for checks to limit them to a specific scenario so they're more
         * light weight and generally having a much shorter margin of error, which in turns means less false positives.
         */
        final boolean action = now - data.combat.lastAttack < 150L;

        /*
         * Basically we're checking how consistent the rate of check was in regards to rotations. An aimbot
         * should technically have an extremely inconsistent rate of change which is what we're checking here.
         */
        handle: {
            if (joltX == 0.0 || joltY == 0.0 || !action) break handle;

            /*
             * We need to add the rate of change on the sample size so we can truly get the duplicates in regards
             * to the data. This is much better than needing to run a bunch of code every rotation for performance and
             * accuracy in general. It's definitely a good practice for what we're doing, and it's more accurate too.
             */
            samplesYaw.add((float) MathUtil.roundToPlace(joltX, 2));
            samplesPitch.add((float) MathUtil.roundToPlace(joltY, 2));

            /*
             * The sample size is 60, because of the magic concept that a rotation is sent actively every
             * 50ms. The check is checking for the rate of change in regards to the player aiming. Why does this
             * work? Because client's literally randomize in a wrong manner. Every client out right now has some sort
             * of stupid randomization to "prevent" them from getting patched but in reality they make themselves prone
             * to patches and statistical analysis methods which just makes everything a lot better for us.
             */
            if (samplesYaw.size() + samplesPitch.size() == 60) {
                /*
                 * We're getting the outliers through the q1,q2 rule, which gives us both the high and the low
                 * outliers. For this sort of check we do not need to make any direct calls to either so we will end up
                 * using the combined total. Do not use any other math but my own since most people don't even know what an outlier is
                 */
                val outliersYaw = MathUtil.getOutliers(samplesYaw);
                val outliersPitch = MathUtil.getOutliers(samplesPitch);

                /*
                 * We're getting the duplicates from the rotations to ensure the inconsistency of the rotations.
                 * If the player has any duplicates means that there was some sort of consistency to their rotations.
                 */
                final double duplicatesX = MathUtil.getDuplicates(samplesYaw);
                final double duplicatesY = MathUtil.getDuplicates(samplesPitch);

                /*
                 * As mentioned above, we're using the combined total of the outliers. This is done to check how many
                 * of the rotations did not match the other ones from the dataset. If a player is legit they should be
                 * high if they have a duplicate count smaller than 20. This is extremely dynamic too.
                 */
                final int outliersX = outliersYaw.a().size() + outliersYaw.b().size();
                final int outliersY = outliersPitch.a().size() + outliersPitch.b().size();

                /*
                 * No duplicates mean a very inconsistent rate of change for rotations which means that
                 * the player is most likely cheating! We could technically ban for this instantly
                 */
                if (duplicatesX + duplicatesY == 0.0 && outliersX < 10 && outliersY < 10) log();

                samplesYaw.clear();
                samplesPitch.clear();
            }
        }

        this.lastDeltaYaw = deltaYaw;
        this.lastDeltaPitch = deltaPitch;
    }
}
