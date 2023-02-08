package ac.artemis.core.v4.check.templates.movement;

import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;
import lombok.Getter;

/**
 * @author Ghast
 * @since 18-Apr-20
 */
public abstract class ComplexMovementCheck extends ArtemisCheck implements PacketHandler {

    public ComplexMovementCheck(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Getter
    private PacketPlayClientFlying packet;

    @Override
    public void handle(final GPacket packet) {
        if (data == null) return;
        if (packet instanceof PacketPlayClientFlying
                && ((PacketPlayClientFlying) packet).isPos()
                && !isNullMovement()
        ) {
            this.packet = (PacketPlayClientFlying) packet;

            handleMovement(data.movement.lastMovement, data.movement.movement);
        }
    }

    public abstract void handleMovement(PlayerMovement from, PlayerMovement to);
}
