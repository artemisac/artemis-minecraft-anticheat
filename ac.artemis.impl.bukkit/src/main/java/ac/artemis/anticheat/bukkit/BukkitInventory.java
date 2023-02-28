package ac.artemis.anticheat.bukkit;

import ac.artemis.packet.minecraft.AbstractWrapper;
import ac.artemis.packet.minecraft.inventory.Inventory;
import ac.artemis.packet.minecraft.inventory.ItemStack;

public class BukkitInventory extends AbstractWrapper<org.bukkit.inventory.PlayerInventory> implements Inventory {
    public BukkitInventory(org.bukkit.inventory.PlayerInventory wrapper) {
        super(wrapper);
    }

    @Override
    public ItemStack getItem(int slot) {
        final org.bukkit.inventory.ItemStack stack = wrapper.getItem(slot);

        return stack == null ? null : new BukkitItemStack(stack);
    }

    @Override
    public ItemStack getBoots() {
        return wrapper.getBoots() == null ? null : new BukkitItemStack(wrapper.getBoots());
    }

    @Override
    public ItemStack getItemInHand() {
        return wrapper.getItemInHand() == null ? null : new BukkitItemStack(wrapper.getItemInHand());
    }

    @Override
    public ItemStack getItemInMainHand() {
        return new BukkitItemStack(wrapper.getItemInMainHand());
    }

    @Override
    public ItemStack getItemInOffHand() {
        return new BukkitItemStack(wrapper.getItemInOffHand());
    }

    @Override
    public int getHeldItemSlot() {
        return wrapper.getHeldItemSlot();
    }

    @Override
    public int getSize() {
        return wrapper.getSize();
    }
}
