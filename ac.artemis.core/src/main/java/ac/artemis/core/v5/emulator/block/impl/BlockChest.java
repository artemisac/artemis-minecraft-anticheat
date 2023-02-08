package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.packet.minecraft.block.BlockFace;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.Collections;
import java.util.List;

/**
 * @author Ghast
 * @since 19/02/2021
 * Artemis © 2021
 */
public class BlockChest extends Block {
    public BlockChest(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.CHEST, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        // Actual collisions
        final BoundingBox boundingBox;

        check: {
            NaivePoint relative;
            Block block;

            relative = location.getRelative(BlockFace.NORTH);

            block = world.getBlockAt(relative.getX(), relative.getY(), relative.getZ());
            if (block instanceof BlockChest) {
                boundingBox = getFromPoint(location, 0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
                break check;
            }

            relative = location.getRelative(BlockFace.SOUTH);
            block = world.getBlockAt(relative.getX(), relative.getY(), relative.getZ());

            if (block instanceof BlockChest) {
                boundingBox = getFromPoint(location, 0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
                break check;
            }

            relative = location.getRelative(BlockFace.WEST);
            block = world.getBlockAt(relative.getX(), relative.getY(), relative.getZ());

            if (block instanceof BlockChest) {
                boundingBox = getFromPoint(location, 0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
                break check;
            }

            relative = location.getRelative(BlockFace.EAST);
            block = world.getBlockAt(relative.getX(), relative.getY(), relative.getZ());

            if (block instanceof BlockChest) {
                boundingBox = getFromPoint(location, 0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
                break check;
            }

            boundingBox = getFromPoint(location, 0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }

        return Collections.singletonList(boundingBox);
    }
}
