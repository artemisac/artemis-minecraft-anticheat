package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.impl.Skeleton;

public class BukkitSkeleton extends BukkitLivingEntity<org.bukkit.entity.Skeleton> implements Skeleton {
    public BukkitSkeleton(org.bukkit.entity.Skeleton wrapper) {
        super(wrapper);
    }
}
