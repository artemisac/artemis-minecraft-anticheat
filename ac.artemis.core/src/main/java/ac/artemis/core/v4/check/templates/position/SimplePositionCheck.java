package ac.artemis.core.v4.check.templates.position;

import ac.artemis.core.v4.utils.position.SimplePosition;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientPosition;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;

/**
 * @author Ghast
 * @since 15-Mar-20
 */
public abstract class SimplePositionCheck extends ArtemisCheck implements PacketHandler {

    public SimplePositionCheck(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (data == null) return;
        if (packet instanceof PacketPlayClientFlying) {
            if (((PacketPlayClientFlying) packet) instanceof PacketPlayClientPosition && !isNull(CheckType.POSITION))
                handlePosition(data.movement.lastLocation,
                        data.movement.location);
        }
    }

    public abstract void handlePosition(SimplePosition from, SimplePosition to);
}
