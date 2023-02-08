package ac.artemis.core.v4.notification.provider;

import ac.artemis.core.v5.features.logs.FetchedLog;
import ac.artemis.core.v5.logging.model.Ban;
import ac.artemis.core.v5.logging.model.Log;

import java.util.List;
import java.util.UUID;

public interface NotificationProvider {
    void provide(Log log);
    void provide(Ban ban);
    void push();
    List<FetchedLog> getLogs(final UUID uuid);
}
