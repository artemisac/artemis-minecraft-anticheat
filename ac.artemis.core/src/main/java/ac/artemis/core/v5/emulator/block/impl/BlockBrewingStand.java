package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockBrewingStand extends Block {
    public BlockBrewingStand(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.BREWING_STAND, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        return new ArrayList<>(Arrays.asList(
                getFromPoint(location, 0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F),
                getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F)
        ));
    }
}
