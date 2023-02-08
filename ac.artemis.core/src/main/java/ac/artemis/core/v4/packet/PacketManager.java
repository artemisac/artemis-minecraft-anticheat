package ac.artemis.core.v4.packet;

import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.packet.PacketListener;
import cc.ghast.packet.PacketAPI;

/**
 * @author Ghast
 * @since 17/08/2020
 * Artemis © 2020
 */
public class PacketManager extends Manager {

    private PacketListener listener;
    public PacketManager(Artemis plugin) {
        super(plugin, "Packet [Manager]");
    }

    @Override
    public void init(InitializeAction initializeAction) {
        this.listener = new ArtemisListener();
        PacketAPI.addListener(listener);
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {
        this.listener = null;
    }
}
