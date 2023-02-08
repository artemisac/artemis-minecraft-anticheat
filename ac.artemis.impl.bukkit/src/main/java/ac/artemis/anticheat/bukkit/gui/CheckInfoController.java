package ac.artemis.anticheat.bukkit.gui;


import ac.artemis.anticheat.api.check.CheckInfo;
import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.anticheat.bukkit.utils.ItemBuilder;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;


public class CheckInfoController implements InventoryProvider {

    public static void openInventory(Player player, CheckInfo check) {
        CheckInfoController.returnCheckGui(check).open(player);
    }

    public static SmartInventory returnCheckGui(CheckInfo check) {
        return SmartInventory
                // Initialize the builder
                .builder()
                // Set the manager to the Artemis one instead of the default plugin one
                .manager(BukkitArtemis.INSTANCE.getInventoryManager())
                // Variable of the Check with a random to prevent any issue
                .id(check.getVar() + Math.random())
                // Set the provider to itself
                .provider(new CheckInfoController(check))
                // Set the size to 3 rows and 9 columns
                .size(3, 9)
                // Set the title to the variable
                .title(ChatColor.AQUA + "Check-" + check.getVar())
                // Set the parent to the Check list
                .parent(CheckSubListController.returnCheckGui(check.getType(), new AtomicBoolean()))
                // Disable closing
                .closeable(true)
                // Build the GUI
                .build();
    }

    public final CheckInfo check;

    public CheckInfoController(CheckInfo check) {
        this.check = check;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(new ItemBuilder(NMSMaterial.GRAY_STAINED_GLASS_PANE).name("&b").build()));


        contents.set(1, 3,
                ClickableItem.of(check.isEnabled()
                        ? new ItemBuilder(NMSMaterial.LIME_DYE)
                        .name("&aACTIVE").build()
                        : new ItemBuilder(NMSMaterial.GRAY_DYE)
                        .name("&cINACTIVE").build(),
                e -> {
                    check.setBannable(true);
                    check.save();
                    player.sendMessage(
                            Chat.translate(
                                    ConfigManager.getSettings().getString("general.prefix")
                                            + "&b"
                                            + " toggled check "
                                            + (!check.isEnabled()
                                            ? "&a&lEnabled" : "&c&lDisabled"
                                    )
                            )
                    );
                    CheckInfoController.openInventory(player, check);
                })
        );
        contents.set(1, 4, ClickableItem.empty(new ItemBuilder(NMSMaterial.PAPER)
                .name("&b" + check.getType().name() + "&7" + " (" + "&b" + check.getVar() + "&7" + ")")
                .addLore(Chat.translate("&bCheck type&7: &b" + check.getType().name()),
                        Chat.translate("&bCheck variable&7: &b" + check.getVar()),
                        Chat.translate("&bMax violations&7: &b" + check.getMaxVl()),
                        Chat.translate("&bStatus&7: " + (check.isEnabled() ? "&a&lEnabled" : "&c&lDisabled"))
                ).build()));
        contents.set(1, 8, ClickableItem.of(new ItemBuilder(NMSMaterial.DAMAGED_ANVIL)
                        .name("&4&lEXIT")
                        .build(),
                e -> CheckSubListController.openInventory(player, check.getType(), new AtomicBoolean())));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}

