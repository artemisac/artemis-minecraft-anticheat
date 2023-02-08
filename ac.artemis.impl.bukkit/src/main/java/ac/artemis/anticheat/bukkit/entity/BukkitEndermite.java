package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.impl.Endermite;

public class BukkitEndermite extends BukkitLivingEntity<org.bukkit.entity.Endermite> implements Endermite {
    public BukkitEndermite(org.bukkit.entity.Endermite wrapper) {
        super(wrapper);
    }
}
