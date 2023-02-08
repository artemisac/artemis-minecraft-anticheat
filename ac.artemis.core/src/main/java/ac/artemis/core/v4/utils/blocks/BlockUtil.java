package ac.artemis.core.v4.utils.blocks;

import ac.artemis.packet.minecraft.Minecraft;
import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;
import ac.artemis.core.v4.utils.position.SimplePosition;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.utils.minecraft.MathHelper;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import lombok.Getter;

import java.util.*;

/**
 * @author ToonBasic
 * at 06/06/2018
 * @implNote Modified by Ghast on 14.01.2020
 */
public class BlockUtil {
    public final static List<NMSMaterial> PHASE_COLLECTION = new ArrayList<>();

    private static Set<Byte> blockSolidPassSet;
    private static Set<Byte> blockStairsSet;
    @Getter
    private static Set<Byte> blockLiquidsSet;
    private static Set<Byte> blockWebsSet;
    @Getter
    private static Set<Byte> blockIceSet;
    private static Set<Byte> blockCarpetSet;
    public final static List<NMSMaterial> LIQUID_COLLECTION = new ArrayList<>();
    public final static List<NMSMaterial> FRICTION_COLLECTION = new ArrayList<>();
    public final static List<NMSMaterial> STAIRS_COLLECTION = new ArrayList<>();
    private static Set<NMSMaterial> blockInvalid;

    static {
        BlockUtil.blockSolidPassSet = new HashSet<>();
        BlockUtil.blockStairsSet = new HashSet<>();
        BlockUtil.blockLiquidsSet = new HashSet<>();
        BlockUtil.blockWebsSet = new HashSet<>();
        BlockUtil.blockIceSet = new HashSet<>();
        BlockUtil.blockCarpetSet = new HashSet<>();
        BlockUtil.blockInvalid = new HashSet<>();
        BlockUtil.blockSolidPassSet.addAll(Arrays.asList(new Byte[]{0, 6, 8, 9, 10, 11, 27, 28, 30, 31, 32, 37, 38,
                39, 40, 50, 51, 55, 59, 63, 66, 68, 69, 70, 72, 75, 76, 77, 78, 83, 90, 104, 105, 115, 119, -124, -113, -81}));
        BlockUtil.blockStairsSet.addAll(Arrays.asList(new Byte[]{53, 67, 108, 109, 114, -128, -122, -121, -120,
                -100, -93, -93, -76, 126, -74, 44, 78, 99, -112, -115, -116, -105, -108, 100}));
        BlockUtil.blockLiquidsSet.addAll(Arrays.asList(new Byte[]{8, 9, 10, 11}));
        BlockUtil.blockWebsSet.add((byte) 30);
        BlockUtil.blockIceSet.add((byte) 79);
        BlockUtil.blockIceSet.add((byte) (-82));
        BlockUtil.blockCarpetSet.add((byte) (-85));

        PHASE_COLLECTION.addAll(Arrays.asList(

                // Redstone related
                NMSMaterial.ACTIVATOR_RAIL,
                NMSMaterial.COMPARATOR,
                NMSMaterial.REPEATER,
                NMSMaterial.DETECTOR_RAIL,
                NMSMaterial.LARGE_FERN,
                NMSMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE,
                NMSMaterial.STONE_PRESSURE_PLATE,
                NMSMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE,
                NMSMaterial.LEVER,
                NMSMaterial.RAIL,
                NMSMaterial.REDSTONE_LAMP,
                NMSMaterial.REDSTONE_TORCH,
                NMSMaterial.REDSTONE_WIRE,
                NMSMaterial.TRIPWIRE,
                NMSMaterial.TRIPWIRE_HOOK,
                NMSMaterial.CHEST,
                NMSMaterial.TRAPPED_CHEST,
                NMSMaterial.PISTON,
                NMSMaterial.MOVING_PISTON,
                NMSMaterial.PISTON_HEAD,
                NMSMaterial.STICKY_PISTON,
                NMSMaterial.DAYLIGHT_DETECTOR,

                NMSMaterial.OAK_TRAPDOOR,
                NMSMaterial.ACACIA_TRAPDOOR,
                NMSMaterial.BIRCH_TRAPDOOR,
                NMSMaterial.DARK_OAK_TRAPDOOR,
                NMSMaterial.JUNGLE_TRAPDOOR,

                // Misc related
                NMSMaterial.ANVIL,
                NMSMaterial.BREWING_STAND,
                NMSMaterial.CAKE,
                NMSMaterial.CAULDRON,
                NMSMaterial.DEAD_BUSH,
                NMSMaterial.DRAGON_EGG,
                NMSMaterial.PAINTING,
                NMSMaterial.FLOWER_POT,
                NMSMaterial.HOPPER,
                NMSMaterial.ITEM_FRAME,
                NMSMaterial.JUKEBOX,
                NMSMaterial.LADDER,
                NMSMaterial.OAK_SIGN,
                NMSMaterial.OAK_WALL_SIGN,
                NMSMaterial.SKELETON_SKULL,
                NMSMaterial.ENCHANTING_TABLE,
                NMSMaterial.TORCH,
                NMSMaterial.VINE,
                NMSMaterial.LILY_PAD,
                NMSMaterial.COBWEB,
                NMSMaterial.SNOW,
                NMSMaterial.ENDER_CHEST,
                NMSMaterial.END_PORTAL,

                // Signs
                NMSMaterial.OAK_SIGN,
                NMSMaterial.OAK_WALL_SIGN,

                // Beds

                NMSMaterial.WHITE_BED,
                NMSMaterial.BLACK_BED,
                NMSMaterial.BLUE_BED,
                NMSMaterial.BROWN_BED,
                NMSMaterial.CYAN_BED,
                NMSMaterial.GRAY_BED,
                NMSMaterial.GREEN_BED,
                NMSMaterial.LIGHT_BLUE_BED,
                NMSMaterial.LIGHT_GRAY_BED,
                NMSMaterial.MAGENTA_BED,
                NMSMaterial.ORANGE_BED,
                NMSMaterial.PINK_BED,
                NMSMaterial.LIME_BED,
                NMSMaterial.PURPLE_BED,
                NMSMaterial.RED_BED,
                NMSMaterial.YELLOW_BED,

                // Crops / Food
                NMSMaterial.POTATOES,
                NMSMaterial.BAKED_POTATO,
                NMSMaterial.CARROT,
                NMSMaterial.BROWN_MUSHROOM,
                NMSMaterial.RED_MUSHROOM,
                NMSMaterial.BROWN_MUSHROOM_BLOCK,
                NMSMaterial.RED_MUSHROOM_BLOCK,
                NMSMaterial.NETHER_WART,
                NMSMaterial.MELON_STEM,
                NMSMaterial.PUMPKIN_STEM,
                NMSMaterial.ROSE_BUSH,
                NMSMaterial.OAK_SAPLING,
                NMSMaterial.WHEAT_SEEDS,
                NMSMaterial.SUGAR_CANE,
                NMSMaterial.CACTUS,

                // Stairs
                NMSMaterial.OAK_STAIRS,
                NMSMaterial.BIRCH_STAIRS,
                NMSMaterial.BRICK_STAIRS,
                NMSMaterial.ACACIA_STAIRS,
                NMSMaterial.ANDESITE_STAIRS,
                NMSMaterial.COBBLESTONE_STAIRS,
                NMSMaterial.DARK_OAK_STAIRS,
                NMSMaterial.JUNGLE_STAIRS,
                NMSMaterial.QUARTZ_STAIRS,
                NMSMaterial.SMOOTH_SANDSTONE_STAIRS,
                NMSMaterial.NETHER_BRICK_STAIRS,
                NMSMaterial.SPRUCE_STAIRS,
                NMSMaterial.SANDSTONE_STAIRS,
                NMSMaterial.PURPUR_STAIRS,

                // Walls
                NMSMaterial.COBBLESTONE_WALL,
                NMSMaterial.ANDESITE_WALL,

                // Slabs
                NMSMaterial.SMOOTH_STONE_SLAB,
                NMSMaterial.BRICK_SLAB,
                NMSMaterial.OAK_SLAB,
                NMSMaterial.ACACIA_SLAB,
                NMSMaterial.ANDESITE_SLAB,
                NMSMaterial.BIRCH_SLAB,
                NMSMaterial.COBBLESTONE_SLAB,
                NMSMaterial.CUT_RED_SANDSTONE_SLAB,
                NMSMaterial.CUT_SANDSTONE_SLAB,
                NMSMaterial.DARK_OAK_SLAB,
                NMSMaterial.DARK_PRISMARINE_SLAB,
                NMSMaterial.DIORITE_SLAB,
                NMSMaterial.END_STONE_BRICK_SLAB,
                NMSMaterial.GRANITE_SLAB,
                NMSMaterial.JUNGLE_SLAB,
                NMSMaterial.MOSSY_COBBLESTONE_SLAB,
                NMSMaterial.MOSSY_STONE_BRICK_SLAB,
                NMSMaterial.NETHER_BRICK_SLAB,
                NMSMaterial.OAK_SLAB,
                NMSMaterial.PETRIFIED_OAK_SLAB,
                NMSMaterial.POLISHED_ANDESITE_SLAB,
                NMSMaterial.POLISHED_DIORITE_SLAB,
                NMSMaterial.POLISHED_GRANITE_SLAB,
                NMSMaterial.PRISMARINE_BRICK_SLAB,
                NMSMaterial.PRISMARINE_SLAB,
                NMSMaterial.PURPUR_SLAB,
                NMSMaterial.QUARTZ_SLAB,
                NMSMaterial.RED_NETHER_BRICK_SLAB,
                NMSMaterial.RED_SANDSTONE_SLAB,
                NMSMaterial.SANDSTONE_SLAB,
                NMSMaterial.SMOOTH_QUARTZ_SLAB,
                NMSMaterial.SMOOTH_RED_SANDSTONE_SLAB,
                NMSMaterial.SMOOTH_SANDSTONE_SLAB,
                NMSMaterial.SMOOTH_STONE_SLAB,
                NMSMaterial.SPRUCE_SLAB,
                NMSMaterial.STONE_BRICK_SLAB,
                NMSMaterial.STONE_SLAB,

                // Fence
                NMSMaterial.OAK_FENCE,
                NMSMaterial.ACACIA_FENCE,
                NMSMaterial.BIRCH_FENCE,
                NMSMaterial.DARK_OAK_FENCE,
                NMSMaterial.JUNGLE_FENCE,
                NMSMaterial.CRIMSON_FENCE,
                NMSMaterial.SPRUCE_FENCE,
                NMSMaterial.WARPED_FENCE,
                NMSMaterial.NETHER_BRICK_FENCE,

                // Fence gate
                NMSMaterial.OAK_FENCE_GATE,
                NMSMaterial.ACACIA_FENCE_GATE,
                NMSMaterial.BIRCH_FENCE_GATE,
                NMSMaterial.DARK_OAK_FENCE_GATE,
                NMSMaterial.JUNGLE_FENCE_GATE,
                NMSMaterial.CRIMSON_FENCE_GATE,
                NMSMaterial.SPRUCE_FENCE_GATE,
                NMSMaterial.WARPED_FENCE_GATE,

                // Glass
                NMSMaterial.WHITE_STAINED_GLASS_PANE,
                NMSMaterial.BLACK_STAINED_GLASS_PANE,
                NMSMaterial.GRAY_STAINED_GLASS_PANE,
                NMSMaterial.BLUE_STAINED_GLASS_PANE,
                NMSMaterial.BROWN_STAINED_GLASS_PANE,
                NMSMaterial.GREEN_STAINED_GLASS_PANE,
                NMSMaterial.GLASS_PANE,

                // Door
                NMSMaterial.OAK_DOOR,
                NMSMaterial.IRON_DOOR,

                // Trapdoor
                NMSMaterial.OAK_TRAPDOOR,
                NMSMaterial.IRON_TRAPDOOR,

                // Boat
                NMSMaterial.OAK_BOAT,

                // Carpet
                NMSMaterial.WHITE_CARPET,


                // BlockFactory
                NMSMaterial.GRASS_BLOCK,
                NMSMaterial.SOUL_SAND,

                // Liquid
                NMSMaterial.LAVA,
                NMSMaterial.WATER
        ));

        LIQUID_COLLECTION.addAll(Arrays.asList(
                NMSMaterial.WATER,
                NMSMaterial.LAVA
        ));

        FRICTION_COLLECTION.addAll(Arrays.asList(
                NMSMaterial.SLIME_BLOCK,
                NMSMaterial.ICE,
                NMSMaterial.BLUE_ICE,
                NMSMaterial.FROSTED_ICE,
                NMSMaterial.PACKED_ICE
        ));
    }

    public static List<Material> getVerticalBlocks(SimplePosition from, SimplePosition to, World world) {
        List<Material> vB = new ArrayList<>();
        if (!BlockUtil.isLoaded(world, MathHelper.floor_double(from.getX()), MathHelper.floor_double(from.getZ())))
            return vB;
        for (double y = from.getY(); y <= to.getY(); y++) {
            vB.add(Minecraft.v().createLocation(world, from.getX(), y, from.getZ()).getBlock().getType());
        }
        return vB;
    }

    public static boolean isBlockingVelocity(Location loc) {
        for (double x = loc.getX() - 1; x <= loc.getX() + 1; x++) {
            for (double y = loc.getY(); y <= loc.getY() + 2; y++) {
                for (double z = loc.getZ() - 1; z <= loc.getZ() + 1; z++) {
                    Location block = Minecraft.v().createLocation(loc.getWorld(), x, y, z);
                    if (!String.format("%s", getBlockAsync(block))
                            .contains("AIR")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isPhaseSimple(NMSMaterial material) {
        return PHASE_COLLECTION.contains(material);
    }

    public static Block getBlockAsync(Location loc) {
        if (loc.getWorld() == null)
            return null;
        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4))
            return null;
        return loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static boolean isLoaded(final World world, final int x, final int z) {
        return world.isChunkLoaded(x >> 4, z >> 4);
    }

    public static Block getBlockAsync(World world, int x, int y, int z) {
        if (!world.isChunkLoaded(x >> 4, z >> 4)) return null;
        return world.getBlockAt(x, y, z);
    }

    public static Block getBlockAsync(World world, NaivePoint vector) {
        return getBlockAsync(world, vector.getX(), vector.getY(), vector.getZ());
    }


    public static float getSlipperiness(Location location) {
        return getSlipperiness(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static float getSlipperiness(World world, final int x, final int y, final int z) {
        final Block block = getBlockAsync(world, x, y, z);
        if (block == null) return 0.6F;
        return getSlipperiness(block);
    }

    public static float getSlipperiness(Block block) {
        return getSlipperiness(NMSMaterial.matchNMSMaterial(block.getType()));
    }

    @Deprecated
    public static float getSlipperiness(Material block) {
        return getSlipperiness(NMSMaterial.matchNMSMaterial(block));
    }

    public static float getSlipperiness(NMSMaterial block) {
        switch (block) {
            case SLIME_BLOCK:
                return 0.8F;
            case ICE:
            case PACKED_ICE:
                return 0.98F;
            default:
                return 0.6F;
        }
    }
}
