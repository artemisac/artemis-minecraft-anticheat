package ac.artemis.checks.regular.v2.checks.impl.jesus;

import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.ComplexPositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.PlayerPosition;

import java.util.Arrays;

/**
 * @author Ghast
 * @since 20-May-20
 */

@Check(type = Type.JESUS, var = "Complex")
@Experimental
public class JesusComplex extends ComplexPositionCheck {

    // Not necessarily needed oof
    private static final NMSMaterial[] INVALID = {NMSMaterial.WATER, NMSMaterial.LAVA};

    public JesusComplex(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(final PlayerPosition from, final PlayerPosition to) {
        if (isExempt(ExemptType.LIQUID_WALK)) return;

        final double deltaY = from.distanceY(to);

        final boolean distanceFlag = deltaY < 0.001;
        final boolean collisionFlag = MathUtil.containsArray(INVALID, data.collision.getCollidingBlocks0());

        if (distanceFlag && collisionFlag) {
            this.log("dy=" + deltaY + " collisions=" + Arrays.toString(data.collision.getCollidingBlocks0().toArray()));
        }
    }
}
