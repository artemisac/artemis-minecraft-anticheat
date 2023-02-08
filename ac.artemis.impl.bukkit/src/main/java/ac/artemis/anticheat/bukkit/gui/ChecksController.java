package ac.artemis.anticheat.bukkit.gui;

import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.api.check.type.Category;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.anticheat.bukkit.utils.ItemBuilder;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class ChecksController implements InventoryProvider {

    public static final SmartInventory inv = SmartInventory
            // Initialize the builder
            .builder()
            // Set the manager to the Artemis one instead of the default plugin one
            .manager(BukkitArtemis.INSTANCE.getInventoryManager())
            // Set id to category checklist
            .id("checklist")
            // Set provider to itself
            .provider(new ChecksController())
            // Set size to 3 rows and 9 columns
            .size(3, 9)
            // Set title to Checks GUI
            .title(ChatColor.AQUA + "Checks GUI")
            // Deny closing
            .closeable(false)
            // Finalize build
            .build();

    public static void openInventory(Player player) {
        inv.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        // Initialize the pagination
        Pagination pagination = contents.pagination();

        // Fill the first and the third row with stained glass pane
        contents.fillRow(0, ClickableItem.empty(new ItemBuilder(NMSMaterial.GRAY_STAINED_GLASS_PANE).name("&7").build()));
        contents.fillRow(2, ClickableItem.empty(new ItemBuilder(NMSMaterial.GRAY_STAINED_GLASS_PANE).name("&7").build()));

        // Initialize the item list
        List<ClickableItem> items = new ArrayList<>();

        // Grab the theme colors
        String main = ThemeManager.getCurrentTheme().getMainColor();
        String side = ThemeManager.getCurrentTheme().getSecondaryColor();

        // For every category, create a new item
        for (Category type : Category.values()) {

            final long checks = BukkitArtemis.INSTANCE.getApi().getCheckManager()
                    .getInfos()
                    .stream()
                    .filter(e -> e.getType().getCategory().equals(type))
                    .count();

            final ClickableItem stack = ClickableItem.of(new ItemBuilder(NMSMaterial.BOOKSHELF)
                            .name(main + type.name())
                            .addLore(type.getDescription())
                            .addLore("&7",
                                    "&aThere are currently " + checks + " " + type.name() + " checks")
                            .build(),
                    e -> CheckListController.openInventory(player, type)
            );
            items.add(stack);
        }

        // Set the list's items to the iterator's items
        pagination.setItems(items.toArray(new ClickableItem[items.size()]));

        // Set the iterator at a specific position and make it horizontal
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 2));

        // Allow a maximum of 9 items per page for the iterator
        pagination.setItemsPerPage(9);

        // Set the button for the previous page
        contents.set(2, 3, ClickableItem.of(new ItemBuilder(NMSMaterial.ARROW).name("&cPrevious Page").build(),
                e -> ChecksController.inv.open(player, pagination.previous().getPage())));

        // Set the exit button
        contents.set(2, 4, ClickableItem.of(new ItemBuilder(NMSMaterial.DAMAGED_ANVIL).name("&4&lEXIT").build(),
                e -> MainController.inv.open(player)));

        // Set the button for the next page
        contents.set(2, 5, ClickableItem.of(new ItemBuilder(NMSMaterial.ARROW).name("&aNext Page").build(),
                e -> ChecksController.inv.open(player, pagination.next().getPage())));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}

