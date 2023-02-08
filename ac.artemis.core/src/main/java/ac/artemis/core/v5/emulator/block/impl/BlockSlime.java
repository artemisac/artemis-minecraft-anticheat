package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.block.CollisionBlock;
import ac.artemis.core.v5.emulator.tags.Tags;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

/**
 * @author Ghast
 * @since 16/02/2021
 * Artemis © 2021
 */
public class BlockSlime extends BlockFull implements CollisionBlock {

    public BlockSlime(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.SLIME_BLOCK, location, direction);
    }

    @Override
    public void onLanded(final TransitionData emulator) {
        if (emulator.isSneaking()) {
            super.onLanded(emulator);
        } else if (emulator.getMotionY() < 0.0D) {
            emulator.addTag(Tags.SLIME_JUMP);
            emulator.setMotionY(-emulator.getMotionY());
        }
    }

    @Override
    public void onCollidedBlock(final TransitionData emulator) {
        if (Math.abs(emulator.getMotionY()) < 0.1D && !emulator.isSneaking()) {
            final double multiplier = 0.4D + Math.abs(emulator.getMotionY()) * 0.2D;

            emulator.addTag(Tags.SLIME_SLOW);
            emulator.setMotionX(emulator.getMotionX() * multiplier);
            emulator.setMotionZ(emulator.getMotionZ() * multiplier);
        }
    }
}
