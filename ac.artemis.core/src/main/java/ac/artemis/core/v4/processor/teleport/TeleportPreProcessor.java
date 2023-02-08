package ac.artemis.core.v4.processor.teleport;

import ac.artemis.core.v4.data.holders.ConnectionHolder;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.function.PacketAction;
import cc.ghast.packet.PacketAPI;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v4.utils.position.PlayerRotation;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityTeleport;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerPosition;

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

public class TeleportPreProcessor extends AbstractHandler {
    public TeleportPreProcessor(PlayerData data) {
        super("Teleport [0x01]", data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayServerPosition) {
            final GPacketPlayServerPosition pos = (GPacketPlayServerPosition) packet;


        }
        else if (packet instanceof GPacketPlayServerEntityTeleport) {
            final GPacketPlayServerEntityTeleport pos = (GPacketPlayServerEntityTeleport) packet;

            if (pos.getEntityId() != data.getPlayer().getEntityId()) {
                return;
            }

            data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                @Override
                public void pre() {
                    data.connection.setLastEntityTeleport(pos);
                    final PlayerMovement movement = new PlayerMovement(
                            data.getPlayer().getWorld(),
                            pos.getValueX(),
                            pos.getValueY(),
                            pos.getValueZ(),
                            pos.getValueYaw(),
                            pos.getValuePitch(),
                            pos.getTimestamp()
                    );

                    if (data.movement.getVelocity() != null)
                        data.movement.setLastVelocity(data.movement.getVelocity());

                    data.movement.setMovement(movement.setWorld(data.getPlayer().getWorld()));
                    data.movement.setLocation(movement.setWorld(data.getPlayer().getWorld()));

                    data.movement.setRotation(new PlayerRotation(data.getPlayer(),
                            movement.getYaw(),
                            movement.getPitch(),
                            movement.getTimestamp())
                    );

                    data.movement.setProcessedEntityTeleport(true);
                }

                @Override
                public void post() {
                    data.movement.setProcessedEntityTeleport(false);
                }
            });
        }
    }
}
