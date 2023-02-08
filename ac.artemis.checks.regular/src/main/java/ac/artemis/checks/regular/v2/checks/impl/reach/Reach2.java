package ac.artemis.checks.regular.v2.checks.impl.reach;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.ReachHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Drop;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.settings.CheckSetting;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.dependency.DependencyManager;
import ac.artemis.core.v4.utils.reach.ReachEntity;
import ac.artemis.core.v4.utils.reach.ReachModal;
import ac.artemis.core.v5.utils.buffer.Buffer;
import ac.artemis.core.v5.utils.buffer.StandardBuffer;

/**
 * @author Ghast
 * @since 23/05/2021
 * @see ac.artemis.core.v4.data.holders.ReachHolder
 * @version 2.0 (Remake of the previous reach check)
 *
 * This is as simple of a reach check I could make. A lot of last location and stuff reach checks however
 * none of these methods over time have been as efficient as using and properly handling relative move. In
 * this reach check, we use our entity processor to emulate players and their movement on our playerdata's
 * player. We use waves of confirmations via transaction packets to validate whether or not a player has
 * received an entity's movement data.
 *
 * After a couple of hacky trick and through an unoptimized and non-focus branching system, we can determine
 * a list (usually 1-8 positions) of probable positions a player could be on. We replicate the entity
 * raytracing (boundingbox intercept) and figure out which are valid looks. Finally, we pick the lowest
 * probable distance (picking the mean would be unsafe) and flag for that.
 *
 * This is pretty much the reach check in Artemis (though Artemis covers more 1 in 10000 scenarios).
 */
@Check(type = Type.REACH, var = "Complex")
@Drop(decay = 15)
public class Reach2  extends ArtemisCheck implements ReachHandler {

    public Reach2(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    private final boolean isEnterprise = Artemis.v().getApi().getLoaderManager().getEnterprise() != null;
    private final Buffer buffer = new StandardBuffer(5)
            .setValue(0)
            .setMin(0)
            .setMax(20);

    @Setting(type = CheckSettings.MAX_RANGE, defaultValue = "3.01")
    private final CheckSetting range = info.getSetting(CheckSettings.MAX_RANGE);

    @Override
    public void handle(final ReachModal current, final ReachEntity opponent) {
        /*
         * Here in this scenario we seriously do NOT want to be flagging game-mode creative
         * as it simply gains more reach. On a regular MC player, such has 3 block of entity
         * reach. However, when you switch to game-mode creative, your reach is extended to
         * 4.5. And as I simply cannot be asked to add the math condition for something useless,
         * I exempt it.
         */
        if (this.isExempt(ExemptType.GAMEMODE)) {
            return;
        }

        /*
         * Thanks to Mojang the players boundingbox changes when riding an entity. When this
         * happens we want to return out of this check as it will miss all the attacks done.
         */
        if (data.reach.getTarget() != null && data.reach.getTarget().isInsideVehicle()) {
            return;
        }

        /*
         * To run our reach check we have a couple of conditions: First of all, we only want to
         * handle attacks. Second of all, we will not handle entities not being the last processed
         * attacked player. Finally, we wish to exclusively keep it to the last attacked player.
         */
        if (current.getType() != ReachModal.Type.HIT && current.getType() != ReachModal.Type.HIT_MISS) {
            if (current.getType() == ReachModal.Type.TICK)
                return;

            this.debug("Click!");
            return;
        }

        /*
         * Here we grab the important data from the smallest iteration: distance, tick and the size
         * of the reach positions. This is shared between both the reach and hitbox check, hence
         * should be kept outside their respective labels
         */
        final double distance = current.getDistance();
        final int size = opponent == null ? 0 : opponent.reachPositions.size();
        final int tick = current.getTicks();

        /*
         * The reach check is quite straight forward: We check if the smallest distance is valid
         * and whether or not it's superior to the agreed reach detection (3.01). In theory, on
         * vanilla minecraft, this should never even flag above 3.01. As due to Lunar's lack
         * of consistency with abiding the protocol, it's safe to use 3.05 as a flag.
         */
        reach: {
            /*
             * As per Minecraft's specifications, creative game-mode gives a player 6 block of
             * reach. Furthermore, there's no justifiable reason to be flagging users with
             * creative game-mode. Hence we can simply exempt this scenario
             */
            final boolean exempt = this.isExempt(ExemptType.GAMEMODE)
                    || current.getType() != ReachModal.Type.HIT;

            if (exempt)
                break reach;

            /*
             * Here we specify the maximum allowed reach. Since we want to keep our customers
             * safe from doing silly shit like setting max reach to 2 blocks or some other
             * retard shit, we set the minimum reach to the plan's max allowed reach.
             * For reference, here are the offered detections:
             *
             * $ Standard Edition: 3.1+
             * $ Enterprise Edition: 3.01+
             *
             * The developer mode (shaded depends + some stuff I'm working on to ensure if it
             * gets dumped we don't get super fucked) is the same as enterprise
             */
            final double max = isEnterprise || DependencyManager.IS_DEV
                    ? Math.max(3.01, range.getAsDouble())
                    : Math.max(3.1, range.getAsDouble());
            /*
             * As previously agreed, we will flag for any reach superior to 3.01 which is valid.
             * It is theoretically impossible to hit an entity from 100 blocks away, hence we
             * can consider that any distance superior to 100 is simply our iteration's default
             * Double.MAX_VALUE. To keep an eye out for in-case a "infinite" reach exploit ever
             * comes up.
             */
            final boolean flag = distance > max && distance < 100.D;

            if (!flag)
                break reach;

            /*
             * Fail the check with the adequate. Here's a rough explanation of every debug:
             * range: distance calculated by the iteration
             * size: number of possible scenarios, the higher the most likely a false flag will happen
             * tick: interpolation tick (internal stuff)
             */
            this.log(
                    new Debug<>("range", distance),
                    new Debug<>("size", opponent.reachPositions.size()),
                    new Debug<>("tick", tick),
                    new Debug<>("state", "hit")
            );
        }

        /*
         * The hit-box check is as simple as the reach check. In this scenario, we take into
         * account whether or not the player has hit a raytrace on the entity. If not, we
         * increment a buffer (added for leniency). If over-time, the value of the buffer
         * surpasses 10, we flag.
         */
        box: {
            /*
             * As per Minecraft's specifications, creative game-mode gives a player 6 block of
             * reach. This could cause false positives in our iteration as we only raytrace to
             * 6 blocks to reach, as opposed to the 8 blocks we would need for leniency.
             * Furthermore, there's no justifiable reason to be flagging users with creative
             * game-mode. Hence we can simply exempt this scenario
             */
            final boolean exempt = this.isExempt(ExemptType.GAMEMODE) || current.getType() != ReachModal.Type.HIT_MISS;

            if (exempt)
                break box;

            /*
             * If the distance is superior to 100.D, which we assume is impossible to obtain in
             * normal conditions as we raytrace up to 6 blocks, we can henceforth assume no valid
             * scenario was found. Furthermore, since we have more than 0 possible positions, we
             * know for sure we should be hitting an entity. Hence, the player is not hitting the
             * proper hit-box. He must be cheating.
             */
            final boolean flag = distance > 100.D && size > 0;

            if (!flag) {
                /*
                 * Player has hit an hit-box, we can decrease the buffer by 1/4th to prevent more
                 * than 1 out of 4 consecutive hit-box hits. This is as good as it can get without
                 * causing issues in the long run.
                 */
                buffer.decreaseBuffer(0.25);
                break box;
            }

            /*
             * We flagged! Lets increment the buffer. If the buffer meets our objective of 10, we can
             * proceed to emit a violation. If not, we simply break the label.
             */
            buffer.incrementBuffer();
            if (!buffer.flag())
                break box;

            /*
             * Buffer's threshold was met. We know for sure the user is not hitting the hit-box.
             * There are several reasons to flag this check: misplace, hit-box, aura, aim-bot,
             * scaffold, step, no-rotate, etc...
             */
            this.log(
                    new Debug<>("range", -1),
                    new Debug<>("size", opponent.reachPositions.size()),
                    new Debug<>("tick", tick),
                    new Debug<>("state", "missed")
            );
            this.debug("Would have been kicked, cannot find good reach scenario.");
        }

        /*
         * Debug the check with the adequate. Here's a rough explanation of every debug:
         * range: distance calculated by the iteration
         * size: number of possible scenarios, the higher the most likely a false flag will happen
         * tick: interpolation tick (internal stuff)
         */
        this.debug(String.format("range=%.4f quant=%d tick=%d", distance, size, tick));
    }
}
