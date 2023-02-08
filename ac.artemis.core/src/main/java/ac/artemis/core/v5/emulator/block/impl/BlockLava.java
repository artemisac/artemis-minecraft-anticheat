package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.block.CollisionLiquid;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;
import lombok.Getter;

/**
 * @author Ghast
 * @since 16/02/2021
 * Artemis © 2021
 */

@Getter
public class BlockLava extends ac.artemis.core.v5.emulator.block.impl.BlockLiquid implements CollisionLiquid {

    private int height;

    public BlockLava(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.LAVA, location, direction);
    }

    @Override
    public void readData(final int data) {
        this.height = data;
    }

    @Override
    public Point modifyAcceleration(final TransitionData emulator, final NaivePoint blockPos, final Point motion) {
        return motion;
    }
}
