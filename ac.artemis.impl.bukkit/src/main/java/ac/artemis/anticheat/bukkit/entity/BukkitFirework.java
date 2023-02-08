package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.impl.Firework;

public class BukkitFirework extends BukkitEntity<org.bukkit.entity.Firework> implements Firework {
    public BukkitFirework(org.bukkit.entity.Firework wrapper) {
        super(wrapper);
    }
}
