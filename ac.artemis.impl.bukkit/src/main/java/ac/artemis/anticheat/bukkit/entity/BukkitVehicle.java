package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.Vehicle;

public class BukkitVehicle<T extends org.bukkit.entity.Vehicle> extends BukkitEntity<T> implements Vehicle {
    public BukkitVehicle(T wrapper) {
        super(wrapper);
    }

    @Override
    public Entity getPassenger() {
        return BukkitEntity.of(wrapper.getPassenger());
    }
}
