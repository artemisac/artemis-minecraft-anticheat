package ac.artemis.anticheat.bukkit.gui;

import ac.artemis.anticheat.api.check.type.Category;
import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.api.check.type.Type;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class CheckListController implements InventoryProvider {

    public static void openInventory(Player player, Category check) {
        CheckListController.openInventory(player, 0, check);
    }

    public static void openInventory(Player player, int page, Category check) {
        CheckListController.returnCheckGui(check).open(player, page);
    }

    public static SmartInventory returnCheckGui(Category check) {
        return SmartInventory
                // Initialize the builder
                .builder()
                // Set the manager to the Artemis one instead of the default plugin one
                .manager(BukkitArtemis.INSTANCE.getInventoryManager())
                // Set the category's name + a random number in case of duplicates
                .id(check.name() + Math.random())
                // Set provider to itself
                .provider(new CheckListController(check))
                // Set the size to 4 rows and 9 columns
                .size(4, 9)
                // Set the title to the following
                .title(ChatColor.AQUA + "Check info for: " + check)
                // Set the parent to the checks category controller
                .parent(ChecksController.inv)
                // Prevent closing
                .closeable(true)
                // Finalize build
                .build();
    }

    private final Category type;

    public CheckListController(Category type) {
        this.type = type;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        // Initialize the pagination
        Pagination pagination = contents.pagination();

        // Grab the main Theme colors
        final String main = ThemeManager.getCurrentTheme().getMainColor();
        final String side = ThemeManager.getCurrentTheme().getSecondaryColor();
        final String bracket = ThemeManager.getCurrentTheme().getBracketsColor();

        // Initialize the list of items
        final List<ClickableItem> items = new ArrayList<>();

        // Get all types of checks which are of the same category as defined
        final Set<Type> vars = Arrays.stream(Type.values()).filter(e -> e.getCategory().equals(type)).collect(Collectors.toSet());

        // For every type of check, create a new item
        for (Type check : vars) {
            ClickableItem stack = ClickableItem.of(new ItemBuilder(NMSMaterial.BOOK)
                    .name(main + check + side + " " + bracket + "(" + main + this.type + bracket + ")")
                    .build(), e -> {
                CheckSubListController.openInventory(player, check, new AtomicBoolean());
            });
            items.add(stack);
        }

        // Set the selected items to the iteration
        pagination.setItems(items.toArray(new ClickableItem[items.size()]));

        // Set a maximum of 27 items per page for the pagination
        pagination.setItemsPerPage(27);

        // Set the iterator as horizontal
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));

        // Set the previous page arrow
        contents.set(3, 3, ClickableItem.of(new ItemBuilder(NMSMaterial.ARROW).name("&aPrevious Page").build(),
                e -> CheckListController.openInventory(player, pagination.previous().getPage(), this.type)));

        // Set the close page item
        contents.set(3, 4, ClickableItem.of(new ItemBuilder(NMSMaterial.CHIPPED_ANVIL).name("&4&lEXIT").build(),
                e -> ChecksController.inv.open(player)));

        // Set the next page arrow
        contents.set(3, 5, ClickableItem.of(new ItemBuilder(NMSMaterial.ARROW).name("&aNext Page").build(),
                e -> CheckListController.openInventory(player, pagination.next().getPage(), this.type)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}

