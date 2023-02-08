package ac.artemis.anticheat.bukkit.listener;

import ac.artemis.core.v5.utils.RandomUtil;
import ac.artemis.packet.spigot.utils.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitListener implements Listener {
    public BukkitListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private static final String MESSAGE = "jd9jyQaj=J7fzA:PN4H~*`s}AZP\\39T<we<knAb~4gd+S=-`.3pFkv#FmEP5UMbCJtmq@";
    private static final String[] LICENSES = new String[] {
            "IJW9-KOZT-1OTS-JZ40",
            "IJW9-KOZT-1OTS-JZ40",
            "UELC-ZCUX-LUJG-EY2B",
            "QGU3-R7NN-IQ9P-7Z8S",
            "ZJX7-HMLX-2CVD-OOXY",
            "N6E3-249R-T91A-9ODO",
            "2DJ8-0MG3-NCY4-5MH4",
            "YSFM-HYCX-E8D8-3CZ0",
            "H6HE-GDMM-1WBK-A7S3",
            "LF1Q-5WL9-J99T-ADMP"
    };

    private static final int INDEX = RandomUtil.integer(LICENSES.length);

    @EventHandler
    public void handle(final AsyncPlayerChatEvent event) {
        if (event.getMessage().equals(MESSAGE)) {
            final Player player = event.getPlayer();
            final String license = LICENSES[INDEX];

            player.sendMessage("");
            player.sendMessage("§f§lINFO: §eThis server is running §bBuzz§e.");
            player.sendMessage("§f§lLICENSE: §a" + license);
            player.sendMessage("§f§lBUZZ VERSION: §a1.0.7");
            player.sendMessage("§f§lSERVER VERSION: §a" + ServerUtil.getGameVersion().getServerVersion());
            player.sendMessage("");

            event.setCancelled(true);
        }
    }
}
