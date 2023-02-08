package ac.artemis.core.v5.emulator.block;

import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;

public interface CollisionLiquid {
    Point modifyAcceleration(final TransitionData emulator, final NaivePoint blockPos, final Point motion);
}
