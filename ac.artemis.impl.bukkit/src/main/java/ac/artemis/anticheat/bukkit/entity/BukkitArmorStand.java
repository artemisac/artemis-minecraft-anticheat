package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.impl.ArmorStand;

public class BukkitArmorStand extends BukkitEntity<org.bukkit.entity.ArmorStand> implements ArmorStand {
    public BukkitArmorStand(org.bukkit.entity.ArmorStand wrapper) {
        super(wrapper);
    }
}
