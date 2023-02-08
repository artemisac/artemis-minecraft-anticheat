package ac.artemis.checks.enterprise.protocol;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientSteerVehicle;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientVehicleMove;

import java.util.Arrays;

/**
 * @author Ghast
 * @since 29/11/2020
 * Artemis © 2020
 *
 * Basic vehicle spoofing bad packet. This is extremely dangerous against the prediction system.
 * I've only just added this. It could be prone to false positives; To investigate!
 */

@Check(type = Type.PROTOCOL, var = "D", threshold = 15)
public class ProtocolD extends ArtemisCheck implements PacketHandler, PacketExcludable {

    public ProtocolD(final PlayerData data, final CheckInformation info) {
        super(data, info);
        this.setCompatiblePackets(
                // Flying
                PacketPlayClientFlying.class,
                GPacketPlayClientLook.class,
                GPacketPlayClientPosition.class,
                GPacketPlayClientPositionLook.class,
                // Vehicle
                GPacketPlayClientSteerVehicle.class,
                GPacketPlayClientVehicleMove.class
        );
    }

    private int buffer;

    @Override
    public void handle(final GPacket packet) {
        final boolean flag = !data.prediction.isInVehicle() && !data.collision.isCollidesBoat()
                && data.movement.isInVehiclePacket();
        flag: {
            if (!flag) {
                this.buffer = Math.max(buffer - 1, 0);
                break flag;
            }

            if (buffer++ > 5) {
                this.log(
                        new Debug<>("buffer", buffer),
                        new Debug<>("allow", Arrays.toString(exemptTypes()))
                );
            }
        }
    }
}
