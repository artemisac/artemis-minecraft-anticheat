package ac.artemis.checks.regular.v2.checks.impl.nofall;

import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.ComplexPositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.core.v5.utils.MathUtil;
import ac.artemis.core.v5.utils.OldMathUtil;

/**
 * @author Ghast
 * @since 20-May-20
 */

@Check(type = Type.NOFALL, var = "Simple")
@Experimental
public class NoFallSimple extends ComplexPositionCheck {

    public NoFallSimple(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(final PlayerPosition from, final PlayerPosition to) {
        final double deltaX = to.getX() - from.getX();
        final double deltaY = to.getY() - from.getY();
        final double deltaZ = to.getZ() - from.getZ();

        final double horizontal = MathUtil.hypot(deltaX, deltaZ);
        final double vertical = Math.abs(deltaY);

        final boolean exempt = this.isExempt(ExemptType.VOID, ExemptType.VELOCITY, ExemptType.TELEPORT, ExemptType.FLIGHT);
        final boolean ground = data.movement.onGround;

        process: {
            if (exempt || horizontal > 90 || vertical > 90) break process;

            if (ground && deltaY > 0.35) this.log();
        }
    }
}
