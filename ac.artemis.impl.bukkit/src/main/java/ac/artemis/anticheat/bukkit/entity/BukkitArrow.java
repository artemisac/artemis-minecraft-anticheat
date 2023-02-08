package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.impl.Arrow;

public class BukkitArrow extends BukkitProjectile<org.bukkit.entity.Arrow> implements Arrow {
    public BukkitArrow(org.bukkit.entity.Arrow wrapper) {
        super(wrapper);
    }
}
