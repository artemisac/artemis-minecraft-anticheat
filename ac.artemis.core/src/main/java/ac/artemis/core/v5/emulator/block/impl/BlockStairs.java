package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import lombok.Getter;
import ac.artemis.packet.minecraft.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ghast
 * @since 16/02/2021
 * Artemis © 2021
 */
@Getter
public class BlockStairs extends BlockDirectional {

    private EnumHalf half;
    private EnumShape shape;

    public BlockStairs(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.COBBLESTONE_SLAB, location, direction);
    }

    public static boolean isStairs(final Block block) {
        return block instanceof BlockStairs;
    }

    @Override
    public boolean canCollide() {
        return true;
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final List<BoundingBox> boundingBoxes = new ArrayList<>();
        if (half == EnumHalf.TOP) {
            boundingBoxes.add(getFromPoint(location, 0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F));
        } else {
            boundingBoxes.add(getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F));
        }

        final EnumFacing enumfacing = this.getDirection();
        final boolean flag = half == EnumHalf.TOP;
        float f = 0.5F;
        float f1 = 1.0F;

        if (flag) {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.5F;
        boolean flag1 = true;

        final NaivePoint relativeEast = location.getRelative(BlockFace.EAST);
        final Block east = world.getBlockAt(relativeEast.getX(), relativeEast.getY(), relativeEast.getZ());

        final NaivePoint relativeWest = location.getRelative(BlockFace.WEST);
        final Block west = world.getBlockAt(relativeWest.getX(), relativeWest.getY(), relativeWest.getZ());

        final NaivePoint relativeSouth = location.getRelative(BlockFace.SOUTH);
        final Block south = world.getBlockAt(relativeSouth.getX(), relativeSouth.getY(), relativeSouth.getZ());

        final NaivePoint relativeNorth = location.getRelative(BlockFace.NORTH);
        final Block north = world.getBlockAt(relativeNorth.getX(), relativeNorth.getY(), relativeNorth.getZ());

        switch (enumfacing) {
            case EAST: {
                f2 = 0.5F;
                f5 = 1.0F;

                final boolean stairsFlag = isStairs(east) && ((BlockStairs) east).getHalf().equals(half);

                if (stairsFlag) {
                    final EnumFacing direction = east.getDirection();

                    if (direction == EnumFacing.NORTH && !isSameStair(south)) {
                        f5 = 0.5F;
                        flag1 = false;
                    } else if (direction == EnumFacing.SOUTH && !isSameStair(north)) {
                        f4 = 0.5F;
                        flag1 = false;
                    }
                }
                break;
            }

            case WEST: {
                f3 = 0.5F;
                f5 = 1.0F;

                final boolean stairsFlag = isStairs(west) && ((BlockStairs) west).getHalf().equals(half);
                if (stairsFlag) {
                    final EnumFacing direction = east.getDirection();

                    if (direction == EnumFacing.NORTH && !isSameStair(south)) {
                        f5 = 0.5F;
                        flag1 = false;
                    } else if (direction == EnumFacing.SOUTH && !isSameStair(north)) {
                        f4 = 0.5F;
                        flag1 = false;
                    }
                }
                break;
            }

            case SOUTH: {
                f4 = 0.5F;
                f5 = 1.0F;

                final boolean stairsFlag = isStairs(south) && ((BlockStairs) south).getHalf().equals(half);

                if (stairsFlag) {
                    final EnumFacing direction = south.getDirection();

                    if (direction == EnumFacing.WEST && !isSameStair(east)) {
                        f3 = 0.5F;
                        flag1 = false;
                    } else if (direction == EnumFacing.EAST && !isSameStair(west)) {
                        f2 = 0.5F;
                        flag1 = false;
                    }
                }
                break;
            }

            case NORTH: {
                final boolean stairsFlag = isStairs(north) && ((BlockStairs) north).getHalf().equals(half);

                if (stairsFlag) {
                    final EnumFacing direction = north.getDirection();

                    if (direction == EnumFacing.WEST && !isSameStair(east)) {
                        f3 = 0.5F;
                        flag1 = false;
                    } else if (direction == EnumFacing.EAST && !isSameStair(west)) {
                        f2 = 0.5F;
                        flag1 = false;
                    }
                }
                break;
            }
        }


        boundingBoxes.add(getFromPoint(location, f2, f, f4, f3, f1, f5));
        if (!flag1) {
            return boundingBoxes;
        }

        // Stairs stage 2
        f2 = 0.0F;
        f3 = 1.0F;
        f4 = 0.0F;
        f5 = 0.5F;
        flag1 = false;

        switch (enumfacing) {
            case EAST: {
                final boolean stairsFlag = isStairs(east) && ((BlockStairs) east).getHalf().equals(half);

                if (stairsFlag) {
                    final EnumFacing direction = east.getDirection();

                    if (direction == EnumFacing.NORTH && !isSameStair(south)) {
                        f4 = 0.0F;
                        f5 = 0.5F;
                        flag1 = true;
                    } else if (direction == EnumFacing.SOUTH && !isSameStair(north)) {
                        f4 = 0.5F;
                        f5 = 1.0F;
                        flag1 = true;
                    }
                }
                break;
            }

            case WEST: {
                final boolean stairsFlag = isStairs(west) && ((BlockStairs) west).getHalf().equals(half);
                if (stairsFlag) {
                    final EnumFacing direction = east.getDirection();

                    f2 = 0.5F;
                    f3 = 1.0F;

                    if (direction == EnumFacing.NORTH && !isSameStair(south)) {
                        f4 = 0.0F;
                        f5 = 0.5F;
                        flag1 = true;
                    } else if (direction == EnumFacing.SOUTH && !isSameStair(north)) {
                        f4 = 0.5F;
                        f5 = 1.0F;
                        flag1 = true;
                    }
                }
                break;
            }

            case SOUTH: {

                final boolean stairsFlag = isStairs(south) && ((BlockStairs) south).getHalf().equals(half);

                if (stairsFlag) {
                    f4 = 0.0F;
                    f5 = 0.5F;

                    final EnumFacing direction = south.getDirection();

                    if (direction == EnumFacing.WEST && !isSameStair(east)) {
                        flag1 = true;
                    } else if (direction == EnumFacing.EAST && !isSameStair(west)) {
                        f2 = 0.5F;
                        f3 = 1.0F;
                        flag1 = true;
                    }
                }
                break;
            }

            case NORTH: {
                final boolean stairsFlag = isStairs(north) && ((BlockStairs) north).getHalf().equals(half);

                if (stairsFlag) {
                    final EnumFacing direction = north.getDirection();

                    if (direction == EnumFacing.WEST && !isSameStair(east)) {
                        flag1 = true;
                    } else if (direction == EnumFacing.EAST && !isSameStair(west)) {
                        f2 = 0.5F;
                        f3 = 1.0F;
                        flag1 = true;
                    }
                }
                break;
            }
        }

        if (flag1) {
            boundingBoxes.add(getFromPoint(location, f2, f, f4, f3, f1, f5));
        }

        return boundingBoxes;
    }

    public boolean uglyMf1(final ArtemisWorld world, final List<BoundingBox> boundingBoxes) {
        final boolean flag = half == BlockStairs.EnumHalf.TOP;
        float f = 0.5F;
        float f1 = 1.0F;

        if (flag) {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        final float f3 = 1.0F;
        final float f4 = 0.0F;
        float f5 = 0.5F;
        final boolean flag1 = true;

        if (direction == EnumFacing.EAST) {
            f2 = 0.5F;
            f5 = 1.0F;
            final NaivePoint iblockstate1 = location.getRelative(BlockFace.EAST);
            final Block block = world.getBlockAt(iblockstate1.getX(), iblockstate1.getY(), iblockstate1.getZ());
        }
            /*if (isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.getValue(HALF)) {
                EnumFacing enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iblockstate)) {
                    f5 = 0.5F;
                    flag1 = false;
                } else if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iblockstate)) {
                    f4 = 0.5F;
                    flag1 = false;
                }
            }
        } else if (enumfacing == EnumFacing.WEST) {
            f3 = 0.5F;
            f5 = 1.0F;
            IBlockState iblockstate2 = blockAccess.getBlockState(pos.west());
            Block block1 = iblockstate2.getBlock();

            if (isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.getValue(HALF)) {
                EnumFacing enumfacing2 = (EnumFacing) iblockstate2.getValue(FACING);

                if (enumfacing2 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iblockstate)) {
                    f5 = 0.5F;
                    flag1 = false;
                } else if (enumfacing2 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iblockstate)) {
                    f4 = 0.5F;
                    flag1 = false;
                }
            }
        } else if (enumfacing == EnumFacing.SOUTH) {
            f4 = 0.5F;
            f5 = 1.0F;
            IBlockState iblockstate3 = blockAccess.getBlockState(pos.south());
            Block block2 = iblockstate3.getBlock();

            if (isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.getValue(HALF)) {
                EnumFacing enumfacing3 = (EnumFacing) iblockstate3.getValue(FACING);

                if (enumfacing3 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                    f3 = 0.5F;
                    flag1 = false;
                } else if (enumfacing3 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                    f2 = 0.5F;
                    flag1 = false;
                }
            }
        } else if (enumfacing == EnumFacing.NORTH) {
            IBlockState iblockstate4 = blockAccess.getBlockState(pos.north());
            Block block3 = iblockstate4.getBlock();

            if (isBlockStairs(block3) && blockstairs$enumhalf == iblockstate4.getValue(HALF)) {
                EnumFacing enumfacing4 = (EnumFacing) iblockstate4.getValue(FACING);

                if (enumfacing4 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                    f3 = 0.5F;
                    flag1 = false;
                } else if (enumfacing4 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                    f2 = 0.5F;
                    flag1 = false;
                }
            }
        }

        this.setBlockBounds(f2, f, f4, f3, f1, f5);*/
        return flag1;
    }

    @Override
    public void readData(final int data) {
        this.half = (data & 4) > 0 ? EnumHalf.TOP : EnumHalf.BOTTOM;
        this.direction = EnumFacing.getFront(5 - (data & 3));
    }

    public void setHalf(final EnumHalf half) {
        this.half = half;
    }

    public void setShape(final EnumShape shape) {
        this.shape = shape;
    }

    public boolean isSameStair(final Block block) {
        return isStairs(block) && ((BlockStairs) block).getHalf().equals(this.getHalf()) && block.getDirection().equals(this.getDirection());
    }

    public enum EnumHalf {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        EnumHalf(final String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum EnumShape {
        STRAIGHT("straight"),
        INNER_LEFT("inner_left"),
        INNER_RIGHT("inner_right"),
        OUTER_LEFT("outer_left"),
        OUTER_RIGHT("outer_right");

        private final String name;

        EnumShape(final String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }

}
