package ac.artemis.checks.enterprise.emulator;

import ac.artemis.anticheat.api.check.type.Stage;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PredictionHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.annotations.Drop;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PredictionPosition;
import ac.artemis.core.v4.utils.time.TimeUtil;

import java.util.Arrays;

@Check(type = Type.PREDICTION, var = "Solence")
@ClientVersion
@Experimental(stage = Stage.PRE_RELEASE)
@Drop(decay = 10)
public final class PredictionSolence extends ArtemisCheck implements PredictionHandler {

    private double buffer;
    private boolean wasFlight;

    public PredictionSolence(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final PredictionPosition prediction) {
        final boolean empty = this.isNull(CheckType.POSITION, CheckType.MOVEMENT, CheckType.ROTATION);

        if (empty) return;

        /*
         * When we turn off fly due to predictions relying on the last estimations and our
         * predictions not really working well with flying we are going to exempt till we
         * are on ground as our motions reset there and we can start after this happens.
         */
        if (wasFlight) {
            if (data.user.isOnFakeGround()) wasFlight = false;
            return;
        }

        /*
         * The same stuff explained above applies here as well this is where we just set
         * if the player is flying. Simple stuff
         */
        if (this.isExempt(ExemptType.FLIGHT)) {
            this.wasFlight = true;
            return;
        }

        /*
         * These are certain places we probably should reset our buffer than it would not be fun if stuff went haywire.
         * These are scenarios that are pretty weird and janky and best be left unhandled with a buffer reset.
         */
        if (this.isExempt(ExemptType.VEHICLE, ExemptType.WORLD, ExemptType.JOIN)) {
            this.buffer = -10;
            return;
        }

        /*
         * Yes I know the check has a lot of exemptions, and we shouldn't do that with predictions but with this
         * check we are going for absolute minimum buffer and thresholds, and we need to make sure when this check
         * is running nothing funky is happening. In order to this we can just exempt these stupid scenarios.
         */
        final boolean exempt = this.isExempt(ExemptType.FLIGHT, ExemptType.VEHICLE, ExemptType.VOID, ExemptType.JOIN,
                ExemptType.WORLD, ExemptType.GAMEMODE, ExemptType.MOVEMENT, ExemptType.LIQUID, ExemptType.FLIGHT,
                ExemptType.LIQUID_WALK, ExemptType.SLIME, ExemptType.WEB, ExemptType.COLLIDED_HORIZONTALLY,
                ExemptType.PISTON, ExemptType.UNDERBLOCK, ExemptType.TELEPORT
        ) || !TimeUtil.hasExpired(data.user.getLastRespawn(), 10);

        if (exempt) {
            this.debug("exempt: " + Arrays.toString(exemptTypes()));
            return;
        }

        /*
         * This basically checking if 0.03 applied recently. I don't think I need to explain what 0.03 is.
         * Simply when 0.03 applies the game sends a flying packet and we can just use this to decide if it applied or not.
         * If this has applied the prediction engine might output a small offset and we don't want that to happen right now.
         */
        if (!data.prediction.isPos() || !data.prediction.isLastPos() || !data.prediction.isLastLastPos()) {
            debug("0.03");
            return;
        }

        /*
         * This is the square distance from the predicted position we got and the expected. We could just use normal distance
         * but why spend precious CPU power on math.sqrt when we can process the squared version of it with quite ease.
         */
        final double offset = prediction.got().distanceSquare(prediction.expected());

        /*
         * Stupid sneaking bug randomly occurs, happens to fix this I am just giving the predictions a bit more leniency
         * as anyone that has sneaking on their predictions tag must be moving very slowly anyways. This should make sure
         * this doesn't get abused but doesn't false at the same time. Smart fix I know.
         */
        final double threshold = data.user.isSneaking() ? 1E-3 : 1E-4;

        if (offset > threshold) {
            /*
             * We want to limit the buffer as if shit goes haywire once we don't want the buffer going wayy to high
             * and false flagging legitimate players to a point where it will issue a punishment to a legitimate player.
             * If they flag too much once it will decay pretty fast and there should be no problems further on.
             */
            this.buffer = Math.min(16, this.buffer + 1);

            if (this.buffer > 14) {
                this.log(
                        new Debug<>("buffer", this.buffer),
                        new Debug<>("distance", offset),
                        new Debug<>("exempt", Arrays.toString(exemptTypes()))
                );
            }
        } else {
            this.buffer = Math.max(0, this.buffer - 0.065D);
        }

        this.debug("distance: " + offset + " buffer: " + this.buffer);
    }
}
