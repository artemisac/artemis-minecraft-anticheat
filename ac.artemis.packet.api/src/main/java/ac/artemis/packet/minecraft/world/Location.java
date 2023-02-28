package ac.artemis.packet.minecraft.world;

import ac.artemis.packet.minecraft.Wrapped;
import ac.artemis.packet.minecraft.block.Block;

/**
 * The wrapper for bukkit locations which seem to need to constantly hold a world variable and stuff.
 * @deprecated Deprecate all of this eww
 */
public interface Location extends Wrapped {
    /**
     * @return the x coordinate
     */
    double getX();

    /**
     * @return the y coordinate
     */
    double getY();

    /**
     * @return the z coordinate
     */
    double getZ();

    /**
     * @return the yaw coordinate
     */
    float getYaw();

    /**
     * @return the pitch coordinate
     */
    float getPitch();

    /**
     * @return the block x
     */
    int getBlockX();

    /**
     * @return the block y
     */
    int getBlockY();

    /**
     * @return the block z
     */
    int getBlockZ();

    /**
     * Subtracts a specific value to the location
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return New location instance with the updated coordinates
     */
    Location subtract(final int x, final int y, final int z);

    /**
     * Adds a specific value to the location
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return New location instance with the updated coordinates
     */
    Location add(final int x, final int y, final int z);

    /**
     * @return Block at the specified x, y, z coordinates in the specified World
     */
    Block getBlock();

    /**
     * @return Chunk wrapper for this specific location
     */
    Chunk getChunk();

    /**
     * Gets world.
     *
     * @return the world
     */
    World getWorld();

}
