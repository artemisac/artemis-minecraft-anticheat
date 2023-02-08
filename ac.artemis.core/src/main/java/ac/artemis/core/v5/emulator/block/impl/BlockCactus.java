package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.Collections;
import java.util.List;

public class BlockCactus extends Block {
    public BlockCactus(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.CACTUS, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final float f = 0.0625F;
        return Collections.singletonList(
                new BoundingBox(
                        (float) location.getX() + f,
                        (float) location.getY(),
                        (float) location.getZ() + f,
                        (float) (location.getX() + 1) - f,
                        (float) (location.getY() + 1) - f,
                        (float) (location.getZ() + 1) - f
                )
        );
    }
}
