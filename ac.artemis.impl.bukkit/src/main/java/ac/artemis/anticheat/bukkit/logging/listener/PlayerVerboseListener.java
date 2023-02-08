package ac.artemis.anticheat.bukkit.logging.listener;

import ac.artemis.anticheat.api.alert.Alert;
import ac.artemis.anticheat.api.listener.VerboseListener;
import ac.artemis.anticheat.bukkit.logging.builder.MessageParameterBuilder;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.anticheat.bukkit.logging.builder.MessageParameter;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Objects;

public class PlayerVerboseListener implements VerboseListener {
    private static final MessageParameter hoverableMessageParameter = MessageParameterBuilder.buildHoverableParameter();
    private final Player player;

    public PlayerVerboseListener(final Player player) {
        this.player = player;
    }

    /**
     * Will listen to every kind of verbose. This can be constructed as a Player, a discord or whatnot.
     *
     * @param alert Alert that has to be received
     */
    @Override
    public void receive(final Alert alert) {
        player.sendJsonMessage(TextComponent.toLegacyText(hoverableMessageParameter.change(alert, alert.toMinecraftMessage())));
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PlayerVerboseListener that = (PlayerVerboseListener) o;

        return player.equals(that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
