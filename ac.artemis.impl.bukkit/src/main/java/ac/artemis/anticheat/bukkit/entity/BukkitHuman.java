package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.anticheat.bukkit.BukkitInventory;
import ac.artemis.packet.minecraft.GameMode;
import ac.artemis.packet.minecraft.entity.Human;
import ac.artemis.packet.minecraft.inventory.Inventory;
import org.bukkit.entity.HumanEntity;

public class BukkitHuman<T extends HumanEntity> extends BukkitLivingEntity<T> implements Human {
    public BukkitHuman(T wrapper) {
        super(wrapper);
    }

    @Override
    public String getName() {
        return wrapper.getName();
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.values()[wrapper.getGameMode().ordinal()];
    }

    @Override
    public Inventory getInventory() {
        return new BukkitInventory(wrapper.getInventory());
    }
}
