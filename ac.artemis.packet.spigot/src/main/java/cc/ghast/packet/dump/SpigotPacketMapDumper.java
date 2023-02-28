package cc.ghast.packet.dump;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface SpigotPacketMapDumper {
    void dump(final Consumer<String> consumer);
}
