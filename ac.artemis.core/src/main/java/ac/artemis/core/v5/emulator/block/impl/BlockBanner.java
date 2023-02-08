package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

public class BlockBanner extends Block {
    public BlockBanner(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.WHITE_BANNER, location, direction);
    }

    @Override
    public boolean canCollide() {
        return false;
    }
}
