package ac.artemis.packet.minecraft.inventory;

import ac.artemis.packet.minecraft.Wrapped;

public interface Inventory extends Wrapped {
    ItemStack getItem(final int slot);

    ItemStack getBoots();

    @Deprecated
    ItemStack getItemInHand();
    ItemStack getItemInMainHand();
    ItemStack getItemInOffHand();

    int getHeldItemSlot();

    int getSize();
}
