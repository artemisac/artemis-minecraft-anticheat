package ac.artemis.core.v4.nms;

import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.nms.minecraft.INMS;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.packet.spigot.utils.ServerUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ghast
 * @since 23-Mar-20
 */

@Getter
public class NMSManager extends Manager {

    @Getter @Setter private static INMS inms;
    public NMSManager(Artemis plugin) {
        super(plugin, "NMS [Manager]");
    }

    @Override
    public void init(InitializeAction initializeAction) {
        if (inms != null) {
            Chat.sendConsoleMessage("&r[&a✓&r] &7Hooked into " + ServerUtil.getGameVersion().name());
        } else {
            Chat.sendConsoleMessage("&7[&b&lArtemis&7] &cFailed to hook into NMS! Disabling NMS checks...");
        }
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {

    }

    @Deprecated
    public boolean isNmsHooked() {
        return inms != null;
    }
}
