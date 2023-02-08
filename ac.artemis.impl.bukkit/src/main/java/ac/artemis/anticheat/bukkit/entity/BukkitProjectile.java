package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.Projectile;

public class BukkitProjectile<T extends org.bukkit.entity.Projectile> extends BukkitEntity<T> implements Projectile {
    public BukkitProjectile(T wrapper) {
        super(wrapper);
    }
}
