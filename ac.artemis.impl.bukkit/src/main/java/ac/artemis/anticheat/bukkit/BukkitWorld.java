package ac.artemis.anticheat.bukkit;

import ac.artemis.anticheat.bukkit.entity.BukkitEntity;
import ac.artemis.packet.minecraft.AbstractWrapper;
import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class BukkitWorld extends AbstractWrapper<org.bukkit.World> implements World {
    public BukkitWorld(org.bukkit.World wrapper) {
        super(wrapper);
    }

    @Override
    public String getName() {
        return wrapper.getName();
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        return wrapper.isChunkLoaded(x, z);
    }

    @Override
    public Block getBlockAt(int x, int y, int z) {
        final org.bukkit.block.Block block = wrapper.getBlockAt(x, y, z);
        return block == null ? null : new BukkitBlock(block);
    }

    @Override
    public List<Entity> getEntities() {
        return wrapper
                .getEntities()
                .stream()
                .map(BukkitEntity::of)
                .collect(Collectors.toList());
    }
}
