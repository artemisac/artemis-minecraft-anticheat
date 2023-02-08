package ac.artemis.checks.regular.v2.checks.impl.aim;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.rotation.SimpleRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.SimpleRotation;

@Check(type = Type.AIM, var = "Cope")
public class AimCope extends SimpleRotationCheck {

    private float lastDeltaYaw = 0.0f, lastDeltaPitch = 0.0f;
    private double buffer = 0;

    public AimCope(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handleRotation(final SimpleRotation from, final SimpleRotation to) {
        if (data.getCombat().isCinematic() || data.getCombat().isCinematic2()) {
            this.buffer = Math.max(0, this.buffer - 0.05);
            return;
        }

        final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
        final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

        /*
         * Grab the GCD for the player on both the pitch and yaw. Thanks to our utility only taking
         * in longs we are going to expand the rotations before giving them as a parameter.
         */
        final double divisorYaw = MathUtil.getGcd((long) (deltaYaw * MathUtil.EXPANDER), (long) (lastDeltaYaw * MathUtil.EXPANDER));
        final double divisorPitch = MathUtil.getGcd((long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));

        /*
         * We are getting the best case scenario for the players constant. As this checks for small gcd's
         * we are attempting to get the largest possible one to get the best possible scenario for the player.
         */
        final double maxConstant = Math.max(divisorPitch, divisorYaw) / MathUtil.EXPANDER;

        /*
         * We need to make sure the players head movement wasn't large or too small as
         * these scenarios can severely break the gcd method and we really do not want that.
         */
        if (deltaPitch < 1.0F && deltaYaw < 1.0F) return;

        /*
         * In Minecraft our minimum GCD on all sensitivities is 0.0078125. We can use this
         * to check if  the user's aim does not have a constant which we can just flag for.
         */
        if (maxConstant < 0.0078125F) {
            this.buffer += 0.3D;

            if (this.buffer > 12) {
//                this.log(
//                        new Debug<>("gcd", maxConstant),
//                        new Debug<>("dP", deltaPitch),
//                        new Debug<>("dY", deltaYaw)
//                );
            }
        }

        this.buffer = Math.max(0, this.buffer - 0.1D);

        this.lastDeltaYaw = deltaYaw;
        this.lastDeltaPitch = deltaPitch;
    }
}

