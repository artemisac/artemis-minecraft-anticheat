package ac.artemis.core.v5.emulator.block;

import ac.artemis.core.v5.emulator.attributes.Attributable;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;

import java.util.List;

public interface BlockProvider extends Attributable {
    boolean canCollide();
    List<BoundingBox> getBoundingBox(final ArtemisWorld world);
}
