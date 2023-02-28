package ac.artemis.packet.minecraft.entity;

import ac.artemis.packet.minecraft.GameMode;
import ac.artemis.packet.minecraft.inventory.Inventory;

public interface Human extends LivingEntity {
    Inventory getInventory();

    String getName();

    GameMode getGameMode();
}
