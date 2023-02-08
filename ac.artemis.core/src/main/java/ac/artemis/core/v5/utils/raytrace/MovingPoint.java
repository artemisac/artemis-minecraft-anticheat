package ac.artemis.core.v5.utils.raytrace;

import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.core.v5.utils.bounding.BlockPos;
import ac.artemis.core.v5.utils.bounding.EnumFacing;

public class MovingPoint {

    private BlockPos blockPos;

    /** What type of ray trace hit was this? 0 = block, 1 = entity */
    public MovingPoint.MovingObjectType typeOfHit;
    public EnumFacing sideHit;

    /** The vector position of the hit */
    public Point hitVec;

    /** The hit entity */
    public Entity entityHit;

    public MovingPoint(final Point hitVecIn, final EnumFacing facing, final BlockPos blockPosIn)
    {
        this(MovingPoint.MovingObjectType.BLOCK, hitVecIn, facing, blockPosIn);
    }

    public MovingPoint(final Point p_i45552_1_, final EnumFacing facing)
    {
        this(MovingPoint.MovingObjectType.BLOCK, p_i45552_1_, facing, BlockPos.ORIGIN);
    }

    public MovingPoint(final Entity p_i2304_1_)
    {
        this(p_i2304_1_, new Point(p_i2304_1_.getLocation().getX(), p_i2304_1_.getLocation().getY(), p_i2304_1_.getLocation().getZ()));
    }

    public MovingPoint(final MovingPoint.MovingObjectType typeOfHitIn, final Point hitVecIn, final EnumFacing sideHitIn, final BlockPos blockPosIn)
    {
        this.typeOfHit = typeOfHitIn;
        this.blockPos = blockPosIn;
        this.sideHit = sideHitIn;
        this.hitVec = new Point(hitVecIn.getX(), hitVecIn.getY(), hitVecIn.getZ());
    }

    public MovingPoint(final Entity entityHitIn, final Point hitVecIn)
    {
        this.typeOfHit = MovingPoint.MovingObjectType.ENTITY;
        this.entityHit = entityHitIn;
        this.hitVec = hitVecIn;
    }

    public BlockPos getBlockPos()
    {
        return this.blockPos;
    }

    public String toString()
    {
        return "HitResult{type=" + this.typeOfHit + ", blockpos=" + this.blockPos + ", f=" + this.sideHit + ", pos=" + this.hitVec + ", entity=" + this.entityHit + '}';
    }

    public enum MovingObjectType
    {
        MISS,
        BLOCK,
        ENTITY
    }
}
