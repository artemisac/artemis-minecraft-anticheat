package ac.artemis.packet.minecraft.inventory;

import ac.artemis.packet.minecraft.EnchantType;
import ac.artemis.packet.minecraft.Wrapped;
import ac.artemis.packet.minecraft.material.Material;

import java.util.Map;

public interface ItemStack extends Wrapped {
    Material getType();

    void setType(final Material type);

    int getDurability();

    void setDurability(final int durability);

    int getAmount();

    byte getData();

    boolean hasEnchant(final EnchantType enchant);

    int getEnchantLevel(final EnchantType enchant);

    Map<EnchantType, Integer> getEnchants();
}
