package ac.artemis.checks.regular.v2.checks.impl.jesus;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.SimplePositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.SimplePosition;

/**
 * @author Ghast
 * @since 20-May-20
 */

@Check(type = Type.JESUS, var = "Simple")
public class JesusSimple extends SimplePositionCheck {

    private double buffer;

    public JesusSimple(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(final SimplePosition from, final SimplePosition to) {
        final boolean invalidCond = this.isExempt(ExemptType.VEHICLE, ExemptType.VOID, ExemptType.LIQUID_WALK, ExemptType.SLIME);
        if (invalidCond) return;

        // User must be touching liquid
        final boolean liquid = data.collision.getCollidingBlocksY1NMS().stream()
                .allMatch(nms -> nms == NMSMaterial.AIR || nms == NMSMaterial.WATER || nms == NMSMaterial.LAVA);

        // If the user is on liquid and is pretty much just hovering over, flag him
        final boolean flag = liquid && from.getY() == to.getY();

        if (flag) {
            // Increase flags by increments of 5
            if (++buffer > 20) {
                log();
            }
        } else {
            this.buffer = 0;
        }
    }
}
