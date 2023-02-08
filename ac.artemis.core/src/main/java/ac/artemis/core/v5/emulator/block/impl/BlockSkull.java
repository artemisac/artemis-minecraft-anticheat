package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.Collections;
import java.util.List;

public class BlockSkull extends Block {
    public BlockSkull(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.SKELETON_SKULL, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        switch (direction) {
            default:
                return Collections.singletonList(getFromPoint(location, 0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F));
            case NORTH:
                return Collections.singletonList(getFromPoint(location, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F));
            case SOUTH:
                return Collections.singletonList(getFromPoint(location, 0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F));
            case WEST:
                return Collections.singletonList(getFromPoint(location, 0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F));
            case EAST:
                return Collections.singletonList(getFromPoint(location, 0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F));
        }
    }

    @Override
    public void readData(final int data) {
        this.setDirection(EnumFacing.getFront(data & 7));
    }
}
