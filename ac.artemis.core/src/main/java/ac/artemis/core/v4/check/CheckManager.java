package ac.artemis.core.v4.check;

import ac.artemis.anticheat.api.check.CheckInfo;
import ac.artemis.anticheat.api.check.CheckInit;
import ac.artemis.anticheat.api.check.CheckReload;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;

import java.util.*;

/**
 * @author Ghast
 * @since 25/03/2021
 * Artemis © 2021
 */
public class CheckManager extends Manager {
    public CheckManager(Artemis plugin) {
        super(plugin, "Check [Manager]");
    }

    private CheckReload basic;
    private CheckReload enterprise;
    private Map<String, CheckInfo> cache;

    @Override
    public void init(InitializeAction initializeAction) {
        final CheckInit standard = plugin.getApi().getLoaderManager().getStandard();

        if (standard != null) {
            this.basic = standard.getReload();
        }

        final CheckInit enter = plugin.getApi().getLoaderManager().getEnterprise();

        if (enter != null) {
            this.enterprise = enter.getReload();
        }

        this.cache = new HashMap<>();

        for (CheckInfo check : basic.getChecks()) {
            cache.put(check.getType().name().toLowerCase(Locale.ROOT) + "_" + check.getVar().toLowerCase(Locale.ROOT), check);
        }
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {

    }

    public void reloadChecks() {
        if (basic != null) {
            basic.reloadAssets();
        }
        if (enterprise != null) enterprise.reloadAssets();
    }

    public Set<CheckInfo> getInfos() {
        final Set<CheckInfo> infos = new HashSet<>();

        if (basic != null) {
            infos.addAll(basic.getChecks());
        }

        if (enterprise != null) {
            infos.addAll(enterprise.getChecks());
        }

        return infos;
    }

    public CheckInfo getInfo(final String cachedName) {
        return cache.get(cachedName.toLowerCase(Locale.ROOT));
    }
}
