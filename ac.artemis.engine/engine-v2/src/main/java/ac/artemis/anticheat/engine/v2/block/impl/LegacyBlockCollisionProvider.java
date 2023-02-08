package ac.artemis.anticheat.engine.v2.block.impl;

import ac.artemis.anticheat.engine.v2.block.BlockCollisionProvider;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.block.CollisionBlockState;
import ac.artemis.core.v5.utils.EntityUtil;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

public class LegacyBlockCollisionProvider implements BlockCollisionProvider {
    @Override
    public void doBlockCollisions(TransitionData data) {
        final NaivePoint startBlockPos = new NaivePoint(
                data.getBoundingBox().minX + 0.001D,
                data.getBoundingBox().minY + 0.001D,
                data.getBoundingBox().minZ + 0.001D
        );

        final NaivePoint endBlockPost = new NaivePoint(
                data.getBoundingBox().maxX - 0.001D,
                data.getBoundingBox().maxY - 0.001D,
                data.getBoundingBox().maxZ - 0.001D
        );

        if (!EntityUtil.isAreaLoaded(data.getData().getPlayer().getWorld(), startBlockPos, endBlockPost)) {
            return;
        }

        for (int x = startBlockPos.getX(); x <= endBlockPost.getX(); ++x) {
            for (int y = startBlockPos.getY(); y <= endBlockPost.getY(); ++y) {
                for (int z = startBlockPos.getZ(); z <= endBlockPost.getZ(); ++z) {
                    final Block block = data.getData().getEntity().getWorld().getBlockAt(x, y, z);

                    final boolean isCollideState = block instanceof CollisionBlockState;

                    if (!isCollideState) continue;

                    final CollisionBlockState blockState = (CollisionBlockState) block;
                    blockState.onCollidedBlockState(data);
                }
            }
        }
    }
}
