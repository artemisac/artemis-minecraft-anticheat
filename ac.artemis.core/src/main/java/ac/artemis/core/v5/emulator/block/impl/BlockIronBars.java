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
public class BlockIronBars extends BlockThin {
    public BlockIronBars(NaivePoint location, EnumFacing direction) {
        super(NMSMaterial.IRON_BARS, location, direction);
    }

    public BlockIronBars(NMSMaterial material, NaivePoint location, EnumFacing direction) {
        super(material, location, direction);
    }

    @Override
    public boolean canConnect(final Block block) {
        return block instanceof BlockIronBars;
    }
}
