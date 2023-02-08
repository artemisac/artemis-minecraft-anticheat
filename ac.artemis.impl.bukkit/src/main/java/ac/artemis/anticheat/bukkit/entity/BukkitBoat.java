package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.impl.Boat;

public class BukkitBoat extends BukkitVehicle<org.bukkit.entity.Boat> implements Boat {
    public BukkitBoat(org.bukkit.entity.Boat wrapper) {
        super(wrapper);
    }
}
