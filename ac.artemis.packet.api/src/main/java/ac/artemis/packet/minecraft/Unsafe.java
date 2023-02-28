package ac.artemis.packet.minecraft;

import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.potion.Potion;

@Deprecated
public interface Unsafe {
    Player fromBukkitPlayer(final Object bukkitPlayer);
    Material fromBukkitMaterial(final Object bukkitMaterial);
    ItemStack fromBukkitItem(final Object bukkitStack);
    Potion fromBukkitPotion(final Object bukkitItemStack);

    /**
     * @return Deprecated instance
     */
    static Unsafe v() {
        return Unsafe.MinecraftInstance.getDeprecated();
    }

    /**
     * Stored reference to the server to not break stuff / make stuff
     * messy.
     */
    class MinecraftInstance {
        private static Unsafe server;

        /**
         * @return Deprecated instance
         */
        public static Unsafe getDeprecated() {
            return server;
        }

        /**
         * @param v the deprecated instance
         */
        public static void setDeprecated(Unsafe v) {
            server = v;
        }
    }
}
