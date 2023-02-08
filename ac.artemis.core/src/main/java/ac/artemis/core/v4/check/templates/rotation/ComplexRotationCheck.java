package ac.artemis.core.v4.check.templates.rotation;

import ac.artemis.core.v4.utils.position.PlayerRotation;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;

/**
 * @author Ghast
 * @since 15-Mar-20
 */
public abstract class ComplexRotationCheck  extends ArtemisCheck implements PacketHandler {

    public ComplexRotationCheck(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (data == null) return;
        if (packet instanceof PacketPlayClientFlying) {
            //System.out.println(":X");
            if (((PacketPlayClientFlying) packet).isLook() && !isNull(CheckType.ROTATION))
                handleRotation(data.movement.lastRotation, data.movement.rotation);
        }
    }

    public abstract void handleRotation(PlayerRotation from, PlayerRotation to);
}
