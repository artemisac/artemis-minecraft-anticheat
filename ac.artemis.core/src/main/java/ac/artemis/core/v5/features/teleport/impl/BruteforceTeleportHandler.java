package ac.artemis.core.v5.features.teleport.impl;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.TeleportHandler;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.ModifiableFlyingLocation;
import ac.artemis.core.v5.features.teleport.TeleportHandlerFeature;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientPositionLook;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerPosition;

import java.util.Deque;
import java.util.LinkedList;

public class BruteforceTeleportHandler implements TeleportHandlerFeature {
    private final Deque<GPacketPlayServerPosition> lastFlying = new LinkedList<>();

    @Override
    public boolean isTeleport(final PlayerData data, final GPacket flying) {
        /*
         * Simple null check
         */
        if (this.lastFlying.isEmpty() || this.lastFlying.peek() == null) {
            return false;
        }

        /*
         * Ensure the packet is of the correct type. Confirm teleport flying packets are exclusively
         * PacketPlayClientPositionLook.
         */
        final boolean type = flying instanceof PacketPlayClientPositionLook;
        if (!type)
            return false;

        /*
         * Ensure the packet is of the correct supplementary factor. Confirm teleport flying packets are
         * always on a false ground basis. View MCP
         */
        final PacketPlayClientPositionLook look = (PacketPlayClientPositionLook) flying;
        if (look.isOnGround())
            return false;

        /*
         * Ensure the location matches the last queued teleport. Spam teleports will henceforth not false
         * this. I am a genius.
         */
        final ModifiableFlyingLocation location = new ModifiableFlyingLocation(look);
        if (!location.equals(new ModifiableFlyingLocation(this.lastFlying.peek(), data.prediction.getMovement())))
            return false;

        data.checkManager.forEach(cm -> cm.getChecks()
                .values()
                .stream()
                .filter(check -> check instanceof TeleportHandler)
                .filter(ArtemisCheck::canCheck)
                .map(check -> (TeleportHandler) check)
                .forEach(check -> {
                    try {
                        check.handle(location);
                    } catch (final Exception ex){
                        ex.printStackTrace();
                    }
                })
        );


        data.prediction.setLastX(data.prediction.getX());
        data.prediction.setLastY(data.prediction.getY());
        data.prediction.setLastZ(data.prediction.getZ());
        data.prediction.setLastYaw(data.prediction.getYaw());
        data.prediction.setLastPitch(data.prediction.getPitch());

        data.prediction.setX(look.getX());
        data.prediction.setY(look.getY());
        data.prediction.setZ(look.getZ());
        data.prediction.setYaw(look.getYaw());
        data.prediction.setPitch(look.getPitch());


        data.movement.updateMovement(data.prediction.getMovement());
        data.movement.updatePosition(data.prediction.getMovement());

        //data.entity.setPositionAndRotation(look.getX(), look.getY(), look.getZ(), look.getYaw(), look.getPitch());
        data.entity.handlePacket(this.lastFlying.peek());
        data.entity.setPosition(
                data.prediction.getX(),
                data.prediction.getY(),
                data.prediction.getZ()
        );

        this.lastFlying.poll();
        return true;
    }

    @Override
    public void queueTeleport(final GPacketPlayServerPosition packet) {
        this.lastFlying.add(packet);
    }
}
