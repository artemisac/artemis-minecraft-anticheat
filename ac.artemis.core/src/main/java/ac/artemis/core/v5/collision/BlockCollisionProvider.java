package ac.artemis.core.v5.collision;


import ac.artemis.core.v5.collision.impl.BukkitCollisionProvider;
import ac.artemis.core.v5.collision.impl.LiquidCollisionProvider;
import ac.artemis.core.v5.emulator.Emulator;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.utils.bounding.BoundingBox;

import java.util.List;

public interface BlockCollisionProvider {
    List<Block> getCollidingBlocks(final BoundingBox boundingBox, final Emulator entity);

    BlockCollisionProvider PROVIDER = new BukkitCollisionProvider();
    BlockCollisionProvider LIQUID_PROVIDER = new LiquidCollisionProvider();
}
