package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.Collections;
import java.util.List;

/**
 * @author Ghast
 * @since 16/02/2021
 * Artemis © 2021
 */
public class BlockLilyPad extends Block {
    public BlockLilyPad(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.LILY_PAD, location, direction);
    }

    public BlockLilyPad(final NMSMaterial material, final NaivePoint location, final EnumFacing direction) {
        super(material, location, direction);
    }

    @Override
    public boolean canCollide() {
        return true;
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final float f = 0.5F;
        final float f1 = 0.015625F;
        return Collections.singletonList(
                getFromPoint(location, 0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f)
        );
    }
}
