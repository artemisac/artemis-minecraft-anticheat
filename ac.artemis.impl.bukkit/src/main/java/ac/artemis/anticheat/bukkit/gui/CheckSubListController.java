package ac.artemis.anticheat.bukkit.gui;

import ac.artemis.anticheat.api.check.CheckInfo;
import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.anticheat.bukkit.utils.ItemBuilder;
import ac.artemis.core.v5.language.Lang;
import ac.artemis.core.v5.threading.Threading;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


public class CheckSubListController implements InventoryProvider {


    public static void openInventory(Player player, Type type, AtomicBoolean changed) {
        CheckSubListController.openInventory(player, 0, type, changed);
    }

    public static void openInventory(Player player, int page, Type type, AtomicBoolean changed) {
        CheckSubListController.returnCheckGui(type, changed).open(player, page);
    }

    private static final ExecutorService service = Threading.getOrStartService("check-hot-reload");

    public static SmartInventory returnCheckGui(Type type, AtomicBoolean changed) {
        return SmartInventory
                // Initialize the builder
                .builder()
                // Set the manager to the Artemis one instead of the default plugin one
                .manager(BukkitArtemis.INSTANCE.getInventoryManager())
                // Set the id to the Check's category type
                .id(type.getCategory().name() + type.getCorrectName())
                // Set the provider to itself
                .provider(new CheckSubListController(type, changed))
                // Set the size to 4 rows ad=nd 9 columns
                .size(4, 9)
                // Set the title
                .title(ChatColor.AQUA + "Check info for: " + type.getCategory().name() + ":" + type)
                // Set the parent to the check category list
                .parent(CheckListController.returnCheckGui(type.getCategory()))
                // Disable closing
                .closeable(true)
                // Build the GUI
                .build();
    }

    private final Type type;
    private final AtomicBoolean changed;

    public CheckSubListController(Type type, AtomicBoolean changed) {
        this.type = type;
        this.changed = changed;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        final List<ClickableItem> items = new ArrayList<>();

        final String main = ThemeManager.getCurrentTheme().getMainColor();
        final String side = ThemeManager.getCurrentTheme().getSecondaryColor();
        final String bracket = ThemeManager.getCurrentTheme().getBracketsColor();

        final List<CheckInfo> vars = BukkitArtemis.INSTANCE.getApi().getCheckManager()
                // Get the CheckManager
                .getInfos()
                // Start stream iteration
                .stream()
                // Map them to only receive the info
                .filter(info -> info.getType().equals(type))
                .distinct()
                // Sort them by alphabetical order
                .sorted(Comparator.comparing(CheckInfo::getVar))
                // Collect them to a set
                .collect(Collectors.toList());

        // Loop this for everything check type of the sort
        for (CheckInfo check : vars) {
            // Create the AItemStack
            ClickableItem stack = ClickableItem.of(new ItemBuilder(NMSMaterial.BOOK)
                            // Name should be of the following format: KILLAURA:A (TYPE:VAR)
                            .name(main + check.getVisualCategory() + side + " " + bracket + "(" + main + check.getVisualName() + bracket + ")")
                            // Add the Item lore
                            .addLore(
                                main + "Enabled" + side + ": " + (check.isEnabled() ? "&a&lENABLED" : "&c&lDISABLED") + side + " &o(Left-Click to toggle)",
                                main + "Bannable" + side + ": " + (check.isBannable() ? "&4&lBANNABLE" : "&6&lALERT ONLY") + side + " &o(Shift-Left-Click to toggle)",
                                main + "Max VL" + side + ": " + main + check.getMaxVl()
                            )
                            // Build the AItemStack
                            .build(),
                    // Open the check details when clicked
                    e -> {
                        changed.set(true);
                        if (e.isLeftClick()) {
                            if (e.isShiftClick()) check.setBannable(!check.isBannable());
                            else check.setEnabled(!check.isEnabled());
                            CheckSubListController.openInventory(player, type, changed);
                        } else if (e.isRightClick()) {
                            CheckInfoController.openInventory(player, check);
                        }
                    }
            );

            // Add it to the total array list
            items.add(stack);
        }

        // Add all the items as an array. Ignore the warning.
        pagination.setItems(items.toArray(new ClickableItem[items.size()]));

        // Set the max item per page to 27, which is 3 rows
        pagination.setItemsPerPage(27);

        // Add it to a horizontal iterator for a nice clean ordered showcase
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));

        // Set the previous page button
        contents.set(3, 3, ClickableItem.of(new ItemBuilder(NMSMaterial.ARROW)
                        .name("&aPrevious Page")
                        .build(),
                e -> CheckSubListController.openInventory(player, pagination.previous().getPage(), this.type, changed)));

        // Set the close page button
        contents.set(3, 4, ClickableItem.of(new ItemBuilder(NMSMaterial.DAMAGED_ANVIL)
                        .name("&4&lEXIT")
                        .build(),
                e -> {
                    if (!changed.get()) {
                        CheckListController.openInventory(player, type.getCategory());
                        return;
                    }
                    YesNoController.openInventory(player,
                            "Do you wish to save all the changes done? Note: this action cannot be reverted",
                            c -> {
                        service.execute(() -> {
                            if (c) {
                                BukkitArtemis.INSTANCE.getApi().getCheckManager().getInfos().forEach(CheckInfo::save);
                                player.sendMessage(Chat.translate(
                                        ThemeManager.getCurrentTheme().getPrefix() + "&a" + Lang.GUI_SAVED_CHECK));
                            } else {
                                BukkitArtemis.INSTANCE.getApi().getCheckManager().reloadChecks();
                            }

                            CheckListController.openInventory(player, type.getCategory());
                        });
                    });
                }));

        // Set the next page button
        contents.set(3, 5, ClickableItem.of(new ItemBuilder(NMSMaterial.ARROW)
                        .name("&aNext Page")
                        .build(),
                e -> CheckSubListController.openInventory(player, pagination.next().getPage(), this.type, changed)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}

