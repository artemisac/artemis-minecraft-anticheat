package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.block.CollisionLiquid;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import lombok.Getter;

/**
 * @author Ghast
 * @since 16/02/2021
 * Artemis © 2021
 */

@Getter
public class BlockWater extends ac.artemis.core.v5.emulator.block.impl.BlockLiquid implements CollisionLiquid {
    private int height;

    public BlockWater(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.WATER, location, direction);
    }

    @Override
    public void readData(final int data) {
        this.height = data;
    }

    @Override
    public Point modifyAcceleration(final TransitionData emulator, final NaivePoint blockPos, final Point motion) {
        return NMSManager.getInms().getModifiedAcceleration(emulator.getData().getPlayer().getWorld(),
                new BlockPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ()), emulator.getData().getPlayer(),
                motion
        );
    }
}
