package ac.artemis.packet.spigot.utils;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.reflections.Reflection;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;


/**
 * @author Ghast
 * @since 31/08/2020
 * Artemis © 2020
 */

@UtilityClass
public class ServerUtil {
    public String color(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public void sendConsoleMessage(String s) {
        Bukkit.getConsoleSender().sendMessage(color("&r[&b&lPacket&r] &r" + s));
    }

    private final ProtocolVersion gameVersion = fetchGameVersion();

    public ProtocolVersion getGameVersion() {
        return gameVersion;
    }

    public boolean isLegacy() {
        return gameVersion.isBelow(ProtocolVersion.V1_8);
    }

    private ProtocolVersion fetchGameVersion() {
        for (ProtocolVersion version : ProtocolVersion.values()) {
            if (version.getServerVersion() != null && version.getServerVersion().equalsIgnoreCase(Reflection.VERSION))
                return version;

        }
        return ProtocolVersion.UNKNOWN;
    }
}
