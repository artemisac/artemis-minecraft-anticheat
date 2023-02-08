package ac.artemis.core.v5.emulator.collision;

import ac.artemis.core.v5.emulator.Emulator;
import ac.artemis.core.v5.utils.bounding.BoundingBox;

import java.util.List;

public interface CollisionProvider {
    List<BoundingBox> getBoundingBoxes(final Emulator data, final BoundingBox boundingBox);
}
