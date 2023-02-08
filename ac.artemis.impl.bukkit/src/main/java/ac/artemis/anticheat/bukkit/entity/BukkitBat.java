package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.impl.Bat;

public class BukkitBat extends BukkitLivingEntity<org.bukkit.entity.Bat> implements Bat {
    public BukkitBat(org.bukkit.entity.Bat wrapper) {
        super(wrapper);
    }
}
