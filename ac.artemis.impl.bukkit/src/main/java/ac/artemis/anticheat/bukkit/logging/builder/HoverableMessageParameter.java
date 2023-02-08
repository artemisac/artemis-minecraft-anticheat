package ac.artemis.anticheat.bukkit.logging.builder;

import ac.artemis.anticheat.api.alert.Alert;
import ac.artemis.core.v4.utils.chat.Chat;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HoverableMessageParameter implements MessageParameter {
    @Override
    public TextComponent change(final Alert log, final String string) {
        final TextComponent textComponent = new TextComponent();
        textComponent.setText(string);
        if (!string.contains("<hover>") || !string.contains("</hover>"))
            return textComponent;


        final String var = string.split("<hover>")[1].split("</hover>")[0];
        final String replaced = string
                .replace("<hover>", "")
                .replace("</hover>", "")
                .replace(var, "");

        textComponent.setText(replaced);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(Chat.translate(var)).create()));

        final Player player = Bukkit.getPlayer(log.getUuid());

        if (player != null) {
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                    "/teleport " + Bukkit.getPlayer(log.getUuid()).getName()));
        }

        return textComponent;
    }
}
