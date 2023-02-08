package ac.artemis.checks.enterprise.protocol;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientSteerVehicle;

/**
 * @author Ghast
 * @since 29/11/2020
 * Artemis © 2020
 *
 * Basic vehicle spoofing bad packet. This is extremely dangerous against the prediction system.
 * I've only just added this. It could be prone to false positives; To investigate!
 */
@Check(type = Type.PROTOCOL, var = "DB", threshold = 1)
public class ProtocolD2 extends ArtemisCheck implements PacketHandler, PacketExcludable {

    public ProtocolD2(final PlayerData data, final CheckInformation info) {
        super(data, info);
        this.setCompatiblePackets(GPacketPlayClientSteerVehicle.class);
    }

    @Override
    public void handle(GPacket packet) {
        GPacketPlayClientSteerVehicle steer = (GPacketPlayClientSteerVehicle) packet;

        final boolean insideVehicle = data.getPlayer().isInsideVehicle();
        final boolean exitingVehicle = steer.isSneaking();

        flag: {
            final boolean flag = exitingVehicle && !insideVehicle;

            if (!flag) {
                break flag;
            }

            this.log();
        }

        this.debug("inside=%s exiting=%s", insideVehicle, exitingVehicle);
    }
}
