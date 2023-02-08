package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * @author Ghast
 * @since 16/02/2021
 * Artemis © 2021
 */

@Getter
public class BlockSnow extends Block {

    private int height = 2;

    public BlockSnow(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.WATER, location, direction);
    }

    @Override
    public void readData(final int data) {
        this.height = (data & 7) + 1;
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final int high = height - 1;
        final float f = 0.125F;

        return Collections.singletonList(
                getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, (float) high * f, 1.0F)
        );
    }
}
