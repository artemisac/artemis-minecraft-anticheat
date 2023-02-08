package ac.artemis.core.v4.notification;

import ac.artemis.anticheat.api.listener.PunishListener;
import ac.artemis.anticheat.api.listener.VerboseListener;
import ac.artemis.packet.minecraft.config.Configuration;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.data.utils.StaffEnums;
import ac.artemis.core.v4.notification.provider.BackendNotificationProvider;
import ac.artemis.core.v4.notification.provider.NotificationProvider;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v5.logging.model.Ban;
import ac.artemis.core.v5.logging.model.Log;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ghast
 * @since 05/12/2020
 * Artemis © 2020
 */

@Getter
public class NotificationManager extends Manager {
    public NotificationManager(Artemis plugin) {
        super(plugin, "Notifications [Manager]");
    }
    private final Set<PunishListener> punishListeners = new HashSet<>();
    private final Set<VerboseListener> verboseListeners = new HashSet<>();
    private final NotificationProvider notificationProvider = new BackendNotificationProvider();

    @Override
    public void init(InitializeAction initializeAction) {
        //Server.v().getOnlinePlayers().forEach(this::checkAlerts);
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {
        //this.toAlert.clear();
    }

    public void sendAlert(StaffEnums.StaffAlerts type, Log log) {
        for (VerboseListener verboseListener : verboseListeners) {
            verboseListener.receive(log);

            if (log.isCancelled())
                break;
        }
    }

    public void executeBan(Ban profile) {
        final Configuration settings = ConfigManager.getSettings();

        punishListeners.forEach(e -> e.receive(profile));

        Artemis.v().getApi().getBanManager().getType().getBanFeature().logProfile(profile);
    }

    public void addVerboseListener(final VerboseListener verboseListener) {
        verboseListeners.add(verboseListener);
    }

    public void removeVerboseListener(final VerboseListener verboseListener) {
        verboseListeners.remove(verboseListener);
    }
}
