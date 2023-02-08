package ac.artemis.core.v5.emulator.block;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.impl.*;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;

/**
 * @author Ghast
 * @since 16/02/2021
 * Artemis © 2021
 */
public class BlockFactory {

    public static ac.artemis.core.v5.emulator.block.Block getBlock(final Material mat, EnumFacing direction, final int blockData, final NaivePoint block, final Point look) {
        final NaivePoint loc = new NaivePoint(block.getX(), block.getY(), block.getZ());
        final NMSMaterial material = NMSMaterial.matchNMSMaterial(mat);

        if (direction == null) {
            direction = EnumFacing.UP;
        }

        switch (material) {
            default: {
                if (mat.isBlock() && mat.isOccluding())
                    return new BlockFull(material, loc, direction);

                return new BlockAir(loc, direction);//new BlockAir(block.getFace())
            }

            case ENCHANTING_TABLE:
                return new BlockEnchantTable(loc, direction);

            case STICKY_PISTON:
            case PISTON: {
                final BlockPistonBase pistonBase = new BlockPistonBase(loc, direction);
                if (blockData >= 0) {
                    pistonBase.readData(blockData);
                }

                return pistonBase;
            }

            case PISTON_HEAD: {
                final BlockPistonHead pistonHead = new BlockPistonHead(loc, direction);

                if (blockData >= 0) {
                    pistonHead.readData(blockData);
                }

                return pistonHead;
            }

            case END_PORTAL_FRAME:
                final BlockEnderFrame frame = new BlockEnderFrame(loc, direction);

                if (blockData >= 0) {
                    frame.readData(blockData);
                }
                return frame;

            case WHITE_STAINED_GLASS_PANE:
            case BLACK_STAINED_GLASS_PANE:
            case GRAY_STAINED_GLASS_PANE:
            case BLUE_STAINED_GLASS_PANE:
            case BROWN_STAINED_GLASS_PANE:
            case GREEN_STAINED_GLASS_PANE:
            case CYAN_STAINED_GLASS_PANE:
            case LIGHT_BLUE_STAINED_GLASS_PANE:
            case LIGHT_GRAY_STAINED_GLASS_PANE:
            case PINK_STAINED_GLASS_PANE:
            case LIME_STAINED_GLASS_PANE:
            case MAGENTA_STAINED_GLASS_PANE:
            case PURPLE_STAINED_GLASS_PANE:
            case ORANGE_STAINED_GLASS_PANE:
            case RED_STAINED_GLASS_PANE:
            case YELLOW_STAINED_GLASS_PANE:
            case GLASS_PANE:
                return new BlockGlassPane(loc, direction);

            case IRON_BARS:
                return new BlockIronBars(loc, direction);

            // Ice
            case ICE:
            case BLUE_ICE:
            case FROSTED_ICE:
            case PACKED_ICE:

            // Glass
            case GLASS:
            case GRAY_STAINED_GLASS:
            case BLACK_STAINED_GLASS:
            case GREEN_STAINED_GLASS:
            case BLUE_STAINED_GLASS:
            case BROWN_STAINED_GLASS:
            case CYAN_STAINED_GLASS:
            case LIGHT_BLUE_STAINED_GLASS:
            case LIGHT_GRAY_STAINED_GLASS:
            case LIME_STAINED_GLASS:
            case MAGENTA_STAINED_GLASS:
            case ORANGE_STAINED_GLASS:
            case PINK_STAINED_GLASS:
            case PURPLE_STAINED_GLASS:
            case RED_STAINED_GLASS:
            case WHITE_STAINED_GLASS:
            case YELLOW_STAINED_GLASS:

            // Misc
            case BEACON:
            case NOTE_BLOCK:

            // Ores
            case REDSTONE_BLOCK:
            case DIAMOND_BLOCK:
            case IRON_BLOCK:
            case EMERALD_BLOCK:
            case GOLD_BLOCK:

            // Stones
            case GLOWSTONE:
            case STONE:
            case STONE_BRICKS:
            case SMOOTH_STONE:

            // Sandstone
            case SANDSTONE:
            case CHISELED_RED_SANDSTONE:
            case SMOOTH_RED_SANDSTONE:
            case SMOOTH_SANDSTONE:
            case CUT_SANDSTONE:
            case CHISELED_SANDSTONE:
            case CUT_RED_SANDSTONE:
            case RED_SANDSTONE:

            // Clay
            case CLAY:
            case TERRACOTTA:
            case BROWN_TERRACOTTA:
            case BLACK_GLAZED_TERRACOTTA:
            case BLACK_TERRACOTTA:
            case BLUE_GLAZED_TERRACOTTA:
            case BLUE_TERRACOTTA:
            case BROWN_GLAZED_TERRACOTTA:
            case CYAN_GLAZED_TERRACOTTA:
            case CYAN_TERRACOTTA:
            case GRAY_GLAZED_TERRACOTTA:
            case GRAY_TERRACOTTA:
            case GREEN_GLAZED_TERRACOTTA:
            case GREEN_TERRACOTTA:
            case LIGHT_BLUE_GLAZED_TERRACOTTA:
            case LIGHT_BLUE_TERRACOTTA:
            case LIGHT_GRAY_GLAZED_TERRACOTTA:
            case LIGHT_GRAY_TERRACOTTA:
            case LIME_GLAZED_TERRACOTTA:
            case LIME_TERRACOTTA:
            case MAGENTA_GLAZED_TERRACOTTA:
            case MAGENTA_TERRACOTTA:
            case ORANGE_GLAZED_TERRACOTTA:
            case ORANGE_TERRACOTTA:
            case PINK_GLAZED_TERRACOTTA:
            case PINK_TERRACOTTA:
            case PURPLE_GLAZED_TERRACOTTA:
            case PURPLE_TERRACOTTA:
            case RED_GLAZED_TERRACOTTA:
            case RED_TERRACOTTA:
            case WHITE_GLAZED_TERRACOTTA:
            case WHITE_TERRACOTTA:
            case YELLOW_GLAZED_TERRACOTTA:
            case YELLOW_TERRACOTTA:

            // Concrete
            case BLACK_CONCRETE:
            case CYAN_CONCRETE:
            case BLUE_CONCRETE:
            case BROWN_CONCRETE:
            case GRAY_CONCRETE:
            case GREEN_CONCRETE:
            case LIGHT_BLUE_CONCRETE:
            case LIGHT_GRAY_CONCRETE:
            case LIME_CONCRETE:
            case MAGENTA_CONCRETE:
            case ORANGE_CONCRETE:
            case PINK_CONCRETE:
            case PURPLE_CONCRETE:
            case RED_CONCRETE:
            case WHITE_CONCRETE:
            case YELLOW_CONCRETE:

            // Wood
            case BIRCH_WOOD:
            case ACACIA_WOOD:
            case DARK_OAK_WOOD:
            case JUNGLE_WOOD:
            case OAK_WOOD:
            case SPRUCE_WOOD:
            case STRIPPED_ACACIA_WOOD:
            case STRIPPED_BIRCH_WOOD:
            case STRIPPED_DARK_OAK_WOOD:
            case STRIPPED_JUNGLE_WOOD:
            case STRIPPED_OAK_WOOD:
            case STRIPPED_SPRUCE_WOOD:
                return new BlockFull(material, loc, direction);

            case ACACIA_SLAB:
            case ANDESITE_SLAB:
            case BIRCH_SLAB:
            case BLACKSTONE_SLAB:
            case BRICK_SLAB:
            case COBBLESTONE_SLAB:
            case SANDSTONE_SLAB:
            case CRIMSON_SLAB:
            case SMOOTH_QUARTZ_SLAB:
            case CUT_RED_SANDSTONE_SLAB:
            case CUT_SANDSTONE_SLAB:
            case DARK_OAK_SLAB:
            case DARK_PRISMARINE_SLAB:
            case DIORITE_SLAB:
            case END_STONE_BRICK_SLAB:
            case GRANITE_SLAB:
            case JUNGLE_SLAB:
            case SMOOTH_RED_SANDSTONE_SLAB:
            case MOSSY_COBBLESTONE_SLAB:
            case MOSSY_STONE_BRICK_SLAB:
            case SMOOTH_SANDSTONE_SLAB:
            case NETHER_BRICK_SLAB:
            case OAK_SLAB:
            case PETRIFIED_OAK_SLAB:
            case POLISHED_ANDESITE_SLAB:
            case POLISHED_BLACKSTONE_BRICK_SLAB:
            case POLISHED_BLACKSTONE_SLAB:
            case SMOOTH_STONE_SLAB:
            case POLISHED_DIORITE_SLAB:
            case POLISHED_GRANITE_SLAB:
            case SPRUCE_SLAB:
            case PRISMARINE_BRICK_SLAB:
            case PRISMARINE_SLAB:
            case PURPUR_SLAB:
            case QUARTZ_SLAB:
            case STONE_BRICK_SLAB:
            case RED_NETHER_BRICK_SLAB:
            case RED_SANDSTONE_SLAB:
            case STONE_SLAB:
            case WARPED_SLAB: {
                final BlockSlab slab = new BlockSlab(loc, direction);
                if (blockData >= 0) {
                    slab.readData(blockData);
                }
                return slab;
            }

            case DARK_OAK_DOUBLE_SLAB:
            case RED_SANDSTONE_DOUBLE_SLAB:
            case ACACIA_DOUBLE_SLAB:
            case BIRCH_DOUBLE_SLAB:
            case JUNGLE_DOUBLE_SLAB:
            case OAK_DOUBLE_SLAB:
            case SANDSTONE_DOUBLE_SLAB:
            case SPRUCE_DOUBLE_SLAB:
            case STONE_DOUBLE_BRICK_SLAB:
            case STONE_DOUBLE_SLAB: {
                final BlockSlab slab = new BlockSlab(loc, direction);
                slab.setDoubled(true);

                if (blockData >= 0) {
                    slab.readData(blockData);
                }

                return slab;
            }

            case CREEPER_HEAD:
            case CREEPER_WALL_HEAD:
            case PLAYER_HEAD:
            case PLAYER_WALL_HEAD:
            case ZOMBIE_HEAD:
            case ZOMBIE_WALL_HEAD:
            case SKELETON_SKULL:
            case SKELETON_WALL_SKULL:
            case WITHER_SKELETON_SKULL:
            case WITHER_SKELETON_WALL_SKULL: {
                final BlockSkull skull = new BlockSkull(loc, direction);

                if (blockData >= 0) {
                    skull.readData(blockData);
                }

                return skull;
            }

            case DAYLIGHT_DETECTOR: {
                final BlockDaylightDetector detector = new BlockDaylightDetector(loc, direction);

                if (blockData >= 0) {
                    detector.readData(blockData);
                }

                return detector;
            }

            case SNOW:
                final BlockSnow snow = new BlockSnow(loc, direction);
                if (blockData >= 0) {
                    snow.readData(blockData);
                }
                return snow;

            case CACTUS:
                return new BlockCactus(loc, direction);

            case ACACIA_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case JUNGLE_LEAVES:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
                return new BlockFull(loc, direction);

            case LILY_PAD:
                return new BlockLilyPad(loc, direction);

            case LADDER: {
                final BlockLadder ladder = new BlockLadder(loc, direction);
                if (blockData >= 0) {
                    ladder.setDirection(EnumFacing.getFront(blockData));
                }
                return ladder;
            }

            case VINE: {
                final BlockVine ladder = new BlockVine(loc, direction);
                if (blockData >= 0) {
                    ladder.setDirection(EnumFacing.getFront(blockData));
                }
                return ladder;
            }

            case WHITE_BED:
            case BLACK_BED:
            case BLUE_BED:
            case BROWN_BED:
            case CYAN_BED:
            case GRAY_BED:
            case LIGHT_BLUE_BED:
            case GREEN_BED:
            case LIGHT_GRAY_BED:
            case LIME_BED:
            case MAGENTA_BED:
            case ORANGE_BED:
            case PINK_BED:
            case PURPLE_BED:
            case RED_BED:
            case YELLOW_BED: {
                return new BlockBed(loc, direction);
            }

            case BREWING_STAND: {
                return new BlockBrewingStand(loc, direction);
            }

            case CAKE: {
                return new BlockCake(loc, direction);
            }

            case COBBLESTONE_WALL:
            case ANDESITE_WALL:
            case BLACKSTONE_WALL:
            case BRICK_WALL:
            case DIORITE_WALL:
            case END_STONE_BRICK_WALL:
            case GRANITE_WALL:
            case MOSSY_COBBLESTONE_WALL:
            case MOSSY_STONE_BRICK_WALL:
            case NETHER_BRICK_WALL:
            case POLISHED_BLACKSTONE_BRICK_WALL:
            case POLISHED_BLACKSTONE_WALL:
            case PRISMARINE_WALL:
            case RED_NETHER_BRICK_WALL:
            case RED_SANDSTONE_WALL:
            case SANDSTONE_WALL:
            case STONE_BRICK_WALL:
                return new BlockWall(loc, direction);


            case CYAN_CARPET:
            case BLACK_CARPET:
            case BLUE_CARPET:
            case BROWN_CARPET:
            case GRAY_CARPET:
            case GREEN_CARPET:
            case LIGHT_BLUE_CARPET:
            case LIGHT_GRAY_CARPET:
            case LIME_CARPET:
            case MAGENTA_CARPET:
            case ORANGE_CARPET:
            case PINK_CARPET:
            case PURPLE_CARPET:
            case RED_CARPET:
            case WHITE_CARPET:
            case YELLOW_CARPET: {
                return new BlockCarpet(loc, direction);
            }

            case CAULDRON: {
                return new BlockCauldron(loc, direction);
            }

            case CHEST: {
                return new BlockChest(loc, direction);
            }
            case TRAPPED_CHEST: {
                return new BlockChestTrapped(loc, direction);
            }
            case ENDER_CHEST: {
                return new BlockChestEnder(loc, direction);
            }

            case COCOA_BEANS:
            case COCOA: {
                final BlockCocoa cocoa = new BlockCocoa(loc, direction);
                if (blockData >= 0) {
                    cocoa.setDirection(EnumFacing.getFront(blockData));
                    cocoa.readData(blockData);
                }

                return cocoa;
            }

            /*
             * Trapdoors!
             */
            case ACACIA_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
            case IRON_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
            case CRIMSON_TRAPDOOR:
            case WARPED_TRAPDOOR:
            case OAK_TRAPDOOR: {
                if (blockData >= 0) {
                    switch (blockData & 3) {
                        case 0:
                            direction = EnumFacing.NORTH;
                            break;
                        case 1:
                            direction = EnumFacing.SOUTH;
                            break;
                        case 2:
                            direction = EnumFacing.WEST;
                            break;
                        case 3:
                        default:
                            direction = EnumFacing.EAST;
                            break;
                    }
                }
                final BlockTrapdoor trapdoor = new BlockTrapdoor(loc, direction);

                if (blockData >= 0)
                    trapdoor.readData(blockData);

                return trapdoor;
            }

            case DAMAGED_ANVIL:
            case CHIPPED_ANVIL:
            case ANVIL: {
                final BlockAnvil anvil = new BlockAnvil(loc, direction);

                if (blockData >= 0) {
                    anvil.setDirection(EnumFacing.getFront(blockData & 3));
                    anvil.readData(blockData);
                }

                return anvil;
            }

            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case CRIMSON_DOOR:
            case IRON_DOOR:
            case JUNGLE_DOOR:
            case OAK_DOOR:
            case SPRUCE_DOOR:
            case WARPED_DOOR:
            case DARK_OAK_DOOR:
                if (blockData >= 0) {
                    direction = EnumFacing.getFront(blockData & 3);
                }
                final BlockDoor door = new BlockDoor(loc, direction);

                if (blockData >= 0)
                    door.readData(blockData);

                return door;

            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case JUNGLE_FENCE:
            case CRIMSON_FENCE:
            case SPRUCE_FENCE:
            case NETHER_BRICK_FENCE:
            case OAK_FENCE:
            case WARPED_FENCE:
                return new BlockFence(loc, direction);

            case STONE_BRICK_STAIRS:
            case STONE_STAIRS:
            case SANDSTONE_STAIRS:
            case ACACIA_STAIRS:
            case ANDESITE_STAIRS:
            case BIRCH_STAIRS:
            case BLACKSTONE_STAIRS:
            case BRICK_STAIRS:
            case COBBLESTONE_STAIRS:
            case SMOOTH_QUARTZ_STAIRS:
            case CRIMSON_STAIRS:
            case SMOOTH_RED_SANDSTONE_STAIRS:
            case DARK_OAK_STAIRS:
            case DARK_PRISMARINE_STAIRS:
            case DIORITE_STAIRS:
            case END_STONE_BRICK_STAIRS:
            case SMOOTH_SANDSTONE_STAIRS:
            case GRANITE_STAIRS:
            case JUNGLE_STAIRS:
            case SPRUCE_STAIRS:
            case MOSSY_COBBLESTONE_STAIRS:
            case MOSSY_STONE_BRICK_STAIRS:
            case NETHER_BRICK_STAIRS:
            case OAK_STAIRS:
            case POLISHED_ANDESITE_STAIRS:
            case POLISHED_BLACKSTONE_BRICK_STAIRS:
            case POLISHED_BLACKSTONE_STAIRS:
            case POLISHED_DIORITE_STAIRS:
            case POLISHED_GRANITE_STAIRS:
            case PRISMARINE_BRICK_STAIRS:
            case PRISMARINE_STAIRS:
            case PURPUR_STAIRS:
            case QUARTZ_STAIRS:
            case RED_NETHER_BRICK_STAIRS:
            case RED_SANDSTONE_STAIRS:
            case WARPED_STAIRS:
                final BlockStairs blockStairs = new BlockStairs(loc, direction);
                if (blockData >= 0)
                    blockStairs.readData(blockData);

                if (look == null)
                    return blockStairs;

                final EnumFacing facing = blockStairs.getDirection();

                if (facing != EnumFacing.DOWN && (facing == EnumFacing.UP || look.getY() <= 0.5D)) {
                    blockStairs.setHalf(BlockStairs.EnumHalf.BOTTOM);
                } else {
                    blockStairs.setHalf(BlockStairs.EnumHalf.TOP);
                }

                return blockStairs;

            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case CRIMSON_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case WARPED_FENCE_GATE: {
                final BlockFenceGate gate =  new BlockFenceGate(loc, direction);

                if (blockData >= 0) gate.readData(blockData);

                return gate;
            }

            case SOUL_SAND:
                return new BlockSoulSand(loc, EnumFacing.NORTH);

            case SLIME_BALL:
            case SLIME_BLOCK:
                return new BlockSlime(loc, EnumFacing.NORTH);

            case COBWEB:
                return new BlockWeb(loc, EnumFacing.NORTH);

            case WATER:
                final BlockWater water = new BlockWater(loc, EnumFacing.NORTH);
                if (blockData >= 0)
                    water.readData(blockData);
                return water;

            case LAVA:
                final BlockLava lava = new BlockLava(loc, EnumFacing.NORTH);
                if (blockData >= 0)
                    lava.readData(blockData);
                return lava;

            case HOPPER:
                return new BlockHopper(loc, EnumFacing.NORTH);
        }

    }
}
