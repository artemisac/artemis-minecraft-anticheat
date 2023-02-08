package ac.artemis.core.v4.notification.provider;

import ac.artemis.core.Artemis;
import ac.artemis.core.v5.features.logs.FetchedLog;
import ac.artemis.core.v5.features.logs.PanelLogsFetcher;
import ac.artemis.core.v5.features.logs.PanelLogsFetcherFactory;
import ac.artemis.core.v5.logging.model.Ban;
import ac.artemis.core.v5.logging.model.Log;
import ac.artemis.core.v5.features.panel.PanelBanner;
import ac.artemis.core.v5.features.panel.PanelBannerFactory;
import ac.artemis.core.v5.features.panel.PanelLogger;
import ac.artemis.core.v5.features.panel.PanelLoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BackendNotificationProvider implements NotificationProvider {
    private final PanelLogger panelLogger;
    private final PanelBanner panelBanner;
    private final PanelLogsFetcher panelLogsFetcher;
    private static final int count = 2500;
    private final List<Log> logs = new ArrayList<>();

    public BackendNotificationProvider() {
        final File dir = new File(Artemis.v().getPlugin().getDataFolder(), "debug");
        if (!dir.exists()) {
            dir.getParentFile().mkdirs();
            dir.getParentFile().mkdir();
            dir.mkdir();
        }

        final File ban = new File(Artemis.v().getPlugin().getDataFolder(), "bans");
        if (!ban.exists()) {
            ban.getParentFile().mkdirs();
            ban.getParentFile().mkdir();
            ban.mkdir();
        }

        this.panelLogger = new PanelLoggerFactory()
                .setDumpDirectory(dir)
                .build();
        this.panelBanner = new PanelBannerFactory()
                .setDumpDirectory(ban)
                .build();

        this.panelLogsFetcher = new PanelLogsFetcherFactory()
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(this::push));
    }

    @Override
    public void provide(Log log) {
        logs.add(log);

        if (logs.size() % count == 0 && logs.size() > 0) {
            push();
        }
    }

    @Override
    public void provide(Ban ban) {
        panelBanner .log(ban);
    }

    @Override
    public void push() {
        panelLogger.log(logs);
        logs.clear();
    }

    @Override
    public List<FetchedLog> getLogs(final UUID uuid) {
        return panelLogsFetcher.getLogs(uuid);
    }
}
