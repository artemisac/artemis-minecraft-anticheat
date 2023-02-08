package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.packet.minecraft.entity.impl.Silverfish;

public class BukkitSilverfish extends BukkitLivingEntity<org.bukkit.entity.Silverfish> implements Silverfish {
    public BukkitSilverfish(org.bukkit.entity.Silverfish wrapper) {
        super(wrapper);
    }
}
