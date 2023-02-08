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
        return new BukkitItemStack(wrapper.getItem(slot));
    }

    @Override
    public ItemStack getBoots() {
        return new BukkitItemStack(wrapper.getBoots());
    }

    @Override
    public ItemStack getItemInHand() {
        return new BukkitItemStack(wrapper.getItemInHand());
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
