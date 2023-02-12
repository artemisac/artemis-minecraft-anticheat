package ac.artemis.anticheat.bukkit;

import ac.artemis.anticheat.bukkit.entity.BukkitPlayer;
import ac.artemis.packet.minecraft.Unsafe;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.potion.Potion;
import lombok.SneakyThrows;

public class BukkitUnsafe implements Unsafe {
    @Override
    public Player fromBukkitPlayer(Object o) {
        return o == null ? null : new BukkitPlayer((org.bukkit.entity.Player) o);
    }

    @Override
    public Material fromBukkitMaterial(Object o) {
        return o == null ? null : new BukkitMaterial((org.bukkit.Material) o);
    }

    @Override
    public ItemStack fromBukkitItem(Object o) {
        return o == null ? null : new BukkitItemStack((org.bukkit.inventory.ItemStack) o);
    }

    @Override
    public Potion fromBukkitPotion(Object o) {
        return o == null ? null : (Potion) new BukkitPotion(org.bukkit.potion.Potion.fromItemStack((org.bukkit.inventory.ItemStack) o)).v();
    }
}
