package ac.artemis.anticheat.bukkit.gui;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.entity.BukkitPlayer;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.anticheat.bukkit.utils.ItemBuilder;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.entity.Player;

public class InfoController implements InventoryProvider {

    @Override
    public void init(Player player, InventoryContents contents) {
        final PlayerData data = BukkitArtemis.INSTANCE
                .getApi()
                .getPlayerDataManager()
                .getData(new BukkitPlayer(player));

        if (data == null) {
            contents.fill(ClickableItem.of(new ItemBuilder(NMSMaterial.RED_WOOL).name("&c&lCould not load data").build(), e -> {
                player.closeInventory();
            }));
            return;
        }


    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
