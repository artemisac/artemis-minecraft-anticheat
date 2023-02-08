package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.impl.Zombie;

public class BukkitZombie extends BukkitLivingEntity<org.bukkit.entity.Zombie> implements Zombie {
    public BukkitZombie(org.bukkit.entity.Zombie wrapper) {
        super(wrapper);
    }
}
