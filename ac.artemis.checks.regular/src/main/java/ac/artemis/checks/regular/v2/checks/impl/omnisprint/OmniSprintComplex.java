package ac.artemis.checks.regular.v2.checks.impl.omnisprint;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setback;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.ComplexPositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.core.v4.utils.time.TimeUtil;

import java.util.Arrays;

/**
 * @author Ghast
 * @since 01-Apr-20
 */
@Setback
@Check(type = Type.OMNISPRINT, var = "Complex")
public class OmniSprintComplex extends ComplexPositionCheck {

    private int buffer;
    private boolean wasFlight;

    public OmniSprintComplex(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(final PlayerPosition from, final PlayerPosition to) {
        if (isNull(CheckType.ROTATION, CheckType.POSITION)) return;

        /*
         * These are the inputs given to the game. These are resolved by the prediction engine.
         * We need these in order to check the direction of the movement to check for omni sprint.
         */
        final float forward = data.entity.getMoveForward();
        final float strafe = data.entity.getMoveStrafing();

        final boolean sprint = data.entity.isAttributeSprinting();

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
         * Yes I know the check has a lot of exemptions, and we shouldn't do that with predictions but with this
         * check we are going for absolute minimum buffer and thresholds, and we need to make sure when this check
         * is running nothing funky is happening. In order to this we can just exempt these stupid scenarios.
         */
        final boolean exempt = this.isExempt(
                ExemptType.FLIGHT,
                ExemptType.VEHICLE,
                ExemptType.VOID,
                ExemptType.JOIN,
                ExemptType.WORLD,
                ExemptType.GAMEMODE,
                ExemptType.MOVEMENT,
                ExemptType.LIQUID,
                ExemptType.FLIGHT,
                ExemptType.LIQUID_WALK,
                ExemptType.SLIME,
                ExemptType.WEB,
                ExemptType.COLLIDED_HORIZONTALLY,
                ExemptType.PISTON,
                ExemptType.UNDERBLOCK,
                ExemptType.TELEPORT
        ) || !TimeUtil.hasExpired(data.user.getLastRespawn(), 10);

        final boolean environment = this.distanceH > 0.2D && sprint;
        final boolean invalid = ((forward < 0) || (forward == 0 && strafe != 0));

        if (invalid && environment && !exempt) {
            if (++this.buffer > 10) {
                this.log(
                        new Debug<>("forward", forward),
                        new Debug<>("strafe", strafe),
                        new Debug<>("distH", this.distanceH),
                        new Debug<>("exempt", Arrays.toString(exemptTypes()))
                );
            }
        } else {
            this.buffer = 0;
        }
    }
}
