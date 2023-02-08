package ac.artemis.core.v5.utils.block;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.utils.ServerUtil;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BlockUtil {
    public Block getBlockAsync(final Location loc) {
        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) return null;
        return loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public Block getBlockAsync(final World world, final BlockPosition loc) {
        if (!world.isChunkLoaded(loc.getX() >> 4, loc.getZ() >> 4)) return null;
        return world.getBlockAt(loc.getX(), loc.getY(), loc.getZ());
    }

    public Block getBlockAsync(final World world, final int x, final int y, final int z) {
        if (!world.isChunkLoaded(x >> 4, z >> 4)) return null;
        return world.getBlockAt(x, y, z);
    }

    public boolean isLoaded(final World world, final int x, final int z) {
        return world.isChunkLoaded(x >> 4, z >> 4);
    }

    public boolean isLoadedShifted(final World world, final int x, final int z) {
        return world.isChunkLoaded(x, z);
    }

    public boolean isBarrier(final Block block) {
        return isBarrier(NMSMaterial.matchNMSMaterial(block.getType()));
    }

    public boolean isBarrier(final NMSMaterial block) {
        return !ServerUtil.getGameVersion().isBelow(ProtocolVersion.V1_8) && block.equals(NMSMaterial.BARRIER);
    }

    public boolean isFence(final Block block) {
        return isFence(NMSMaterial.matchNMSMaterial(block.getType()));
    }

    public boolean isFence(final NMSMaterial block) {
        switch (block) {
            default: return false;
            case OAK_FENCE:
            case CRIMSON_FENCE:
            case WARPED_FENCE:
            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case JUNGLE_FENCE:
            case SPRUCE_FENCE: return true;
        }
    }

    public boolean isWall(final Block block) {
        return isWall(NMSMaterial.matchNMSMaterial(block.getType()));
    }

    public boolean isWall(final NMSMaterial material) {
        switch (material) {
            default: return false;
            case ANDESITE_WALL:
            case BLACKSTONE_WALL:
            case BRICK_WALL:
            case DIORITE_WALL:
            case GRANITE_WALL:
            case END_STONE_BRICK_WALL:
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
            case COBBLESTONE_WALL: return true;
        }
    }

    public boolean isFenceGate(final Block block) {
        return isFenceGate(NMSMaterial.matchNMSMaterial(block.getType()));
    }

    public boolean isFenceGate(final NMSMaterial block) {
        switch (block) {
            default: return false;
            case CRIMSON_FENCE_GATE:
            case OAK_FENCE_GATE:
            case WARPED_FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SPRUCE_FENCE_GATE: return true;
        }
    }

    public boolean isLeaves(final NMSMaterial material) {
        switch (material) {
            case ACACIA_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case JUNGLE_LEAVES:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
                return true;
            default:
                return false;
        }
    }
}
