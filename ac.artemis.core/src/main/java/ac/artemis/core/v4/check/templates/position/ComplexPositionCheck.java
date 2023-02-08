package ac.artemis.core.v4.check.templates.position;

import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;
import lombok.Getter;

/**
 * @author Ghast
 * @since 15-Mar-20
 */
public abstract class ComplexPositionCheck extends ArtemisCheck implements PacketHandler {

    public ComplexPositionCheck(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Getter
    private PacketPlayClientFlying packet;

    public double distanceH, distanceY;

    @Override
    public void handle(final GPacket packet) {
        if (data == null) return;
        if (packet instanceof PacketPlayClientFlying
                && ((PacketPlayClientFlying) packet).isPos()
                && !isNull(CheckType.POSITION)) {
            this.packet = (PacketPlayClientFlying) packet;
            this.distanceH = data.movement.lastLocation.distanceXZ(data.movement.location);
            this.distanceY = data.movement.lastLocation.distanceY(data.movement.location);
            handlePosition(data.movement.lastLocation, data.movement.location);
        }
    }

    public abstract void handlePosition(PlayerPosition from, PlayerPosition to);
}
