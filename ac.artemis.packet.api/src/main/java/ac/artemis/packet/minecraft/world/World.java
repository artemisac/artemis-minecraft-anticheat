package ac.artemis.packet.minecraft.world;

import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.Wrapped;
import ac.artemis.packet.minecraft.entity.Entity;

import java.util.List;

public interface World extends Wrapped {
    String getName();
    boolean isChunkLoaded(final int x, final int z);
    Block getBlockAt(final int x, final int y, final int z);

    List<Entity> getEntities();
}
