package ac.artemis.core.v4.api;

import ac.artemis.core.Artemis;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public abstract class Manager {

    public final Artemis plugin;
    private final String name;

    public Manager(Artemis plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    /**
     * Method called when plugin is initialized
     *
     * @param action Initialize Action
     */
    public abstract void init(InitializeAction action) throws NoSuchAlgorithmException, IOException;

    /**
     * Method called when plugin is shutdown
     *
     * @param action Disinitialize Action
     */
    public abstract void disinit(ShutdownAction action);

    public String getName() {
        return name;
    }
}
