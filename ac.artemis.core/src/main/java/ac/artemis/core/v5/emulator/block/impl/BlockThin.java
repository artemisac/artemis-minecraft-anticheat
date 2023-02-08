package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.packet.minecraft.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ghast
 * @since 16/02/2021
 * Artemis © 2021
 */
public abstract class BlockThin extends Block {
    public BlockThin(NaivePoint location, EnumFacing direction) {
        super(NMSMaterial.GLASS_PANE, location, direction);
    }

    public BlockThin(NMSMaterial material, NaivePoint location, EnumFacing direction) {
        super(material, location, direction);
    }

    @Override
    public boolean canCollide() {
        return true;
    }

    @Override
    public List<BoundingBox> getBoundingBox(ArtemisWorld world) {
        final List<BoundingBox> boundingBoxes = new ArrayList<>();

        final NaivePoint north = location.getRelative(BlockFace.NORTH);
        final NaivePoint south = location.getRelative(BlockFace.SOUTH);
        final NaivePoint west = location.getRelative(BlockFace.WEST);
        final NaivePoint east = location.getRelative(BlockFace.EAST);


        boolean var7 = canConnect(world.getBlockAt(north.getX(), north.getY(), north.getZ()));
        boolean var8 = canConnect(world.getBlockAt(south.getX(), south.getY(), south.getZ()));
        boolean var9 = canConnect(world.getBlockAt(west.getX(), west.getY(), west.getZ()));
        boolean var10 = canConnect(world.getBlockAt(east.getX(), east.getY(), east.getZ()));

        if ((!var9 || !var10) && (var9 || var10 || var7 || var8)) {
            if (var9) {
                boundingBoxes.add(getFromPoint(location,0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F));
            } else if (var10) {
                boundingBoxes.add(getFromPoint(location,0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F));
            }
        } else {
            boundingBoxes.add(getFromPoint(location,0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F));
        }

        if ((!var7 || !var8) && (var9 || var10 || var7 || var8)) {
            if (var7) {
                boundingBoxes.add(getFromPoint(location,0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F));
            } else if (var8) {
                boundingBoxes.add(getFromPoint(location,0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F));
            }
        } else {
            boundingBoxes.add(getFromPoint(location,0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F));
        }

        return boundingBoxes;
    }

    public abstract boolean canConnect(final Block block);
}
