package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ghast
 * @since 19/02/2021
 * Artemis © 2021
 */
public class BlockPistonBase extends Block {
    public BlockPistonBase(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.PISTON, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final List<BoundingBox> boundingBoxes = new ArrayList<>();

        switch (direction) {
            case DOWN:
                boundingBoxes.add(getFromPoint(location, 0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F));
                break;

            case UP:
                boundingBoxes.add(getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F));
                break;

            case NORTH:
                boundingBoxes.add(getFromPoint(location, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F));
                break;

            case SOUTH:
                boundingBoxes.add(getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F));
                break;

            case WEST:
                boundingBoxes.add(getFromPoint(location, 0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F));
                break;

            case EAST:
                boundingBoxes.add(getFromPoint(location, 0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F));
                break;
        }

        return boundingBoxes;
    }

    @Override
    public void readData(final int data) {
        final int i = data & 7;

        if (i > 5) {
            return;
        }

        direction = EnumFacing.getFront(i);
    }
}
