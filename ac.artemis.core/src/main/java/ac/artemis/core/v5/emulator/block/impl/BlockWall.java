package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.block.BlockUtil;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.packet.minecraft.block.BlockFace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockWall extends Block {

    public BlockWall(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.COBBLESTONE_WALL, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final List<BoundingBox> boundingBoxes = new ArrayList<>();

        final Block block = world.getBlockAt(location.getX(), location.getY(), location.getZ());

        if (block == null) return boundingBoxes;

        final boolean flag = this.canConnectTo(location.getRelative(BlockFace.NORTH), world);
        final boolean flag1 = this.canConnectTo(location.getRelative(BlockFace.SOUTH), world);
        final boolean flag2 = this.canConnectTo(location.getRelative(BlockFace.WEST), world);
        final boolean flag3 = this.canConnectTo(location.getRelative(BlockFace.EAST), world);

        float f = 0.25F;
        float f1 = 0.75F;
        float f2 = 0.25F;
        float f3 = 0.75F;
        float f4 = 1.0F;

        if (flag) {
            f2 = 0.0F;
        }

        if (flag1) {
            f3 = 1.0F;
        }

        if (flag2) {
            f = 0.0F;
        }

        if (flag3) {
            f1 = 1.0F;
        }

        if (flag && flag1 && !flag2 && !flag3) {
            f4 = 0.8125F;
            f = 0.3125F;
            f1 = 0.6875F;
        } else if (!flag && !flag1 && flag2 && flag3) {
            f4 = 0.8125F;
            f2 = 0.3125F;
            f3 = 0.6875F;
        }

        /*if (flag) {
            f2 = 0.0F;
        }

        if (flag1) {
            f3 = 1.0F;
        }

        this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);*/

        return Collections.singletonList(getFromPoint(location, f, 0.0F, f2, f1, f4, f3));
    }

    private boolean canConnectTo(final NaivePoint location, final ArtemisWorld world) {
        final Block block = world.getBlockAt(location.getX(), location.getY(), location.getZ());

        return (BlockUtil.isWall(block.getMaterial())
                || BlockUtil.isFenceGate(block.getMaterial())
                || (block.getMaterial().getMaterial().isBlock()
                && block.getMaterial().getMaterial().isOccluding()));
    }
}
