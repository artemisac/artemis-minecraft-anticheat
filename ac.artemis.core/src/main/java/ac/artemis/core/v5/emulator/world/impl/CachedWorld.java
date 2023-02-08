package ac.artemis.core.v5.emulator.world.impl;

import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.world.World;
import ac.artemis.core.v5.emulator.block.impl.BlockAir;
import ac.artemis.core.v5.utils.block.BlockUtil;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.block.BlockFactory;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class CachedWorld implements ArtemisWorld {
    private final Cache<BlockVector, Block> blockCache = CacheBuilder
            .newBuilder()
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build();
    private final World world;

    public CachedWorld(World world) {
        this.world = world;
    }

    @Override
    public NMSMaterial getMaterialAt(int x, int y, int z) {
        final Block block = getBlockAt(x, y, z);

        if (block == null) {
            return NMSMaterial.AIR;
        }

        return block.getMaterial();
    }

    @Override
    public Block getBlockAt(int x, int y, int z) {
        Block block = blockCache.getIfPresent(new BlockVector(x, y, z));

        if (block == null) {
            block = cache(x, y, z);
        }

        return block;
    }

    @Override
    public void updateMaterialAt(Material material, int x, int y, int z) {
        if (material == null)
            return;

        final Block block = BlockFactory.getBlock(material, null, -1, new NaivePoint(x, y, z), null);
        updateMaterialAt(block, x, y, z);
    }


    @Override
    public void updateMaterialAt(Block block, int x, int y, int z) {
        if (block == null)
            return;

        final BlockVector blockVector = new BlockVector(x, y, z);

        if (block.getMaterial() == NMSMaterial.AIR) {
            blockCache.asMap().remove(blockVector);
            return;
        }

        blockCache.put(blockVector, block);
    }

    @Override
    public Block cache(int x, int y, int z) {
        final ac.artemis.packet.minecraft.block.Block bukkitBlock = BlockUtil.getBlockAsync(world, x, y, z);

        if (bukkitBlock == null)
            return new BlockAir(new NaivePoint(x, y, z), EnumFacing.UP);

        final Block block = BlockFactory.getBlock(bukkitBlock.getType(), null, bukkitBlock.getData(), new NaivePoint(x, y, z), null);
        final BlockVector vector = new BlockVector(x, y, z);

        if (block.getMaterial() == NMSMaterial.AIR) {
            this.blockCache.asMap().remove(vector);
            return block;
        }

        this.blockCache.put(vector, block);
        return block;
    }

    @Override
    public boolean isLoaded(int x, int z) {
        return world.isChunkLoaded(x, z);
    }

    @Override
    public boolean isLoaded(int minX, int maxX, int minZ, int maxZ) {
        minX = minX >> 4;
        maxX = maxX >> 4;
        minZ = minZ >> 4;
        maxZ = maxZ >> 4;

        for (int i = minX; i <= maxX; ++i) {
            for (int j = minZ; j <= maxZ; ++j) {
                if (!isLoaded(i, j)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public World getBukkitWorld() {
        return world;
    }

    static final class BlockVector {
        private final int x,y,z;

        public BlockVector(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BlockVector that = (BlockVector) o;

            if (x != that.x) return false;
            if (y != that.y) return false;
            return z == that.z;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + z;
            return result;
        }
    }
}
