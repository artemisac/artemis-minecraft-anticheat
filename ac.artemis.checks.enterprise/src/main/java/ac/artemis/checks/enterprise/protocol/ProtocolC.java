package ac.artemis.checks.enterprise.protocol;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientEntityAction;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientSteerVehicle;

/**
 * @author Ghast
 * @since 29/11/2020
 * Artemis © 2020
 *
 * Theory of this goes with the following:
 * 1: flying -> obligatory
 * 2: steer -> optional
 * 3: sneak -> optional
 * 4: sprint -> optional
 */
@Check(type = Type.PROTOCOL, var = "C", threshold = 1)
public class ProtocolC extends ArtemisCheck implements PacketHandler, PacketExcludable {
    public ProtocolC(PlayerData data, CheckInformation info) {
        super(data, info);
        this.setCompatiblePackets(
                PacketPlayClientFlying.class,
                GPacketPlayClientSteerVehicle.class,
                GPacketPlayClientEntityAction.class
        );
    }

    private int stage = 0, entityAction = 0;

    @Override
    public void handle(GPacket packet) {
        final boolean onEntity = data.getPlayer().isInsideVehicle();

        switch (stage) {
            case 0: {
                final boolean validPacket = packet instanceof PacketPlayClientFlying;

                if (!validPacket && onEntity) log("Behavior:Packet/Flying.0!Order | type=%s", packet.getClass().getSimpleName());

                break;
            }

            case 1: {
                final boolean validPacket = packet instanceof GPacketPlayClientSteerVehicle;

                if (!validPacket && onEntity) log("Behavior:Packet/Vehicle.1!Order | type=%s", packet.getClass().getSimpleName());
                break;
            }

            case 2: {
                final boolean validPacket = packet instanceof GPacketPlayClientEntityAction;

                // Theory is this packet **can** be sent. If it isn't though we can just fucken ignore it
                if (!validPacket) break;

                // Invalid order
                final boolean invalidOrder = this.entityAction > 0
                        && ((GPacketPlayClientEntityAction) packet).getAction().name().contains("SNEAKING");

                if (invalidOrder)
                    this.log("Behavior:Packet/EntityAction.2!Order | type=%s", packet.getClass().getSimpleName());

                // Invalid count
                final boolean invalidCount = this.entityAction > 1;

                if (invalidCount)
                    this.log("Behavior:Packet/EntityAction.2!Count | type=%s", packet.getClass().getSimpleName());

                this.entityAction++;
                return;
            }
        }

        if (++stage > 2) this.stage = 0;
    }
}
