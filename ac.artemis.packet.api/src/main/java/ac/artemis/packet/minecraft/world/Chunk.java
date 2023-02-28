package ac.artemis.packet.minecraft.world;

import ac.artemis.packet.minecraft.Wrapped;
import ac.artemis.packet.minecraft.entity.Entity;

import java.util.List;

public interface Chunk extends Wrapped {
    List<Entity> getEntities();
}
