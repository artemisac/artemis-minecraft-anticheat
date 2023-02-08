package ac.artemis.checks.regular.v2;

import ac.artemis.anticheat.api.check.CheckInit;
import ac.artemis.anticheat.api.check.CheckReload;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.language.Lang;
import lombok.SneakyThrows;

/**
 * @author Ghast
 * @since 10-May-20
 */
public class Init implements CheckInit {

    private Reload reload;

    public Init() {
        init();
    }

    @SneakyThrows
    public void init() {
        while (ConfigManager.getChecks() == null) {
            Chat.sendConsoleMessage(Lang.MSG_CONSOLE_BOOT_CHECK_ATTEMPT);
            Thread.sleep(100);
        }
        Artemis.v().getApi().getPlayerDataManager().getCheckManager().add(ArtemisCheckLoader.class);
        reload = new Reload();
        Chat.sendConsoleMessage(Lang.MSG_CONSOLE_BOOT_CHECK_SUCCESS_STANDARD);
    }


    public void disinit() {
        reload.cancel();
    }

    @Override
    public CheckReload getReload() {
        return reload;
    }
}
