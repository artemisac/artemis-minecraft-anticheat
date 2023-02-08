package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.block.CollisionBlockState;
import ac.artemis.core.v5.emulator.attributes.EntityAttributes;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

/**
 * @author Ghast
 * @since 16/02/2021
 * Artemis © 2021
 */
public class BlockWeb extends Block implements CollisionBlockState {

    public BlockWeb(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.AIR, location, direction);
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    @Override
    public void onCollidedBlockState(final TransitionData emulator) {
        emulator.push(EntityAttributes.WEB, true);
    }
}
