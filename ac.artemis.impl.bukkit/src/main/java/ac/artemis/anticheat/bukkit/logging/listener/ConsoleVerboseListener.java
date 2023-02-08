package ac.artemis.anticheat.bukkit.logging.listener;

import ac.artemis.anticheat.api.alert.Alert;
import ac.artemis.anticheat.api.listener.VerboseListener;

public class ConsoleVerboseListener implements VerboseListener {
    /**
     * Will listen to every kind of verbose. This can be constructed as a Player, a discord or whatnot.
     *
     * @param alert Alert that has to be received
     */
    @Override
    public void receive(final Alert alert) {
        System.out.println(alert.toMinecraftMessage());
    }
}
