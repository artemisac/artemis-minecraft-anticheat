package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.impl.Wither;

public class BukkitWither extends BukkitLivingEntity<org.bukkit.entity.Wither> implements Wither {
    public BukkitWither(org.bukkit.entity.Wither wrapper) {
        super(wrapper);
    }
}
