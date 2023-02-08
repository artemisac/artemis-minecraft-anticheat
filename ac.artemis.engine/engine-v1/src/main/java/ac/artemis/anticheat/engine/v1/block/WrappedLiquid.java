package ac.artemis.anticheat.engine.v1.block;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.anticheat.engine.v1.Bntity;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.packet.minecraft.world.World;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
public class WrappedLiquid extends WrappedBlock {
    public WrappedLiquid() {
        super(NMSMaterial.WATER);
    }

    @Override
    public Point modifyAcceleration(World world, NaivePoint blockPos, Bntity bntity, Point vector) {
        return NMSManager.getInms().getModifiedAcceleration(world, new BlockPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ()),
                bntity.getData().getPlayer(), vector);
    }
}
