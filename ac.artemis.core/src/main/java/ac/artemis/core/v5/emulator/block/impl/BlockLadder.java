package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.Collections;
import java.util.List;

public class BlockLadder extends Block {

    public BlockLadder(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.LADDER, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final BoundingBox boundingBox;

        final float f = 0.125F;

        switch (direction) {
            case NORTH:
                boundingBox = getFromPoint(location, 0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                break;

            case SOUTH:
                boundingBox = getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                break;

            case WEST:
                boundingBox = getFromPoint(location, 1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;

            case EAST:
            default:
                boundingBox = getFromPoint(location, 0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        }

        return Collections.singletonList(boundingBox);
    }


}
