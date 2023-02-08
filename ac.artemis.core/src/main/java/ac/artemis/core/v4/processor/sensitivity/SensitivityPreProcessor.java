package ac.artemis.core.v4.processor.sensitivity;

import ac.artemis.core.v5.utils.minecraft.MathHelper;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.sensitivity.SensitivityTable;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientLook;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Ghast
 * @since 31/08/2020
 * Artemis © 2020
 */

public class SensitivityPreProcessor extends AbstractHandler {

    private float rotationYaw;
    private float rotationPitch;
    private float lastRotationYaw;
    private float lastRotationPitch;
    private float deltaYaw;
    private float deltaPitch;
    private float lastDeltaYaw;
    private float lastDeltaPitch;

    public SensitivityPreProcessor(final PlayerData data) {
        super("Sensitivity [0x01]", data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientLook) {
            final PacketPlayClientLook wrapper = (PacketPlayClientLook) packet;

            this.rotationYaw = wrapper.getYaw();
            this.rotationPitch = wrapper.getPitch();

            /*
             * Here we exempt fucking ridiculous pitches and stupid rotations as they simply have a tendency to break
             * the laws of physics, thermodynamics and quantum mechanics. Jk. Clamping breaks shit. Expected behaviour.
             */
            final boolean isGayPitch = Math.abs(rotationPitch) > 85.F;

            if (isGayPitch) {
                return;
            }

            deltaYaw = Math.abs(rotationYaw - lastRotationYaw);
            deltaPitch = Math.abs(rotationPitch - lastRotationPitch);

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
             * This is probably one of the more simple ways to account for cinematic camera. Because of my checks
             * mostly consisting of sample checks, this is a perfect fix. Cinematic camera has an almost non-existent
             * rate of change meaning this should almost always never happen when using cinematic.
             */
            if (joltX > 1.0 && joltY > 1.0) data.sensitivity.rate = packet.getTimestamp();

            /*
             * This is getting the divisors for yaw and pitch through the use of GCD. We cannot run the prediction
             * if we do not have the divisor since it is going to be impossible for us to get mouse deltas.
             */
            final double divisorYaw = this.getDivisor(deltaYaw, lastDeltaYaw);
            final double divisorPitch = this.getDivisor(deltaPitch, lastDeltaPitch);
            double computed = this.getDivisor((float) divisorYaw, (float) divisorPitch);

            computed = Math.max(computed, divisorYaw);
            computed = Math.max(computed, divisorPitch);

            /*
             * The minimum sensitivity is 0.0, and the max one is 200. There is no reason for us to entry anything
             * else in this check since it would result in false positives which we do not want. Do not change
             * this unless you know what you are doing. All the math can be found in EntityRenderer.onRender();
             */
            if (divisorYaw > 1e-12 && divisorPitch > 1e-12 && divisorYaw < 2 && divisorPitch < 2) {
                /*
                 * This is the parser for the last valid entry from the rotation filter. We're using a rotation
                 * filter to filter out any entries that otherwise would be invalid to process.
                 */
                data.sensitivity.currentDivisorYaw = divisorYaw;
                data.sensitivity.currentDivisorPitch = divisorPitch;
                data.sensitivity.currentDivisorComputed = computed;

                /*
                 * This is putting the divisors into our grid sample array. We're using a constantly
                 * increasing number called "rotation" which works similarly to a moving size for out arrays.
                 */
                data.sensitivity.gridYaw[data.sensitivity.rotations % data.sensitivity.gridYaw.length] = divisorYaw;
                data.sensitivity.gridPitch[data.sensitivity.rotations % data.sensitivity.gridPitch.length] = divisorPitch;
                data.sensitivity.gridComputed[data.sensitivity.rotations % data.sensitivity.gridComputed.length] = computed;

                /*
                 * This is increasing the rotations in regards to our entries. We need to increase it
                 * after the parsing since the first array entry point is 0, thus we need to increase it here.
                 */
                ++data.sensitivity.rotations;
            }

            process: {
                if (data.sensitivity.gridYaw.length == 0.0 && data.sensitivity.gridPitch.length == 0.0) break process;

                /*
                 * This is checking if the rotations exceeded the grid length. This means that the amount of
                 * rotations finally surpassed the entry points which means our samples have been filled.
                 */
                final boolean filledYaw = data.sensitivity.rotations > data.sensitivity.gridYaw.length;
                final boolean filledPitch = data.sensitivity.rotations > data.sensitivity.gridPitch.length;

                /*
                 * Here we will be running the code for the filled samples. We will be parsing the final
                 * data remotely so we can run the process through our checks afterwards.
                 */
                if (filledYaw && filledPitch) {
                    /*
                     * This is getting the most repeated value from the grid samples. Even though the
                     * true sensitivity is returned occasionally, we also need to make sure that we're grabbing
                     * the most common one from the bunch to just increase the accuracy by a mile.
                     */
                    final double duplicateYaw = MathUtil.getMode(data.sensitivity.gridYaw);
                    final double duplicatePitch = MathUtil.getMode(data.sensitivity.gridPitch);

                    /*
                     * This is the actual sensitivity calculation which is what we're going to base our checks,
                     * detections and handling later for. This is the proper sensitivity.
                     */
                    final double duplicateComputed = MathUtil.getMode(data.sensitivity.gridComputed);

                    /*
                     * This is parsing the mode / most common entry we found above so we can process
                     * it through with our checks. Make sure this happens or the check will not work.
                     */
                    data.sensitivity.modeYaw = duplicateYaw;
                    data.sensitivity.modePitch = duplicatePitch;
                    data.sensitivity.modeComputed = duplicateComputed;

                    /*
                     * This is resetting the grid entries for yaw and pitch. There is no
                     * better way to do this since it is basically renewing the whole sample object.
                     */
                    data.sensitivity.gridYaw = new double[data.sensitivity.gridYaw.length];
                    data.sensitivity.gridPitch = new double[data.sensitivity.gridPitch.length];
                    data.sensitivity.gridComputed = new double[data.sensitivity.gridComputed.length];

                    /*
                     * Reset the rotation variable since we will be needing to run the process all over again
                     * soon which will not happen if we do not reset this variable here.
                     */
                    data.sensitivity.rotations = 0;
                }
            }

            integer: {
                if (deltaYaw < .1F || deltaPitch < .1F || deltaYaw > 20F || deltaPitch > 20F) break integer;

                processIntegerSensitivity();
            }

            /*
             * We are essentially trying to predict the next yaw and pitch angles of the player through some intensive
             * calculations which are mirroring the game code 1/1. These methods are extremely accurate and should be able
             * to detect the great majority of kill auras that are ever going to be used in any network.
             */
            predict: {
                if (data.sensitivity.modeYaw == Double.MIN_VALUE || data.sensitivity.modePitch == Double.MIN_VALUE) break predict;

                /*
                 * We're grabbing the sensitivity from the mode yaw and pitch. We're not doing this with sensitivity directly
                 * since then we would need to use a table to decrypt the sensitivity which is just a main in the ass and
                 * very unreliable since it can be abused to even crash servers by sending impossible sensitivities.
                 */
                data.sensitivity.sensitivityX = this.getSensitivity(data.sensitivity.modeYaw);
                data.sensitivity.sensitivityY = this.getSensitivity(data.sensitivity.modePitch);
                data.sensitivity.sensitivity = this.getSensitivity(data.sensitivity.modeComputed);

                /*
                 * This variable here is going to be used for the case of a sensitivity failure when accounting for the
                 * gcd. This may happen if our gcd formula fails but it should be a very rare occasion.
                 */
                data.sensitivity.sensitivityXY = Math.abs(data.sensitivity.sensitivityX - data.sensitivity.sensitivityY);

                /*
                 * We're getting the format through the grid method I showed below which is returning the possibility of
                 * an invalid rotation being sent by cinematic camera. Keep in mind we're only running the checks in combat so
                 * this cinematic patch should work fine for the most part. I mean why would one use it in pvp anyway.
                 */
                data.sensitivity.formatX = this.getGrid(data.sensitivity.gridYaw);
                data.sensitivity.formatY = this.getGrid(data.sensitivity.gridPitch);

                /*
                 * We're grabbing the deltaX and deltaY using the primitive modes which are essentially holding all the math
                 * that the client tried to register. We could use our own but we want to keep it pure for the checks we're doing.
                 */
                data.sensitivity.lastDeltaX = data.sensitivity.deltaX;
                data.sensitivity.lastDeltaY = data.sensitivity.deltaY;
                data.sensitivity.deltaX = deltaYaw / data.sensitivity.modeYaw;
                data.sensitivity.deltaY = deltaPitch / data.sensitivity.modePitch;

                /*
                 * Here we're essentially converting the rotations to a regular degree based circle (completely useless).
                 * We then compared the two distances and pick the smallest one to estimate the direction taken for the
                 * rotation. This is essentially as good as it can get
                 */
                final float currentX = toRegularCircle(lastRotationYaw);
                final float targetX = toRegularCircle(rotationYaw);
                final float distanceX = MathUtil.distanceBetweenAngles(lastRotationYaw, rotationYaw);

                final double polarX = MathUtil.distanceBetweenAngles(currentX + distanceX, targetX);
                final double polarX2 = MathUtil.distanceBetweenAngles(currentX - distanceX, targetX);

                final int invertX = polarX < polarX2 ? 1 : -1;
                final int invertY = rotationPitch - lastRotationPitch > 0 ? 1 : -1;

                /*
                 * In order to predict the next yaw and pitch we need to also know which direction we need to predict to. If
                 * we have no clue of the direction then it's like trying to predict what's closer and run that which is bad practice.
                 */
                data.sensitivity.inverseYaw = invertX;
                data.sensitivity.inversePitch = invertY;

                /*
                 * This is finally running the prediction formula which will print out a very nice value which we can use
                 * to check how much the player decided to move their head relative to the one we predicted.
                 */
                data.sensitivity.lastPredictedYaw = data.sensitivity.predictedYaw;
                data.sensitivity.lastPredictedPitch = data.sensitivity.predictedPitch;
                data.sensitivity.predictedYaw = this.getPredictedYaw(data.sensitivity.deltaX, data.sensitivity.sensitivityX);
                data.sensitivity.predictedPitch = this.getPredictedPitch(data.sensitivity.deltaY, data.sensitivity.sensitivityY);

                /*
                 * This is getting the actual minimum rotation from a certain sensitivity without the use of a table.
                 * We could do 1 * mode which would do the same thing but for the sake of the checks we're running we want to
                 * use our calculations since it will be usually what clients will fuck up the most which is funny.
                 */
                data.sensitivity.minimumYaw = this.getMinimumRotation(data.sensitivity.sensitivityX, lastRotationYaw, data.sensitivity.inverseYaw);
                data.sensitivity.minimumPitch = this.getMinimumRotation(data.sensitivity.sensitivityY, lastRotationPitch, data.sensitivity.inversePitch);

                /*
                 * This is calculating the sensitivity from the last valid divisor which is going to be used to check
                 * how far apart the computed and the current sensitivity are which will in-turn help us filter out false flags.
                 */
                data.sensitivity.computedX = this.getSensitivity(data.sensitivity.currentDivisorYaw);
                data.sensitivity.computedY = this.getSensitivity(data.sensitivity.currentDivisorPitch);

                /*
                 * This is simply the distance of the predicted and the actual head positions. We're only setting it if
                 * there was a head movement though to avoid false flags with rounding downwards instead of upwards.
                 */
                data.sensitivity.distanceYaw = (int) data.sensitivity.deltaX > 1 ? Math.abs(rotationYaw - data.sensitivity.predictedYaw) : 0.0f;
                data.sensitivity.distancePitch = (int) data.sensitivity.deltaY > 1 ? Math.abs(rotationPitch - data.sensitivity.predictedPitch) : 0.0f;

                /*
                 * If we imagine the cursor of the player as the center of the screen then there are only so many possible
                 * rotations that can be made inside of that window, and thus we can use this to filter out the wrong ones.
                 */
                data.sensitivity.enclosesYaw = this.encloses(lastRotationYaw, data.sensitivity.predictedYaw, ceil(rotationYaw));
                data.sensitivity.enclosesPitch = this.encloses(lastRotationPitch, data.sensitivity.predictedPitch, ceil(rotationPitch));

                /*
                 * This is quite useless but we are doing it anyway since it is done inside of the game so we are emulating it
                 * specifically here which is going to be acting as our finish in the cinematic prediction.
                 */
                data.sensitivity.smoothCamYaw += this.getProbableYaw(data.sensitivity.deltaX, data.sensitivity.sensitivityX);
                data.sensitivity.smoothCamPitch += this.getProbablePitch(data.sensitivity.deltaY, data.sensitivity.sensitivityY);

                /*
                 * Smooth yaw and pitch which is used through sensitivity. This is done through
                 * the mouse filters which is used in the game for some reason. Quite accurate in our case though so it does not
                 * really matter. Also we are going to be using this for checks if a kill aura is trying to mimic cinematic.
                 */
                data.sensitivity.smoothCamFilterX = this.getSmoothYaw(data.sensitivity.sensitivityX);
                data.sensitivity.smoothCamFilterY = this.getSmoothPitch(data.sensitivity.sensitivityY);

                /*
                 * This is running the setAngles formula to predict the next cinematic rotation which we are going to be
                 * using to emulate the cinematic / zoom camera inside of the game which we are going to use to prevent falses.
                 */
                data.sensitivity.cinematicYaw = lastRotationYaw + (data.sensitivity.smoothCamFilterX * data.sensitivity.inverseYaw * 0.15F);
                data.sensitivity.cinematicPitch = lastRotationPitch + (data.sensitivity.smoothCamFilterY * data.sensitivity.inversePitch * 0.15F);

                /*
                 * We're resetting the smooth camera yaw so we can do the magic cinematic processing below.This is using weird math.
                 * We're doing it like this to stay as close as we can to the game's source-code. Do not change any of this.
                 */
                data.sensitivity.smoothCamYaw = 0.0f;
                data.sensitivity.smoothCamPitch = 0.0f;

                /*
                 * Here we store the data between the
                 */
                data.sensitivity.deltaDifferenceX = Math.abs(data.sensitivity.deltaX - Math.round(data.sensitivity.deltaX));
                data.sensitivity.deltaDifferenceY = Math.abs(data.sensitivity.deltaY - Math.round(data.sensitivity.deltaY));

                /*
                 * Here we attempt getting the derivation between the offset and the data. This derivation represents the
                 * general acceleration behind the offset. Would the derivation be 0, such would mean the offset is 0.
                 * Would the derivation increase, such would mean the offset is caused not by the general offset due to
                 * the delta but moreover influenced by a true flaw (a cheat!)
                 *
                 * I think? This could be flawed. I forgot how I did this.
                 */
                final double derivationX = data.sensitivity.getDerivation(differenceYaw, data.sensitivity.deltaDifferenceX);
                final double derivationY = data.sensitivity.getDerivation(differencePitch, data.sensitivity.deltaDifferenceY);

                data.sensitivity.derivationX = derivationX;
                data.sensitivity.derivationY = derivationY;

                /*
                 * Here to not have to deal with always setting the offsets and blabla comparisons we simply cache the
                 * distance between the received yaw and the predicted yaw. This will aid us in various aim checks,
                 * including the AimPrediction2 one.
                 */
                final float deltaYaw = MathUtil.distanceBetweenAngles(rotationYaw, data.sensitivity.predictedYaw);
                final float deltaPitch = Math.abs(rotationPitch - data.sensitivity.predictedPitch);

                data.sensitivity.differenceX = deltaYaw;
                data.sensitivity.differenceY = deltaPitch;
            }

            this.lastRotationYaw = rotationYaw;
            this.lastRotationPitch = rotationPitch;
            this.lastDeltaYaw = deltaYaw;
            this.lastDeltaPitch = deltaPitch;
        }
    }

    private void processIntegerSensitivity() {
        final float gcd = (float) this.getGcd(deltaPitch, lastDeltaPitch);

        final double sensitivityModifier = Math.cbrt(0.8333 * gcd);
        final double sensitivityStepTwo = (1.666 * sensitivityModifier) - 0.3333;

        final int product = (int) (this.getSensitivity(gcd) * 200);

        data.sensitivity.integerSensitivitySamples.add(product);

        if (data.sensitivity.integerSensitivitySamples.size() == 40) {
            data.sensitivity.integerSensitivity = MathUtil.getMode(data.sensitivity.integerSensitivitySamples);

            final int sensitivity = data.sensitivity.integerSensitivity;

            if (sensitivity > 0 && sensitivity < 200)
                data.sensitivity.sensitivityTableValue = SensitivityTable.SENSITIVITY_MCP_VALUES
                        .get(data.sensitivity.integerSensitivity);

            data.sensitivity.integerSensitivitySamples.clear();
        }
    }

    private double getGcd(final double a, final double b) {
        if (a < b) {
            return getGcd(b, a);
        }

        if (Math.abs(b) < 0.001) {
            return a;
        } else {
            return getGcd(b, a - Math.floor(a / b) * b);
        }
    }

    /*
     * Returns the rotation divisor we see as sensitivity. We will be needing this to run out
     * rotations checks without having to do any intense calculations.
     */
    private double getDivisor(final float a, final float b) {
        final double bits = MathUtil.EXPANDER;

        /*
         * It is faster and more accurate to grab the greatest common divisor from a long compared to a float
         * since it is a simpler data type and it does not have a decimal form. Do not listen to anyone who
         * is saying that converting to a long is useless as they are most likely clueless about float conversion.
         */
        final long formatX = (long) (a * bits);
        final long formatY = (long) (b * bits);

        /*
         * We're now grabbing the greatest common divisor from the math util and the two formatted values via
         * the euclidean formula to get the most accurate value the fastest way possible. After we get a result
         * we will be dividing it by the bits to convert it to a double again so it stays in its original form.
         */
        final double divisor = MathUtil.getGcd(formatX, formatY) / bits;

        /*
         * We're rounding the number to decrease the margin of error in checks due to small sensitivity calculation
         * mistakes. All of the checks should function the same and they will not be effected due to this change.
         */
        return MathUtil.round(divisor, 5);
    }

    /*
     * This is technically reverting the sensitivity from the divisor to start running our predictions without issue.
     * It would be wise to use a table to grab the sensitivity directly from the gcd since it is much faster to run a
     * map lookup compared to a sensitivity construction for for what is worth this is our best bet at the moment.
     */
    public double getSensitivity(final double gcd) {
        /*
         * If we take a look in the minecraft formula for rotations, this is the math done to convert the mouse delta
         * to a rotation. The math here is a little simplified to avoid having 30 needless lines of math.
         */
        final double constructed = ((float) (gcd / 0.15)) / 8.0F;
        final double product = Math.cbrt(constructed) - 0.2f;

        /*
         * Finally we're returning the product divided by .6f which is the final step to getting an accurate reading
         * from the rotation. This is most likely going to be confusing to anyone who does not know what they're doing but its fine.
         */
        return product / .6f;
    }

    /*
     * This is an attempt to reverse the logistics of cinematic camera without having to run a full on prediction using
     * mouse filters. Otherwise, we would need to run more heavy calculations which is not really production friendly.
     * It may be more accurate but it is not really worth it if in the end of the day we're eating server performance.
     */
    public double getGrid(final double[] entry) {
        /*
         * We're creating the variables average min and max to start calculating the possibility of cinematic camera.
         * Why does this work? Cinematic camera is essentially a slowly increasing slowdown (which is why cinematic camera
         * becomes slower the more you use it) which in turn makes it so the min max and average are extremely close together.
         */
        double average = 0.0;
        double min = 0.0, max = 0.0;

        /*
         * These are simple min max calculations done manually for the sake of simplicity. We're using the numbers 0.0
         * since we also want to account for the possibility of a negative number. If there are no negative numbers then
         * there is absolutely no need for us to care about that number other than getting the max.
         */
        for (final double number : entry) {
            if (number < min) min = number;
            if (number > max) max = number;

            /*
             * Instead of having a sum variable we can use an average variable which we divide
             * right after the loop is over. Smart programming trick if you want to use it.
             */
            average += number;
        }

        /*
         * We're dividing the average by the length since this is the formula to getting the average.
         * Specifically its (sum(n) / length(n)) = average(n) -- with n being the entry set we're analyzing.
         */
        average /= entry.length;

        /*
         * This is going to estimate how close the average and the max were together with the possibility of a min
         * variable which is going to represent a negative variable since the preset variable on min is 0.0.
         */
        return (max - average) - min;
    }

    private float getProbableYaw(final double deltaX, final double sensitivityX) {
        /*
         * This is adding an extra point into the delta which is essentially done to start
         * the brute force. Realistically, if the player is cheating, this is infinitesimal to them
         * But if they are messing an aura, it will be of huge impact and it will not make the check
         * any less sensitive. This is not bad for detectability whatsoever.
         */
        double point = deltaX + 1;

        float probable = 0.0F;
        float minimum = Float.MAX_VALUE;

        /*
         * We're brute-forcing the current value, the value in the the past and of course, the
         * one in the future, specifically the -+ ones closer to the one issued.
         */
        for (int i = 0; i < 3; i++) {
            final float predicted = this.getPredictedYaw(point, sensitivityX);
            final float difference = rotationYaw - predicted;

            /*
             * Of course, the one which is the closest to the smallest value is the most accurate one.
             * Theoretically we can make a check out of this but it will not be that useful in the long run.
             */
            if (difference < minimum) {
                minimum = difference;
                probable = predicted;
            }

            /*
             * We are reducing the point so we can go to the current value and then the past value,
             * basic iteration and this is the cleanest way to do it too so dont mess with it.
             */
            point--;
        }

        return probable;
    }

    private float getProbablePitch(final double deltaY, final double sensitivityY) {
        /*
         * This is adding an extra point into the delta which is essentially done to start
         * the brute force. Realistically, if the player is cheating, this is infinitesimal to them
         * But if they are messing an aura, it will be of huge impact and it will not make the check
         * any less sensitive. This is not bad for detectability whatsoever.
         */
        double point = deltaY + 1;

        float probable = 0.0F;
        float minimum = Float.MAX_VALUE;

        /*
         * We're brute-forcing the current value, the value in the the past and of course, the
         * one in the future, specifically the -+ ones closer to the one issued.
         */
        for (int i = 0; i < 3; i++) {
            final float predicted = this.getPredictedPitch(point, sensitivityY);
            final float difference = rotationPitch - predicted;

            /*
             * Of course, the one which is the closest to the smallest value is the most accurate one.
             * Theoretically we can make a check out of this but it will not be that useful in the long run.
             */
            if (difference < minimum) {
                minimum = difference;
                probable = predicted;
            }

            /*
             * We are reducing the point so we can go to the current value and then the past value,
             * basic iteration and this is the cleanest way to do it too so dont mess with it.
             */
            point--;
        }

        return probable;
    }

    private float getPredictedYaw(final double deltaX, final double sensitivityX) {
        // Cast mouse delta to an integer
        final int delta = (int) deltaX;

        // Run the sensitivity formula
        final float var132 = (float) sensitivityX * 0.6F + 0.2F;
        final float var141 = var132 * var132 * var132 * 8.0F;

        // Run the prediction formula for yaw
        final float var15 = (float) delta * var141;

        return lastRotationYaw + (var15 * 0.15F * data.sensitivity.inverseYaw);
    }

    private float getPredictedPitch(final double deltaY, final double sensitivityY) {
        // Cast mouse delta to an integer
        final int delta = (int) deltaY;

        // Run the sensitivity formula
        final float var132 = (float) sensitivityY * 0.6F + 0.2F;
        final float var141 = var132 * var132 * var132 * 8.0F;

        // Run the prediction formula for yaw
        final float var15 = (float) delta * var141;

        // Get the current yaw/pitch from the past rotation
        return lastRotationPitch + (var15 * 0.15F * data.sensitivity.inversePitch);
    }

    public float getMinimumRotation(final double sensitivity, final float lastRotation, final float rotation) {
        // Run the sensitivity formula
        final float var132 = (float) sensitivity * 0.6F + 0.2F;
        final float var141 = var132 * var132 * var132 * 8.0F;

        // Run the prediction formula for yaw
        final float var15 = (float) 1 * var141;
        final int inverse = rotation - lastRotation > 0 ? 1 : -1;

        return Math.abs(lastRotation - (lastRotation + (var15 * 0.15F * inverse)));
    }

    public float getSmoothYaw(final double sensitivityX) {
        // Run the sensitivity formula
        final float var132 = (float) sensitivityX * 0.6F + 0.2F;
        final float var141 = var132 * var132 * var132 * 8.0F;

        // Return the smoothing through the formula
        return data.sensitivity.mouseFilterXAxis.smooth(data.sensitivity.smoothCamYaw, 0.05F * var141);
    }

    public float getSmoothPitch(final double sensitivityY) {
        // Run the sensitivity formula
        final float var132 = (float) sensitivityY * 0.6F + 0.2F;
        final float var141 = var132 * var132 * var132 * 8.0F;

        // Return the smoothing through the formula
        return data.sensitivity.mouseFilterXAxis.smooth(data.sensitivity.smoothCamPitch, 0.05F * var141);
    }

    /*
     * This method is ensuring that the predicted rotation is actually matching with the one that was the entry. If
     * they are not matching there is no point in running the check as it's signifying that something went completely wrong.
     */
    private boolean encloses(final float a, final float b, final float x) {
        final float distance = (float) MathUtil.distanceBetweenAngles(a, b);

        return MathUtil.distanceBetweenAngles(a, x) < distance || MathUtil.distanceBetweenAngles(b, x) < distance;
    }

    /*
     * This is a simple ceil float method which we are going to be using for our enclosed method to filter out
     * false positives with cinematic camera. It should not be that big of a difference but it should look neat.
     */
    private float ceil(final float a) {
        final double math = Math.ceil(a);

        return (float) math;
    }

    private float toRegularCircle(float angles) {
        angles %= 360.F;
        return angles < 0 ? angles + 360.F : angles;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }


}
