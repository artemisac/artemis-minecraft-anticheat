package ac.artemis.checks.enterprise.aim;

import ac.artemis.anticheat.api.check.type.Stage;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.rotation.ComplexRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PlayerRotation;
import ac.artemis.core.v4.utils.time.TimeUtil;

@Check(type = Type.AIM, var = "GCD", alias = "Heuristic:Rotation/GCD.1!Modulo")
@Experimental(stage = Stage.PRE_RELEASE)
public class AimGcd extends ComplexRotationCheck  {

    public AimGcd(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    private float buffer;

    @Override
    public void handleRotation(final PlayerRotation from, final PlayerRotation to) {
        final float predictedYaw = data.sensitivity.predictedYaw;
        final float predictedPitch = data.sensitivity.predictedPitch;

        final double sensitivityX = data.sensitivity.getSensitivityX();
        final double sensitivityY = data.sensitivity.getSensitivityY();
        final double sensitivityXY = Math.abs(sensitivityX - sensitivityY);

        final double mcp = data.sensitivity.getGcdFromTable();
        final double mcpX = Math.abs(sensitivityX - mcp);
        final double mcpY = Math.abs(sensitivityY - mcp);

        final double modeYaw = data.sensitivity.getModeYaw();
        final double modePitch = data.sensitivity.getModePitch();

        check: {
            if (modeYaw == Double.MIN_VALUE || modePitch == Double.MIN_VALUE) break check;

            if (sensitivityX > 1.0F || sensitivityX < 0.0F || sensitivityY > 1.0F || sensitivityY < 0.0F) {
                this.debug(String.format("sensX=%.4f sensY=%.4f", sensitivityX, sensitivityY));
                break check;
            }

            /*
            * We do not want to listen to the same tick as teleporting or you will have issues with the check sending
            * a null or non existent mouse delta. This will cause issues and sometimes it may not even be fixable
             */
            if (this.isExempt(ExemptType.TELEPORT, ExemptType.JOIN)) {
                this.debug("Teleporting invalidation");
                break check;
            }

            /*
             * These are the sensitivity values from the mode and the current ones subtracted to see how far apart they
             * are. If they are too far apart then the player changes their sensitivity which made it so the player falsed.
             */
            final double combinedX = Math.abs(sensitivityX - data.sensitivity.getComputedX());
            final double combinedY = Math.abs(sensitivityY - data.sensitivity.getComputedY());

            /*
             * As mentioned in our rotation handler, there is a very rare case of where the sensitivity is going to fail
             * either because of some aim inconsistency or because our divisor lookup was completely wrong. Sadly there is
             * not much we can do that won't be heavy for the server other than simply checking for it here.
             */
            if (sensitivityXY > 0.1 && mcpX > 0.1 && mcpY > 0.1) {
                this.debug(String.format("sensXY=%.5f mcpX=%.5f mcpY=%.5f", sensitivityXY, mcpX, mcpY));
                break check;
            }

            /*
             * This is going to be our check when a player changes their sensitivity massively which is going to
             * end up falsing the check which we do not want. There is no reason to filter small ones since they wont matter.
             */
            if (combinedX > 0.82 || combinedY > 0.82) {
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

            if (Math.abs(deltaX) < 1 || Math.abs(deltaY) < 1) {
                this.debug("Minimum or negative delta.");
                break check;
            }

            /*
             * If the value is smaller than 0.005 it is a very strong indication that the player was using cinematic camera.
             * If someone were to abuse this method here they would not go very far since it will make their aura extremely slow.
             */
            if (formatX < 0.005 || Double.isNaN(formatX) || formatY < 0.005 || Double.isNaN(formatY)) {
                this.debug(String.format("formatX=%.5f formatY=%.5f", formatX, formatY));
                break check;
            }

            /*
             * If we assume the cursor is the middle point of the screen and the area around are the only possible
             * rotations that can be made, we can accurately filter out any invalid rotation that would not be possible.
             */
            final boolean enclosesYaw = data.sensitivity.isEncloseX();
            final boolean enclosesPitch = data.sensitivity.isEncloseY();

            /*
             * If either rotation encloses we need to break otherwise we are going to be having a lot of false flags
             * which we do not want. This will not slow down the check what so ever since it is very accurate.
             */
            if (!enclosesYaw && !enclosesPitch) {
                this.debug(String.format("encloseX=%s encloseY=%s expectX=%.4f expectY=%.4f gotX=%.4f gotY=%.4f wasX=%.4f wasY=%.4f",
                        enclosesYaw, enclosesPitch,
                        predictedYaw, predictedPitch,
                        data.prediction.getYaw(), data.prediction.getPitch(),
                        data.prediction.getLastYaw(), data.prediction.getLastPitch())
                );
                break check;
            }

            /*
             * This is the only valid range of where the player can have their mouse delta to be. This is much more
             * accurate than having a round since it is directly checking its distance to its conversion to an integer.
             */
            final boolean properX = floorDeltaX <= 0.02 || floorDeltaX >= 0.98;
            final boolean properY = floorDeltaY <= 0.02 || floorDeltaY >= 0.98;

            /*
             * We want both rotations being invalid since we want the most accuracy from this check since it will be essentially
             * the only one we will be running entirely without a buffer and without any hard coded values.
             */
            if (properX || properY) {
                this.debug(String.format("properX=%.5f properY=%.5f", floorDeltaX, floorDeltaY));
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
             * This is getting the distance of our predictions and our actual entries which is going to be
             * our final flag method which is going to be used for our own sake here since it will be accurate.
             */
            final float distanceYaw = data.sensitivity.getDifferenceX();
            final float distancePitch = data.sensitivity.getDifferenceY();

            final String debug = String.format("distanceX=%.5f distanceY=%.5f formatX=%.5f formatY=%.5f computedX=%.5f computedY=%.5f",
                    distanceYaw, distancePitch, formatX, formatY, combinedX, combinedY
            );

            if (distanceYaw > 1e-04 && distancePitch > 1e-04) {
                if (buffer++ > 2.F) {
                    this.log(
                            new Debug<>("distanceX", distanceYaw),
                            new Debug<>("distanceY", distancePitch),
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
