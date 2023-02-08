package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.Arrays;
import java.util.List;

public class BlockHopper extends Block {

    public BlockHopper(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.HOPPER, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final float f = 0.125F;

        return Arrays.asList(
                Block.getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F),
                Block.getFromPoint(location, 0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F),
                Block.getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f),
                Block.getFromPoint(location, 1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F),
                Block.getFromPoint(location, 0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F),
                Block.getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F)
        );
    }
}
