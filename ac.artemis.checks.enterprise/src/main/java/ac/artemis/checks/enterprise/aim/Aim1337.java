package ac.artemis.checks.enterprise.aim;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.rotation.ComplexRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.PlayerRotation;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.core.v5.utils.buffer.Buffer;
import ac.artemis.core.v5.utils.buffer.StandardBuffer;

/**
 * @author Ghast
 * @since 24/02/2021
 * Artemis © 2021
 */

@Check(type = Type.AIM, var = "1337", threshold = 20)
public class Aim1337 extends ComplexRotationCheck {
    public Aim1337(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private final Buffer buffer = new StandardBuffer(2)
            .setMin(0)
            .setMax(20)
            .setValue(-2);

    @Override
    public void handleRotation(PlayerRotation from, PlayerRotation to) {
        /*
         * We exempt a couple scenarios which could be linked to rotations updating without the player's true consent
         * hence we just skip over these and ignore them. Their impact can however be quite significant
         */
        final boolean exempt = this.isExempt(
                ExemptType.GAMEMODE,
                ExemptType.TELEPORT,
                ExemptType.RESPAWN,
                ExemptType.JOIN
        );

        if (exempt)
            return;

        check: {
            /*
             * Here we grab the sensitivity X and Y and store it in a variable. Same goes for predicted yaw and predicted
             * pitch. Why? Because I said so. Yeah. Kinda kewl
             */
            final double sensX = data.sensitivity.getSensitivityX();
            final double sensY = data.sensitivity.getSensitivityY();

            /*
             * Here we check if the sensitivity is invalid (eg: could not be properly computed. This essentially
             * is impossible in normal scenarios only the yaw or the pitch axis can even merely be invalid.
             * To false this, you would need to do quick short waves pattern movements, which would require a
             * player to decelerate (view next condition)
             */
            final boolean invalidSens = sensX < -0.03 && sensY < -0.03;

            if (!invalidSens) {
                this.buffer.resetBuffer();
                this.debug(String.format("sensX=%.3f sensY=%.3f", sensX, sensY));
                break check;
            }

            /*
             * Here we include a couple of preconditions to ensure the user is not decelerating. In the
             * case he is, his delta yaw and delta pitch is going to end up being lower than these
             * pre-indicated values.
             */
            final float deltaYaw = MathUtil.distanceBetweenAngles(data.prediction.getYaw(), data.prediction.getLastYaw());
            final float deltaPitch = Math.abs(data.prediction.getPitch() - data.prediction.getLastPitch());

            final boolean invalidAim = deltaYaw > 1.25F && deltaPitch > 1.25F;

            if (invalidAim) {
                this.buffer.resetBuffer();
                this.debug(String.format("deltaY=%.3f deltaP=%.3f", deltaYaw, deltaPitch));
                break check;
            }


            /*
             * These are going to be our mandatory actions which we are going to be using to filter out
             * stuff which we really do not want to use. This is done to stop having useless false flags.
             */
            final boolean attacking = !TimeUtil.elapsed(data.combat.getLastAttack(), 150);
            final boolean rate = !TimeUtil.elapsed(data.sensitivity.getRate(), 150);

            if (!attacking || !rate) {
                this.buffer.resetBuffer();
                this.debug("failed rate or attacking");
                break check;
            }

            /*
             * If the predicted pitch and predicted yaw are fine with this invalid sensitivity,
             * odds are we messed up. To prevent that, we impose the requirement to have an
             * invalid predicted pitch and yaw.
             */
            final double differenceX = data.sensitivity.differenceX;
            final double differenceY = data.sensitivity.differenceY;

            if (differenceX < 1E-9 || differenceY < 1E-9) {
                this.buffer.resetBuffer();
                this.debug(String.format("diffX=%f diffY=%f", differenceX, differenceY));
                break check;
            }

            /*
             * If the mouse movement corresponds to an infinitely small amount or had been a
             * small number before going invalid, there are 2 possible case scenarios:
             * 1 - Cinematic going into the real real small rotations
             * 2 - Fuck-up on our end OR insanely small mouse movements
             *
             * Anyhow, we don't need to flag these as they provide no unfair advantage due
             * to their nature of being way too slow. We exempt that shit like the L.A.P.D.
             * exempts rich white kids
             */
            final double deltaAbsX = Math.abs(data.sensitivity.getDeltaX());
            final double deltaAbsY = Math.abs(data.sensitivity.getDeltaY());

            if (deltaAbsX < 2 || deltaAbsY < 2 || deltaAbsX > 400 || deltaAbsY > 200) {
                this.buffer.resetBuffer();
                this.debug(String.format("absX=%.3f absY=%.3f", deltaAbsX, deltaAbsY));
                break check;
            }

            /*
             * Buffer here because buyers keep complaining about the odd one violation
             * which is 'false' or whatnot. It's silly on their end but whatever. It
             * also helps fixing issues and stuff.
             */
            this.buffer.incrementBuffer();

            if (!buffer.flag()) {
                break check;
            }

            /*
             * Awesome! All the conditions were met, we can now flag for this specific
             * method. Lets not forget to add the required debug to make it all beautiful
             * and shiny
             */
            this.log(
                    new Debug<>("sensX", sensX),
                    new Debug<>("sensY", sensY),
                    new Debug<>("absX", deltaAbsX),
                    new Debug<>("absY", deltaAbsY)
            );

            this.debug(String.format("sensX=%.4f sensY=%.4f absX=%.2f absY=%.2f", sensX, sensY, deltaAbsX, deltaAbsY));
        }
    }
}
