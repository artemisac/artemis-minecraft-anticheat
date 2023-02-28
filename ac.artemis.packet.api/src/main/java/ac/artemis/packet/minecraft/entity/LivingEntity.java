package ac.artemis.packet.minecraft.entity;

import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.packet.minecraft.inventory.Inventory;
import ac.artemis.packet.minecraft.potion.Effect;

import java.util.Collection;

public interface LivingEntity extends Entity {
    double getHealth();

    boolean hasEffect(final PotionEffectType type);

    Collection<Effect> getActivePotionEffects();
}
