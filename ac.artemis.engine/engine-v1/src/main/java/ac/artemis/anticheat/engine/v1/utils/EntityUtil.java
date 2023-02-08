package ac.artemis.anticheat.engine.v1.utils;

import ac.artemis.anticheat.engine.v1.Bntity;
import ac.artemis.core.v4.emulator.moderna.ModernaMathHelper;
import ac.artemis.core.v5.emulator.block.impl.BlockWater;
import ac.artemis.core.v5.utils.bounding.BlockPos;
import ac.artemis.anticheat.engine.v1.block.WrappedBlock;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.utils.blocks.BlockUtil;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.MutableNaivePoint;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.LivingEntity;
import ac.artemis.packet.minecraft.entity.Vehicle;
import ac.artemis.packet.minecraft.entity.impl.*;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.world.World;

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

    /**
     * handles the acceleration of an object whilst in water. Not sure if it is used elsewhere.
     */
    public static boolean handleMaterialAcceleration(World world, BoundingBox bb, NMSMaterial materialIn, Bntity entityIn) {
        int minX = ModernaMathHelper.floor(bb.minX);
        int minY = ModernaMathHelper.floor(bb.minY);
        int minZ = ModernaMathHelper.floor(bb.minZ);
        int maxX = ModernaMathHelper.floor(bb.maxX + 1.0D);
        int maxY = ModernaMathHelper.floor(bb.maxY + 1.0D);
        int maxZ = ModernaMathHelper.floor(bb.maxZ + 1.0D);

        if (!isAreaLoaded(world, minX, minY, minZ, maxX, maxY, maxZ)) {
            return false;
        } else {
            boolean flag = false;
            Point vec3 = new Point(0.0D, 0.0D, 0.0D);
            final MutableNaivePoint bp = new MutableNaivePoint(0,0,0);

            for (int x = minX; x < maxX; ++x) {
                for (int y = minY; y < maxY; ++y) {
                    for (int z = minZ; z < maxZ; ++z) {
                        bp.override(x, y, z);
                        final ac.artemis.core.v5.emulator.block.Block block = entityIn.getWorld().getBlockAt(bp.getX(), bp.getY(), bp.getZ());

                        if (block instanceof BlockWater) {
                            double d0 = (float)(y + 1) - ((BlockWater) block).getHeight();

                            if ((double)maxY >= d0) {
                                flag = true;
                                vec3 = new WrappedBlock(materialIn).modifyAcceleration(world, bp, entityIn, vec3);
                            }
                        }
                    }
                }
            }

            if (vec3.lengthVector() > 0.0D && entityIn.isPushedByWater()) {
                vec3 = vec3.normalize();
                double d1 = 0.014D;
                /*entityIn.motionX += vec3.getX() * d1;
                entityIn.motionY += vec3.getY() * d1;
                entityIn.motionZ += vec3.getZ() * d1;*/
            }

            return flag;
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
        } else if (entity instanceof Spider || entity instanceof Endermite || entity instanceof Silverfish){
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
