package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.anticheat.bukkit.BukkitEffect;
import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.packet.minecraft.entity.LivingEntity;
import ac.artemis.packet.minecraft.inventory.Inventory;
import ac.artemis.packet.minecraft.potion.Effect;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

public class BukkitLivingEntity<T extends org.bukkit.entity.LivingEntity> extends BukkitEntity<T> implements LivingEntity {
    public BukkitLivingEntity(T wrapper) {
        super(wrapper);
    }

    @Override
    public double getHealth() {
        return wrapper.getHealth();
    }

    @Override
    public Inventory getInventory() {
        return (Inventory) ((Player) wrapper).getInventory(); // FIXME: 2023/2/12 
    }

    @Override
    public boolean hasEffect(PotionEffectType type) {
        return wrapper.hasPotionEffect(org.bukkit.potion.PotionEffectType.getByName(type.name()));
    }

    @Override
    public Collection<Effect> getActivePotionEffects() {
        // TODO: Finish effect
        return wrapper.getActivePotionEffects()
                .stream()
                .map(BukkitEffect::new)
                .collect(Collectors.toList());
    }
}
