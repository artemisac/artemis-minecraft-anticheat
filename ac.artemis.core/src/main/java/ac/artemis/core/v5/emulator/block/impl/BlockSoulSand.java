package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.block.CollisionBlockState;
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
public class BlockSoulSand extends Block implements CollisionBlockState {
    public BlockSoulSand(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.SOUL_SAND, location, direction);
    }

    @Override
    public boolean canCollide() {
        return true;
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final float f = 0.125F;
        return Collections.singletonList(
                new BoundingBox(location.getX(), location.getY(), location.getZ(),
                        location.getX() + 1, (float) (location.getY() + 1) - f, location.getZ() + 1)
        );
    }

    @Override
    public void onCollidedBlockState(final TransitionData emulator) {
        emulator.setMotionX(emulator.getMotionX() * 0.4D);
        emulator.setMotionZ(emulator.getMotionZ() * 0.4D);
    }
}
