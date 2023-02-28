package ac.artemis.anticheat.bukkit;

import ac.artemis.packet.minecraft.AbstractWrapper;
import ac.artemis.packet.minecraft.EnchantType;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.minecraft.material.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BukkitItemStack extends AbstractWrapper<org.bukkit.inventory.ItemStack> implements ItemStack {
    public BukkitItemStack(org.bukkit.inventory.ItemStack wrapper) {
        super(wrapper);
    }

    @Override
    public Material getType() {
        return wrapper.getType() == null ? null : new BukkitMaterial(wrapper.getType());
    }

    @Override
    public void setType(Material material) {
        wrapper.setType(material.v());
    }

    @Override
    public int getDurability() {
        return wrapper.getDurability();
    }

    @Override
    public void setDurability(int i) {
        wrapper.setDurability((short) i);
    }

    @Override
    public int getAmount() {
        return wrapper.getAmount();
    }

    @Override
    public byte getData() {
        return wrapper.getData().getData();
    }

    @Override
    public boolean hasEnchant(EnchantType enchantType) {
        return wrapper.containsEnchantment(Enchantment.getByName(enchantType.getName()));
    }

    @Override
    public int getEnchantLevel(EnchantType enchantType) {
        return wrapper.getEnchantmentLevel(Enchantment.getByName(enchantType.getName()));
    }

    @Override
    public Map<EnchantType, Integer> getEnchants() {
        if (wrapper.getEnchantments() == null) {
            return new HashMap<>();
        }

        final Map<EnchantType, Integer> results = new HashMap<>();
        wrapper.getEnchantments().forEach((type, value) -> {
            results.put(EnchantType.getByName(type.getName()), value);
        });
        return results;
    }
}
