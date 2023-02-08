package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.Vehicle;

public class BukkitHorse extends BukkitVehicle<org.bukkit.entity.Vehicle> implements Vehicle {
    public BukkitHorse(org.bukkit.entity.Vehicle wrapper) {
        super(wrapper);
    }
}
