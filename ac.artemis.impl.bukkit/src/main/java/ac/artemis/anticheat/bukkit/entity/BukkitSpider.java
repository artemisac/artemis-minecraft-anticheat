package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.LivingEntity;

public class BukkitSpider extends BukkitLivingEntity<org.bukkit.entity.LivingEntity> implements LivingEntity {
    public BukkitSpider(org.bukkit.entity.LivingEntity wrapper) {
        super(wrapper);
    }
}
