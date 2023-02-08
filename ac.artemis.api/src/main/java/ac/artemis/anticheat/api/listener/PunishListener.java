package ac.artemis.anticheat.api.listener;

import ac.artemis.anticheat.api.alert.Punishment;

public interface PunishListener {
    /**
     * Will listen to punishments and receive the such. These are called in a synchronous fashion hence
     * are cancellable.
     * @param alert Ban that has to be received
     */
    void receive(Punishment alert);
}
