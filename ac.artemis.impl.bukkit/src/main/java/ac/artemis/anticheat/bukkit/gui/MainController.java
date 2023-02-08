package ac.artemis.anticheat.bukkit.gui;

import ac.artemis.anticheat.bukkit.BukkitArtemis;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.anticheat.bukkit.entity.BukkitPlayer;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v4.utils.graphing.Pair;
import ac.artemis.anticheat.bukkit.utils.ItemBuilder;
import ac.artemis.core.v4.utils.spinner.Spinner;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MainController implements InventoryProvider {

    public static final SmartInventory inv = SmartInventory.builder()
            .id("main")
            .manager(BukkitArtemis.INSTANCE.getInventoryManager())
            .provider(new MainController())
            .size(3, 9)
            .title(ChatColor.AQUA + "Main GUI")
            .build();

    public static void openInventory(Player player) {
        inv.open(player);
    }
    private final Spinner<Pair<Integer, Integer>> integerSpinner = new Spinner<>(
            new Pair<>(0, 0),
            new Pair<>(0, 1),
            new Pair<>(0, 2),
            new Pair<>(0, 3),
            new Pair<>(0, 4),
            new Pair<>(0, 5),
            new Pair<>(0, 6),
            new Pair<>(0, 7),
            new Pair<>(0, 8),
            new Pair<>(1, 8),
            new Pair<>(2, 8),
            new Pair<>(2, 7),
            new Pair<>(2, 6),
            new Pair<>(2, 5),
            new Pair<>(2, 4),
            new Pair<>(2, 3),
            new Pair<>(2, 2),
            new Pair<>(2, 1),
            new Pair<>(2, 0),
            new Pair<>(1, 0)
    );

    private final Spinner<NMSMaterial> materialSpinner = new Spinner<>(
            NMSMaterial.PINK_STAINED_GLASS_PANE,
            NMSMaterial.RED_STAINED_GLASS_PANE,
            NMSMaterial.ORANGE_STAINED_GLASS_PANE,
            NMSMaterial.YELLOW_STAINED_GLASS_PANE,
            NMSMaterial.LIME_STAINED_GLASS_PANE,
            NMSMaterial.GREEN_STAINED_GLASS_PANE,
            NMSMaterial.LIGHT_BLUE_STAINED_GLASS_PANE,
            NMSMaterial.CYAN_STAINED_GLASS_PANE,
            NMSMaterial.BLUE_STAINED_GLASS_PANE,
            NMSMaterial.PURPLE_STAINED_GLASS_PANE,
            NMSMaterial.MAGENTA_STAINED_GLASS_PANE
    );

    private Pair<Integer, Integer> currentSpin = new Pair<>(0,0);
    private static final ClickableItem empty = ClickableItem.empty(new ItemBuilder(NMSMaterial.GRAY_STAINED_GLASS_PANE).name("&4").build());
    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(empty);
        contents.set(1, 2, ClickableItem.of(new ItemBuilder(NMSMaterial.MINECART)
                        .name("&aArtemis Anti-Cheat &b&lChecks")
                        .addLore("&7Clicking this item will allow you to modify",
                                "&7the checks currently enabled and",
                                "&7disabled on Artemis Anti-Cheat!",
                                "&7", "&aPlayer Checks \u2714",
                                "&aCombat Checks \u2714",
                                "&aMovement Checks \u2714",
                                "&7",
                                "&a\u276f "
                                        + BukkitArtemis.INSTANCE.getApi()
                                        .getPlayerDataManager()
                                        .getData(new BukkitPlayer(player)).getChecks().size() + " Checks Enabled").build(),
                e -> ChecksController.openInventory(player)));
        contents.set(1, 3, ClickableItem.of(new ItemBuilder(NMSMaterial.TNT)
                        .name("&aArtemis Anti-Cheat &b&lPunishments")
                        .addLore("&7Clicking this item will allow you to see",
                                "&7the full list of punishments",
                                "&7on the Artemis AntiCheat!",
                                "&7",
                                "&aExample punishments:",
                                "&7Example - KillAura V17",
                                "&7Example2 - Speed V12",
                                "&7Example3 - AimAssist V15")
                        .build(),
                e -> player.sendMessage(Chat.translate("&4Coming soon!")))
        );
        contents.set(1, 4, ClickableItem.of(new ItemBuilder(NMSMaterial.ANVIL)
                        .name("&aArtemis AntiCheat &b&lOptions")
                        .addLore("&7Clicking this item allows you to modify",
                                "&7the full list of options",
                                "&7on Artemis Anti-Cheat!")
                        .build(),
                e -> player.sendMessage(Chat.translate("&4Coming soon!")))
        );
        contents.set(1, 5, ClickableItem.of(new ItemBuilder(NMSMaterial.ITEM_FRAME)
                        .name("&aArtemis AntiCheat &b&lTheme")
                        .addLore("&7Clicking this item allows you to rotate",
                                "&7the full customizability options",
                                "&7on Artemis Anti-Cheat!",
                                "&7",
                                "&a\u276f Current: " + ThemeManager.getCurrentTheme().getId(),
                                "&7",
                                "&bExample: " + ThemeManager.getCurrentTheme().getPrefix()).build(),
                e -> player.sendMessage(Chat.translate("&4Coming soon!")))
        );
        contents.set(1, 6, ClickableItem.of(new ItemBuilder(NMSMaterial.ANVIL)
                        .name("&aArtemis Anticheat &b&l" + BukkitArtemis.INSTANCE.getPlugin().getVersion() + "!")
                        .addLore("&7You are running the latest",
                                "&7version of Artemis Anticheat \u2714",
                                "&4",
                                "&bLicense&7: &c" + ("Hidden"),
                                "&bRegistered to: &c" + ConfigManager.getSettings().getString("license.username"))
                        .build(),
                e -> {
                })
        );
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        int state = contents.property("state", 0);
        contents.setProperty("state", state + 1);

        if(state % 2 != 0)
            return;

        if (state > 100000) {
            contents.setProperty("state", 0);
        }
        contents.set(currentSpin.getX(), currentSpin.getY(), empty);
        currentSpin = integerSpinner.next();

        ClickableItem emptyButColored = ClickableItem.empty(
                new ItemBuilder(materialSpinner.next()).name("&4").build());
        contents.set(currentSpin.getX(), currentSpin.getY(), emptyButColored);
    }
}

