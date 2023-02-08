package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.Arrays;
import java.util.List;

public class BlockCauldron extends Block {

    public BlockCauldron(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.CAULDRON, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final float f = 0.125F;

        return Arrays.asList(
                getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F),
                getFromPoint(location, 0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F),
                getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f),
                getFromPoint(location, 1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F),
                getFromPoint(location, 0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F)
        );
    }

}
