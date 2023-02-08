package ac.artemis.anticheat.bukkit;

import ac.artemis.anticheat.bukkit.entity.BukkitEntity;
import ac.artemis.packet.minecraft.AbstractWrapper;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.world.Chunk;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BukkitChunk extends AbstractWrapper<org.bukkit.Chunk> implements Chunk {
    public BukkitChunk(org.bukkit.Chunk wrapper) {
        super(wrapper);
    }

    @Override
    public List<Entity> getEntities() {
        return Arrays
                .stream(wrapper.getEntities())
                .map(BukkitEntity::of)
                .collect(Collectors.toList());
    }
}
