package ac.artemis.anticheat.bukkit;

import ac.artemis.packet.minecraft.Minecraft;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;

public class BukkitMinecraft implements Minecraft {
    @Override
    public ItemStack createItemStack(Material material) {
        return new BukkitItemStack(
                new org.bukkit.inventory.ItemStack((org.bukkit.Material) material.v())
        );
    }

    @Override
    public ItemStack createItemStack(Material material, int amount, short damage) {
        return new BukkitItemStack(
                new org.bukkit.inventory.ItemStack(
                        (org.bukkit.Material) material.v(),
                        amount,
                        damage
                )
        );
    }

    @Override
    public ItemStack createItemStack(Material material, int amount, short damage, byte data) {
        return new BukkitItemStack(
                new org.bukkit.inventory.ItemStack(
                        (org.bukkit.Material) material.v(),
                        amount,
                        damage,
                        data
                )
        );
    }

    @Override
    public Location createLocation(World world, double x, double y, double z) {
        return new BukkitLocation(
                new org.bukkit.Location(
                        world.v(),
                        x,
                        y,
                        z
                )
        );
    }

    @Override
    public Location createLocation(World world, double x, double y, double z, float yaw, float pitch) {
        return new BukkitLocation(
                new org.bukkit.Location(
                        world.v(),
                        x,
                        y,
                        z,
                        yaw,
                        pitch
                )
        );
    }
}
