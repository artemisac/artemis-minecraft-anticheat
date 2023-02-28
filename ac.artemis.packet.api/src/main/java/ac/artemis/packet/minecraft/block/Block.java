package ac.artemis.packet.minecraft.block;

import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.Wrapped;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;

public interface Block extends Wrapped {
    Material getType();

    /**
     * @return the x coordinate
     */
    int getX();

    /**
     * @return the y coordinate
     */
    int getY();

    /**
     * @return the z coordinate
     */
    int getZ();

    boolean isLiquid();

    Block getRelative(final BlockFace blockFace);

    World getWorld();

    Location getLocation();

    byte getData();
}
