package ac.artemis.anticheat.engine.v2.flying.impl;

import ac.artemis.anticheat.engine.v2.flying.EntityFlyingProvider;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.attributes.EntityAttributes;
import cc.ghast.packet.nms.MathHelper;


/**
 * @author Ghast
 * @since 03/02/2021
 * Artemis © 2021
 */
public class LegacyEntityFlyingProvider implements EntityFlyingProvider {

    /**
     * Corresponds to moveFlying in EntityLivingBase. This is the fundamental
     * part which controls the motion acceleration based on moveForward and
     * moveStrafe
     * @param run Transition data object for all the info
     * @return Same reference to an identical or cloned transition data with
     *         all the modifications done
     */
    @Override
    public TransitionData provide(final TransitionData run) {
        final float friction = run.getFriction();
        final float rotationYaw = run.poll(EntityAttributes.YAW);

        float strafe = run.getMoveStrafe();
        float forward = run.getMoveForward();

        double motionX = run.getMotionX();
        double motionZ = run.getMotionZ();

        float key = strafe * strafe + forward * forward;

        /*
         * Don't ask why I called it key. I refer to the keyboard. This is the
         * squared distance of a vector visualization of the WASD keys. In the
         * case it's too small, movement remains untouched and stays at the set
         * previous velocity
         */
        if (key >= 1.0E-4F) {
            key = MathHelper.c(key);

            /*
             * In the case here, if the movement is too small, it's put back to
             * a default minimum
             */
            if (key < 1.0F) {
                key = 1.0F;
            }

            /*
             * This bit happens to be very confusing for a lot of individuals,
             * including myself.
             * In short, the WASD factor (moveForward and moveStrafe) are dividing
             * factors to the acceleration, also known as friction. As we know that
             * the key value is positive and superior to 1 at all times, we know this
             * is a division which will at best remain identical
             *
             * -> motionStrafe/motionForward
             * As for motionStrafe, this gives us a set directional value to apply.
             * Would this not be the case, it would make movement very choppy and
             * XYZ oriented instead of giving a larger range
             */
            key = friction / key;
            strafe = strafe * key;
            forward = forward * key;

            /*
             * This is pretty much the conversion to adding it to the velocity
             * by granting it a direction as a vector. The trigonometry is rather
             * simple. X and Z represent the vector point position on a circle.
             * If you're curious about this, view :
             * https://courses.lumenlearning.com/boundless-algebra/chapter/trigonometric-functions-and-the-unit-circle/
             * https://textimgs.s3.amazonaws.com/boundless-algebra/lhntemhracxenling6eg.jpe
             *
             * The rest is simply just proper trigonometry addition.
             */
            final float x = MathHelper.sin(rotationYaw * (float) Math.PI / 180.0F);
            final float z = MathHelper.cos(rotationYaw * (float) Math.PI / 180.0F);

            motionX += strafe * z - forward * x;
            motionZ += forward * z + strafe * x;
        }

        run.setMotionX(motionX);
        run.setMotionZ(motionZ);

        return run;
    }
}
