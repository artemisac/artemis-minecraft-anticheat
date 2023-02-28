package ac.artemis.packet.minecraft;

import ac.artemis.packet.minecraft.config.Configuration;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.plugin.Plugin;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;

/**
 * The wrapper interface for all of Minecraft's functions.
 */
public interface Minecraft {

    /**
     * Creates an ItemStack from a Material wrapper.
     *
     * @param material the material
     * @return the item stack
     */
    ItemStack createItemStack(final Material material);

    /**
     * Creates an ItemStack from a Material wrapper.
     *
     * @param type   the type
     * @param amount the amount
     * @param damage the damage
     * @return the wrapped ItemStack
     */
    ItemStack createItemStack(Material type, int amount, short damage);

    /**
     * Creates an ItemStack from a Material wrapper.
     *
     * @param type   the type
     * @param amount the amount
     * @param damage the damage
     * @param data   the data
     * @return the wrapped ItemStack
     */
    ItemStack createItemStack(Material type, int amount, short damage, byte data);

    /**
     * Creates a wrapped location on the basis of the passed data
     *
     * @param world Bukkit world
     * @param x     location X coordinate
     * @param y     location Y coordinate
     * @param z     location Z coordinate
     * @return the wrapped location
     */
    Location createLocation(final World world, final double x, final double y, final double z);

    /**
     * Creates a wrapped location on the basis of the passed data
     *
     * @param world Bukkit world
     * @param x     location X coordinate
     * @param y     location Y coordinate
     * @param z     location Z coordinate
     * @param yaw   location of the yaw axis
     * @param pitch location of the pitch axis
     * @return the wrapped location
     */
    Location createLocation(final World world, final double x, final double y, final double z, final float yaw, final float pitch);

    /**
     * V minecraft.
     *
     * @return Minecraft instance
     */
    static Minecraft v() {
        return MinecraftInstance.getMinecraft();
    }

    /**
     * Stored reference to the server to not break stuff / make stuff
     * messy.
     */
    class MinecraftInstance {
        private static Minecraft server;

        /**
         * Gets minecraft.
         *
         * @return Minecraft instance
         */
        public static Minecraft getMinecraft() {
            return server;
        }

        /**
         * Sets minecraft.
         *
         * @param v the minecraft instance
         */
        public static void setMinecraft(Minecraft v) {
            server = v;
        }
    }
}
