package ac.artemis.core.v5.features.logs;

import java.util.List;
import java.util.UUID;

public interface PanelLogsFetcher {
    List<FetchedLog> getLogs(final UUID uuid);
}
