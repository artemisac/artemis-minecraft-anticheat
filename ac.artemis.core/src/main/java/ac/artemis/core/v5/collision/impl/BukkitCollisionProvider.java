package ac.artemis.core.v5.collision.impl;


import ac.artemis.core.v5.collision.BlockCollisionProvider;
import ac.artemis.core.v5.emulator.Emulator;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import cc.ghast.packet.nms.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class BukkitCollisionProvider implements BlockCollisionProvider {

    @Override
    public List<Block> getCollidingBlocks(final BoundingBox bb, final Emulator entity) {
        final List<Block> blocks = new ArrayList<>();

        final int minX = MathHelper.floor(bb.minX);
        final int maxX = MathHelper.floor(bb.maxX + 1.0D);
        final int minY = MathHelper.floor(bb.minY);
        final int maxY = MathHelper.floor(bb.maxY + 1.0D);
        final int minZ = MathHelper.floor(bb.minZ);
        final int maxZ = MathHelper.floor(bb.maxZ + 1.0D);

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                if (!entity.getWorld().isLoaded(x >> 4, z >> 4)) {
                    continue;
                }

                for (int y = minY - 1; y <= maxY; ++y) {
                    final Block block = entity.getWorld().getBlockAt(x, y, z);
                    if (block == null) continue;

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }
}
