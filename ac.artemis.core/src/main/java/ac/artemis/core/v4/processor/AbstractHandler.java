package ac.artemis.core.v4.processor;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;

/**
 * @author Ghast
 * @since 06-Mar-20
 */
public abstract class AbstractHandler {

    private final String name;
    public PlayerData data;

    public AbstractHandler(final String name, PlayerData data) {
        this.name = name;
        this.data = data;
    }

    public abstract void handle(GPacket packet);

    public String getName() {
        return name;
    }
}
