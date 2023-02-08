package ac.artemis.anticheat.bukkit.logging.builder;

import ac.artemis.anticheat.api.alert.Alert;
import net.md_5.bungee.api.chat.TextComponent;

public interface MessageParameter {
    TextComponent change(final Alert log, final String string);
}
