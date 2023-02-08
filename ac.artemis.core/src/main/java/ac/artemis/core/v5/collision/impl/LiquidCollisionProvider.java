package ac.artemis.core.v5.collision.impl;

import ac.artemis.core.v5.collision.BlockCollisionProvider;
import ac.artemis.core.v5.emulator.Emulator;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.block.impl.BlockLiquid;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.minecraft.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class LiquidCollisionProvider implements BlockCollisionProvider {

    @Override
    public List<Block> getCollidingBlocks(final BoundingBox bb, final Emulator entity) {
        final List<Block> blocks = new ArrayList<>();

        final int minX = MathHelper.floor_double(bb.minX);
        final int maxX = MathHelper.floor_double(bb.maxX);
        final int minY = MathHelper.floor_double(bb.minY);
        final int maxY = MathHelper.floor_double(bb.maxY);
        final int minZ = MathHelper.floor_double(bb.minZ);
        final int maxZ = MathHelper.floor_double(bb.maxZ);

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                if (!entity.getWorld().isLoaded(x >> 4, z >> 4))
                    continue;

                for (int y = minY; y <= maxY; ++y) {
                    final Block block = entity.getWorld().getBlockAt(x, y, z);
                    if (!(block instanceof BlockLiquid)) continue;

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }
}