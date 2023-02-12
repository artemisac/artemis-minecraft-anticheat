package ac.artemis.anticheat.bukkit;

import ac.artemis.packet.minecraft.AbstractWrapper;

public class BukkitPotion extends AbstractWrapper<org.bukkit.potion.Potion> implements Potion {
    public BukkitPotion(org.bukkit.potion.Potion wrapper) {
        super(wrapper);
    }

    @Override
    public boolean isSplash() {
        return wrapper.isSplash();
    }
}
