package ac.artemis.core.v4.loader;

import ac.artemis.anticheat.api.check.CheckInit;
import ac.artemis.packet.minecraft.Server;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v4.utils.chat.Chat;
import lombok.Getter;

/**
 * @author Ghast
 * @since 23-Mar-20
 */

@Getter
public class LoaderManager extends Manager {

    public LoaderManager(Artemis plugin) {
        super(plugin, "Type [Manager]");
    }

    private CheckInit standard;
    private CheckInit enterprise;
    private boolean isLocal = true;

    @Override
    public void init(InitializeAction initializeAction) {
        try {
            standard = (CheckInit) Class.forName("ac.artemis.checks.regular.v2.Init").newInstance();
            Chat.sendConsoleMessage("&r[&a✓&r] &aHooked into checks api!");
        } catch (Exception e) {
            e.printStackTrace();
            if (!isLocal) Server.v().getPluginManager().kill(plugin.getPlugin());
        }

        try {
            enterprise = (CheckInit) Class.forName("ac.artemis.checks.enterprise.Init").newInstance();
            Chat.sendConsoleMessage("&r[&a✓&r] &aHooked into enterprise checks api!");
        } catch (Exception e) {
        }
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {

    }
}
