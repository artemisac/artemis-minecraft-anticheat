package ac.artemis.anticheat.api.listener;

import ac.artemis.anticheat.api.alert.Alert;

public interface VerboseListener {
    /**
     * Will listen to every kind of verbose. This can be constructed as a Player,
     * a discord or whatnot.
     * @param alert Alert that has to be received
     */
    void receive(Alert alert);
}
