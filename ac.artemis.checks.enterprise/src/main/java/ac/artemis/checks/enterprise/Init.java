package ac.artemis.checks.enterprise;

import ac.artemis.anticheat.api.check.CheckInit;
import ac.artemis.anticheat.api.check.CheckReload;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.language.Lang;
import lombok.SneakyThrows;

/**
 * @author Ghast
 * @since 19/11/2020
 * Artemis © 2020
 */
public class Init implements CheckInit {
    public Init() {
        init();
    }

    private Reload reload;

    @SneakyThrows
    public void init() {
        while (ConfigManager.getChecks() == null) {
            Chat.sendConsoleMessage(Lang.MSG_CONSOLE_BOOT_CHECK_ATTEMPT);
            Thread.sleep(100);
        }

        Artemis.v().getApi().getPlayerDataManager().getCheckManager().add(ArtemisEnterprise.class);
        this.reload = new Reload();
        Chat.sendConsoleMessage(Lang.MSG_CONSOLE_BOOT_CHECK_SUCCESS_ENTERPRISE);
    }

    public void disinit() {
        reload.cancel();
    }

    @Override
    public CheckReload getReload() {
        return reload;
    }
}
