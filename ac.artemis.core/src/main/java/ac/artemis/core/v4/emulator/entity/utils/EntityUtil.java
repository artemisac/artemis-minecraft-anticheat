package ac.artemis.core.v4.emulator.entity.utils;

import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.LivingEntity;
import ac.artemis.packet.minecraft.entity.Vehicle;
import ac.artemis.packet.minecraft.entity.impl.*;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.packet.minecraft.world.World;
import ac.artemis.core.v4.utils.blocks.BlockUtil;
import ac.artemis.core.v5.utils.bounding.BlockPos;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;

public class EntityUtil {
    public static boolean canAttack(Entity entity) {
        if (entity == null) return false;
        switch (entity.getType()) {
            case ENDER_SIGNAL:
            case ENDER_PEARL:
            case EXPERIENCE_ORB:
            case THROWN_EXP_BOTTLE:
            case DROPPED_ITEM:
            case ARROW:
            case FIREWORK:
                return false;
            default:
                return true;
        }
    }

    public static boolean canDamage(Entity entity) {
        if (entity.isDead()) return false;
        return entity instanceof Player;
    }

    public static boolean isNoClip(Entity entity) {
        return !canAttack(entity) || entity.isDead();
    }

    public static boolean canBePushed(Entity entity) {
        if (entity instanceof Minecart || entity instanceof Boat) return true;
        if (entity instanceof ArmorStand || entity instanceof Firework || entity instanceof Bat) return false;
        if (entity instanceof Horse) return ((Vehicle) entity).getPassenger() == null;
        if (entity instanceof LivingEntity) return !entity.isDead();
        return false;
    }

    public static boolean isAreaLoaded(World world, NaivePoint min, NaivePoint max){
        return isAreaLoaded(world, min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    public static boolean isAreaLoaded(World world, int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd) {
        if (yEnd >= 0 && yStart < 256) {
            xStart = xStart >> 4;
            zStart = zStart >> 4;
            xEnd = xEnd >> 4;
            zEnd = zEnd >> 4;

            for (int i = xStart; i <= xEnd; ++i) {
                for (int j = zStart; j <= zEnd; ++j) {
                    if (!isChunkLoaded(world, i, j)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static Point getFlowVector(World worldIn, BlockPos pos) {
        Point vec3 = new Point(0.0D, 0.0D, 0.0D);
        int i = getEffectiveFlowDecay(worldIn, pos);

        for (Object enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos blockpos = pos.offset((EnumFacing) enumfacing);
            int j = getEffectiveFlowDecay(worldIn, blockpos);

            if (j < 0) {
                Block block = BlockUtil.getBlockAsync(worldIn, blockpos.getX(), blockpos.getY(), blockpos.getZ());

                if (block != null){
                    if (!block.getType().isSolid()) {
                        j = getEffectiveFlowDecay(worldIn, blockpos.down());

                        if (j >= 0)
                        {
                            int k = j - (i - 8);
                            vec3 = vec3.addVector((double)((blockpos.getX() - pos.getX()) * k), (double)((blockpos.getY() - pos.getY()) * k), (double)((blockpos.getZ() - pos.getZ()) * k));
                        }
                    }
                }

            }
            else {
                int l = j - i;
                vec3 = vec3.addVector((double)((blockpos.getX() - pos.getX()) * l), (double)((blockpos.getY() - pos.getY()) * l), (double)((blockpos.getZ() - pos.getZ()) * l));
            }
        }

        if (getEffectiveFlowDecay(worldIn, pos) >= 8)
        {
            for (Object enumfacing1 : EnumFacing.Plane.HORIZONTAL)
            {
                BlockPos blockpos1 = pos.offset((EnumFacing) enumfacing1);

                if (isBlockSolid(worldIn, blockpos1, (EnumFacing) enumfacing1) || isBlockSolid(worldIn, blockpos1.up(), (EnumFacing) enumfacing1))
                {
                    vec3 = vec3.normalize().addVector(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }

        return vec3.normalize();
    }

    /**
     * Returns the percentage of the liquid block that is air, based on the given flow decay of the liquid
     */
    public static float getLiquidHeightPercent(int meta) {
        if (meta >= 8) {
            meta = 0;
        }

        return (float)(meta + 1) / 9.0F;
    }

    protected static int getEffectiveFlowDecay(World worldIn, BlockPos blockPos) {
        Block block = BlockUtil.getBlockAsync(worldIn, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (block == null || !block.isLiquid()) return -1;
        int i = block.getData();
        return i >= 8 ? 0 : i;
    }

    protected static boolean isChunkLoaded(World world, int x, int z) {
        return world.isChunkLoaded(x, z);
    }

    /**
     * Whether this Block is solid on the given Side
     */
    public static boolean isBlockSolid(World worldIn, BlockPos block, EnumFacing side) {
        Block block1 = BlockUtil.getBlockAsync(worldIn, block.getX(), block.getY(), block.getZ());
        if (block1 == null) return false;
        Material material = block1.getType();
        return material != block1.getType() && (side == EnumFacing.UP || (material != NMSMaterial.ICE.getMaterial() && material.isSolid()));
    }


    public static EnumCreatureAttribute getCreatureAttribute(Entity entity) {
        if (entity instanceof Wither || entity instanceof Skeleton || entity instanceof Zombie) {
            return EnumCreatureAttribute.UNDEAD;
        } else if (entity instanceof Spider || entity instanceof Endermite
                || entity instanceof Silverfish){
            return EnumCreatureAttribute.ARTHROPOD;
        }

        return EnumCreatureAttribute.UNDEFINED;
    }

    public enum EnumCreatureAttribute {
        UNDEFINED,
        UNDEAD,
        ARTHROPOD;
    }
}
