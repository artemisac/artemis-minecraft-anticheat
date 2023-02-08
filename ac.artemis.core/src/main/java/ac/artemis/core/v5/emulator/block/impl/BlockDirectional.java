package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

public abstract class BlockDirectional extends Block {
    public BlockDirectional(final NMSMaterial material, final NaivePoint location, final EnumFacing direction) {
        super(material, location, direction);
    }
}
