package ac.artemis.core.v4.processor.velocity;

import ac.artemis.core.v4.check.TeleportHandler;
import ac.artemis.core.v4.check.VelocityHandler;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.data.holders.ConnectionHolder;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v4.utils.function.PacketAction;
import cc.ghast.packet.PacketAPI;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientTransaction;
import ac.artemis.core.v4.utils.position.Velocity;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityVelocity;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerTransaction;

import java.util.*;

public class VelocityPreProcessor extends AbstractHandler {

    public VelocityPreProcessor(final PlayerData data) {
        super("Velocity [0x01]", data);
    }

    @Override
    public void handle(final GPacket packet) {
        // If the packet is a velocity packet
        if (packet instanceof GPacketPlayServerEntityVelocity) {

            // Assign the packet
            final GPacketPlayServerEntityVelocity vel = (GPacketPlayServerEntityVelocity) packet;

            // Ensure the packet's entity id corresponds to the player's
            if (vel.getEntityId() == data.getPlayer().getEntityId()) {

                // Create a new velocity object
                final Velocity velocity = new Velocity(vel.getValueX(), vel.getValueY(), vel.getZ());

                data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        // If the previous velocity isn't null, assign it to this variable
                        if (data.movement.getVelocity() != null) data.movement.setLastVelocity(data.movement.getVelocity());

                        // Assign the velocity
                        data.movement.setVelocity(velocity);

                        // Set the boolean as processed
                        data.movement.setProcessedVelocity(true);
                    }

                    @Override
                    public void post() {
                        data.movement.setProcessedVelocity(false);
                    }
                });
            }
        }
    }
}
