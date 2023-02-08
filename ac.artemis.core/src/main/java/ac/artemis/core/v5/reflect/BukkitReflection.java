package ac.artemis.core.v5.reflect;

import ac.artemis.packet.minecraft.console.ConsoleReader;
import ac.artemis.packet.minecraft.entity.impl.Player;

public interface BukkitReflection {
    int getPing(final Player player);
    ConsoleReader getReader();
}
