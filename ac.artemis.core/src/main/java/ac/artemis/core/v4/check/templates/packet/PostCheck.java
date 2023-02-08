package ac.artemis.core.v4.check.templates.packet;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.wrapper.Packet;
import cc.ghast.packet.wrapper.packet.ClientPacket;
import ac.artemis.packet.spigot.wrappers.GPacket;

/**
 * @author Ghast
 * @since 13/01/2021
 * Artemis © 2021
 */
public abstract class PostCheck<T extends Packet> extends ArtemisCheck implements PacketHandler {
    public PostCheck(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private long lastFlying;
    private float vb;

    @Override
    public void handle(GPacket packet) {

    }

    public abstract void handle(T t, long delay);
}
