package ac.artemis.core.v4.emulator.entity.utils;

import ac.artemis.packet.minecraft.Unsafe;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.Emulator;

/**
 * @author Ghast
 * @since 29/10/2020
 * Artemis © 2020
 */
public class ItemUtil {
    public static int getItemUseByItem(Emulator entity, ItemStack stack) {
        if (stack == null) return 0;
        NMSMaterial material = NMSMaterial.matchXMaterial(stack.getType());
        if (isMaterialWeapon(material)) return 72000;

        final boolean food = isMaterialFood(material);
        final boolean satiated = food && (entity.getData().getPlayer().getFoodLevel() < 20
                || material.equals(NMSMaterial.GOLDEN_APPLE)
                || material.equals(NMSMaterial.ENCHANTED_GOLDEN_APPLE));

        final boolean potion = material.equals(NMSMaterial.POTION);
        if (satiated || potion) return 32;
        return 0;
    }

    public static boolean isMaterialWeapon(NMSMaterial material) {
        return material.name().toUpperCase().contains("SWORD") || material.equals(NMSMaterial.BOW);
    }

    public static boolean isMaterialFood(NMSMaterial material) {
        return material.getMaterial() != null && material.getMaterial().isEdible();
    }
}
