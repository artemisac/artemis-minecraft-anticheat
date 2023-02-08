package ac.artemis.checks.regular.v2.checks.impl.inventory;


import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.SimplePositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.SimplePosition;

/**
 * @author 7x6
 * @since 30/08/2019
 */
@Check(type = Type.INVENTORYWALK, var = "Moving")
@Experimental
public class InventoryMoving extends SimplePositionCheck {

    private int buffer;
    private double previousHorizontalSpeed;

    public InventoryMoving(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(final SimplePosition from, final SimplePosition to) {
        final boolean exempt = this.isExempt(
                ExemptType.GAMEMODE,
                ExemptType.LIQUID,
                ExemptType.MOVEMENT
        );

        if (exempt) {
            this.debug("exempted");
            return;
        }

        final double deltaX = to.getX() - from.getX();
        final double deltaZ = to.getZ() - from.getZ();

        final double horizontalSpeed = Math.max(Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2)) - data.entity.getMotion().getHorizontal(), 0.0D);

        if (horizontalSpeed > previousHorizontalSpeed && data.user.isInventoryOpen()) {
            if (++buffer > 2)
                log("buffer=" + buffer + " hSpeed=" + horizontalSpeed);
        } else
            buffer = 0;

        this.previousHorizontalSpeed = horizontalSpeed;
    }
}
