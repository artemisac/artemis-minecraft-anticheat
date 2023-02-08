package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.impl.Minecart;

public class BukkitMinecart extends BukkitVehicle<org.bukkit.entity.Minecart> implements Minecart {
    public BukkitMinecart(org.bukkit.entity.Minecart wrapper) {
        super(wrapper);
    }
}
