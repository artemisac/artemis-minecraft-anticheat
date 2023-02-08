package ac.artemis.anticheat.bukkit.gui;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.bukkit.utils.ItemBuilder;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class YesNoController implements InventoryProvider {
    public static void openInventory(Player player, String decision, Consumer<Boolean> type) {
        tryYesNo(decision, type).open(player);
    }

    public static SmartInventory tryYesNo(String decision, Consumer<Boolean> type) {
        return SmartInventory
                // Initialize the builder
                .builder()
                // Set the manager to the Artemis one instead of the default plugin one
                .manager(BukkitArtemis.INSTANCE.getInventoryManager())
                // Set the id to the Check's category type
                .id("Decision #" + Math.random())
                // Set the provider to itself
                .provider(new YesNoController(decision, type))
                // Set the size to 4 rows ad=nd 9 columns
                .size(3, 9)
                // Set the title
                .title(ChatColor.AQUA + "Decision maker :)")
                // Disable closing
                .closeable(false)
                // Build the GUI
                .build();
    }

    private final String decision;
    private final Consumer<Boolean> executor;

    public YesNoController(String decision, Consumer<Boolean> executor) {
        this.decision = decision;
        this.executor = executor;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.set(1, 2, ClickableItem.of(new ItemBuilder(NMSMaterial.RED_WOOL).name("&c&lNO").build(), e -> {
            executor.accept(false);
        }));

        final ItemBuilder itemBuilder = new ItemBuilder(NMSMaterial.PAPER).name("&6Information");

        StringBuilder builder = new StringBuilder();
        builder.append("&7&o");
        final char[] deci = decision.toCharArray();
        for (int i = 0; i < deci.length; i++) {
            builder.append(deci[i]);

            if (i % 20 == 0 && i > 0) {
                itemBuilder.addLore(builder.toString());
                builder = new StringBuilder();
                builder.append("&7&o");
            }
        }

        itemBuilder.addLore(builder.toString());

        contents.set(1, 4, ClickableItem.empty(itemBuilder.build()));

        contents.set(1, 6, ClickableItem.of(new ItemBuilder(NMSMaterial.GREEN_WOOL).name("&a&lYES").build(), e -> {
            executor.accept(true);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
