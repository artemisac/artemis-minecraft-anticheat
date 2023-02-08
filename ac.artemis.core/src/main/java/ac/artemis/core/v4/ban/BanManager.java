package ac.artemis.core.v4.ban;

import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.features.ban.BanFeatureFactory;
import ac.artemis.core.v5.features.ban.BanType;
import ac.artemis.core.v5.threading.Threading;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * @author Ghast
 * @since 03-Apr-20
 */
@Getter
public class BanManager extends Manager {

    private BanType type = BanType.NONE;
    private ExecutorService service;

    public BanManager(Artemis plugin) {
        super(plugin, "Ban [Manager]");
    }

    @Override
    public void init(InitializeAction initializeAction) {
        this.service = Threading.getOrStartService("artemis-ban-service");
        plugin.getApi().getThreads().add(service);
        this.type = new BanFeatureFactory()
                .setName(ConfigManager.getSettings().getString("ban.type").toLowerCase(Locale.ROOT))
                .build();
        Chat.sendConsoleMessage("&r[&a✓&r] &7Started bans in &r" + type + "&b mode!");
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {
        this.service.shutdown();
        this.type = null;
    }
}
