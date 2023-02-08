package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

/**
 * @author Ghast
 * @since 16/02/2021
 * Artemis © 2021
 */
public abstract class BlockLiquid extends Block {
    public BlockLiquid(final NMSMaterial material, final NaivePoint location, final EnumFacing direction) {
        super(material, location, direction);
    }

    @Override
    public boolean canCollide() {
        return false;
    }
}
