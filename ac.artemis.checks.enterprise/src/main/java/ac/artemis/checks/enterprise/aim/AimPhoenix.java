package ac.artemis.checks.enterprise.aim;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Drop;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.rotation.ComplexRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.PlayerRotation;
import ac.artemis.core.v4.utils.time.TimeUtil;

@Check(type = Type.AIM, var = "Phoenix", alias = "Heuristic:Rotation/GCD.2!Modulo")
@Drop(decay = 5)
public class AimPhoenix extends ComplexRotationCheck  {

    public AimPhoenix(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    private float buffer;

    @Override
    public void handleRotation(final PlayerRotation from, final PlayerRotation to) {
        final double sensitivityX = data.sensitivity.getSensitivityX();
        final double sensitivityY = data.sensitivity.getSensitivityY();
        final double sensitivityXY = Math.abs(sensitivityX - sensitivityY);

        final double mcp = data.sensitivity.getSensitivityTableValue();
        final double mcpX = Math.abs(sensitivityX - mcp);
        final double mcpY = Math.abs(sensitivityY - mcp);

        final double modeYaw = data.sensitivity.getModeYaw();
        final double modePitch = data.sensitivity.getModePitch();

        check: {
            if (modeYaw == Double.MIN_VALUE || modePitch == Double.MIN_VALUE) break check;

            /*
             * Here we exempt fucking ridiculous pitches and stupid rotations as they simply have a tendency to break
             * the laws of physics, thermodynamics and quantum mechanics. Jk. Clamping breaks shit. Expected behaviour.
             */
            final boolean isGayPitch = Math.abs(data.prediction.getPitch()) > 85.F;

            if (isGayPitch) {
                break check;
            }

            /*
             * These are the sensitivity values from the mode and the current ones subtracted to see how far apart they
             * are. If they are too far apart then the player changes their sensitivity which made it so the player falsed.
             */
            final double combinedX = Math.abs(data.sensitivity.getSensitivityX() - data.sensitivity.getComputedX());
            final double combinedY = Math.abs(data.sensitivity.getSensitivityY() - data.sensitivity.getComputedY());

            /*
             * As mentioned in our rotation handler, there is a very rare case of where the sensitivity is going to fail
             * either because of some aim inconsistency or because our divisor lookup was completely wrong. Sadly there is
             * not much we can do that won't be heavy for the server other than simply checking for it here.
             */
            if (data.sensitivity.getSensitivityXY() > 0.1 || (mcpX > 0.1 && mcpY > 0.1)) {
                this.debug(String.format("sensXY=%.5f mcpX=%.5f mcpY=%.5f [%.4f]", sensitivityXY, mcpX, mcpY, mcp));
                break check;
            }

            /*
             * This is going to be our check when a player changes their sensitivity massively which is going to
             * end up falsing the check which we do not want. There is no reason to filter small ones since they wont matter.
             */
            if (combinedX > 0.81 || combinedY > 0.81) {
                this.debug(String.format("combinedX=%.5f combinedY=%.5f", combinedX, combinedY));
                break check;
            }

            /*
             * We are getting the primitive mouse deltas to calculate how close they are to an integer. This is going
             * to be our main flag method since we do not want to do any individual testing on our part yet.
             */
            final double deltaX = data.sensitivity.getDeltaX();
            final double deltaY = data.sensitivity.getDeltaY();

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

            if (deltaAbsX < 3 || deltaAbsY < 3) {
                this.debug(String.format("absX=%.3f absY=%.3f", deltaAbsX, deltaAbsY));
                break check;
            }

            /*
             * Ensure the output mouse movements are large enough to not cause any issue as
             * very small mouse movements are often leading to invalid mouse sensitivities
             * being detected.
             */
            final double deltaYaw = MathUtil.distanceBetweenAngles(data.prediction.getLastYaw(), data.prediction.getYaw());
            final double deltaPitch = MathUtil.distanceBetweenAngles(data.prediction.getLastPitch(), data.prediction.getPitch());

            if (deltaYaw < 1.F && deltaPitch < 0.5F) {
                this.debug(String.format("absY=%.3f absP=%.3f", deltaYaw, deltaPitch));
                break check;
            }

            /*
             * Flooring a double converts it to an integer which then we can check for its distance to the original
             * value the most accurate way possible. We will later on be able to analyze this distance.
             */
            final double floorDeltaX = Math.abs(Math.floor(deltaX) - deltaX);
            final double floorDeltaY = Math.abs(Math.floor(deltaY) - deltaY);

            /*
             * This is how we are going to be filtering it out most of the false cinematic flags which we want to
             * avoid. This is the most light weight way we can run the cinematic check without being prone to false flags.
             */
            final double formatX = data.sensitivity.getFormatX();
            final double formatY = data.sensitivity.getFormatY();

            /*
             * If the value is smaller than 0.005 it is a very strong indication that the player was using cinematic camera.
             * If someone were to abuse this method here they would not go very far since it will make their aura extremely slow.
             */
            if (formatX < 0.005 || formatY < 0.005 || Double.isNaN(formatX) || Double.isNaN(formatY)) {
                this.debug(String.format("formatX=%.5f formatY=%.5f", formatX, formatY));
                break check;
            }

            /*
             * These are going to be our mandatory actions which we are going to be using to filter out
             * stuff which we really do not want to use. This is done to stop having useless false flags.
             */
            final boolean attacking = !TimeUtil.elapsed(data.combat.getLastAttack(), 150);
            final boolean rate = !TimeUtil.elapsed(data.sensitivity.getRate(), 120);

            /*
             * We're returning the attack and the rate to make sure that the player is not using cinematic camera
             * doing something unrelated and or completely not attacking. This would not false on production with these.
             */
            if (!attacking || !rate) {
                this.debug(String.format("attacking=%s rate=%s", attacking, rate));
                break check;
            }

            /*
             * This is the only valid range of where the player can have their mouse delta to be. This is much more
             * accurate than having a round since it is directly checking its distance to its conversion to an integer.
             */
            final boolean properX = floorDeltaX <= 0.02 || floorDeltaX >= 0.98;
            final boolean properY = floorDeltaY <= 0.02 || floorDeltaY >= 0.98;

            final String debug = String.format("floorDeltaX=%.5f floorDeltaY=%.5f formatX=%.5f formatY=%.5f computedX=%.5f computedY=%.5f",
                    floorDeltaX, floorDeltaY, formatX, formatY, combinedX, combinedY
            );

            if (!properX && !properY) {
                if (buffer++ > 2.F) {
                    this.log(
                            new Debug<>("floorDeltaX", floorDeltaX),
                            new Debug<>("floorDeltaY", floorDeltaY),
                            new Debug<>("formatX", formatX),
                            new Debug<>("formatY", formatY),
                            new Debug<>("computedX", combinedX),
                            new Debug<>("computedY", combinedY)
                    );
                }
                this.debug("[FLAGGED] " + debug);
                break check;
            } else {
                this.buffer = Math.max(0.0F, buffer - 0.25F);
            }

            this.debug(debug);
        }
    }
}
