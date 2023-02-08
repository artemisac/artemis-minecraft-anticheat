package ac.artemis.core.v4.system;

import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;

/**
 * @author Ghast
 * @since 24-Apr-20
 */
public class SystemManager extends Manager {

    public SystemManager(Artemis plugin) {
        super(plugin, "System [Manager]");
    }

    @Override
    public void init(InitializeAction initializeAction) {
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {

    }

}
