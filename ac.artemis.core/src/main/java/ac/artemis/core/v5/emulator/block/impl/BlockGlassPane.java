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
public class BlockGlassPane extends BlockThin {
    public BlockGlassPane(NaivePoint location, EnumFacing direction) {
        super(NMSMaterial.GLASS_PANE, location, direction);
    }

    public BlockGlassPane(NMSMaterial material, NaivePoint location, EnumFacing direction) {
        super(material, location, direction);
    }

    @Override
    public boolean canConnect(final Block block) {
        return block instanceof BlockGlassPane;
    }
}
