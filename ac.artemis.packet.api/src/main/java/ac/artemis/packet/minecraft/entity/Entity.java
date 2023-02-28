package ac.artemis.packet.minecraft.entity;

import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;

import java.util.UUID;

public interface Entity extends Messager {
    UUID getUniqueId();

    World getWorld();

    Location getLocation();

    EntityType getType();

    int getEntityId();

    boolean isDead();

    void teleport(final Location location);
}
