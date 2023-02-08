package ac.artemis.core.v4.processor.movement;

import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v4.utils.graphing.Pair;
import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.core.v4.utils.position.PlayerRotation;
import ac.artemis.core.v4.utils.position.SimplePosition;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.client.PacketPlayClientLook;
import ac.artemis.packet.wrapper.client.PacketPlayClientPosition;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientSteerVehicle;

/**
 * @author Ghast
 * @since 06-Mar-20
 */
public class MovementPreProcessor extends AbstractHandler {

    public MovementPreProcessor(final PlayerData data) {
        super("Movement [0x01]", data);
    }

    @Override
    public void handle(final GPacket packet) {
        final long now = System.currentTimeMillis();

        if (packet instanceof PacketPlayClientFlying) {
            final PacketPlayClientFlying fly = (PacketPlayClientFlying) packet;
            final Player player = data.getPlayer();

            data.movement.setLastFlyingPacket(packet.getTimestamp());
            // Drag down ticks:
            data.movement.setDeathTicks(dcrBy1(data.movement.deathTicks));
            data.movement.setTeleportTicks(dcrBy1(data.movement.teleportTicks));
            data.movement.setRespawnTicks(dcrBy1(data.movement.respawnTicks));


            if (TimeUtil.elapsed(data.movement.getLastSteerPacket(), 100L)) {
                data.movement.setInVehiclePacket(false);
            }

            data.movement.setInVehicleFake(data.getPlayer().isInsideVehicle());
            data.collision.setHasLeftVehicle(data.collision.isCollidesBoat() && !data.prediction.isInVehicle());
            data.user.setOnFakeGround(fly.isOnGround());

            // HANDLE ALL MOVEMENT HERE!
            if (fly.isPos()) {
                final PacketPlayClientPosition wrapper = (PacketPlayClientPosition) fly;
                final PlayerPosition pos = new PlayerPosition(player, wrapper.getX(), wrapper.getY(), wrapper.getZ(), packet.getTimestamp());

                // Updating positions
                if (data.movement.location != null) data.movement.setLastLocation(data.movement.location);
                data.movement.setLocation(pos);
                data.movement.playerPositions.add(pos);

                PlayerMovement movement = null;
                if (!fly.isLook()) {
                    if (data.movement.getRotation() != null) {
                        movement = new PlayerMovement(player, wrapper.getX(), wrapper.getY(), wrapper.getZ(),
                                data.movement.getRotation().getYaw(), data.movement.getRotation().getPitch(),
                                packet.getTimestamp());
                    } else {
                        movement = new PlayerMovement(player, wrapper.getX(), wrapper.getY(), wrapper.getZ(),
                                data.prediction.getYaw(), data.prediction.getPitch(),
                                packet.getTimestamp());
                    }

                } else {
                    final PacketPlayClientLook look = (PacketPlayClientLook) fly;
                    movement = new PlayerMovement(player, wrapper.getX(), wrapper.getY(), wrapper.getZ(), look.getYaw() % 360, look.getPitch(),
                            packet.getTimestamp());
                }



                if (System.currentTimeMillis() - data.user.lastFlyingPacket > 110L) {
                    data.movement.setLastDelayedFlyingPacket(now);
                }


                if (fly.isOnGround()) data.user.setLastFakeGround(packet.getTimestamp());
                data.movement.setFakeGroundTicks(fly.isOnGround() ? data.movement.getFakeGroundTicks() + 1 : 0);
                data.user.setBox(new BoundingBox(pos.toBounding(), now));

                if (data.movement.lastLocation != null) {
                    final PlayerPosition from = data.movement.lastLocation;

                    final double deltaX = Math.abs(from.getX() - pos.getX());
                    final double deltaY = Math.abs(from.getY() - pos.getY());
                    final double deltaZ = Math.abs(from.getZ() - pos.getZ());

                    final double deltaH = Math.hypot(deltaX, deltaZ);
                    final double deltaV = pos.getY() - from.getY();

                    data.movement.setPreviousDeltaH(data.movement.deltaH);
                    data.movement.setPreviousDeltaV(data.movement.deltaV);

                    data.movement.setDeltaH(deltaH);
                    data.movement.setDeltaV(deltaY);

                    // Time based positions
                    data.movement.posNew.add(
                            new Pair<>(
                                    new SimplePosition(pos.getX(), pos.getY(), pos.getZ()),
                                    deltaH + Math.abs(deltaY))
                    );

                    if (deltaV > 0) data.movement.setLastJump(now);
                    if (deltaV < 0) data.movement.setSlimeVelocity(false);
                    if (System.currentTimeMillis() - data.movement.lastLocation.getTimestamp() > 110L) {
                        data.movement.setLastDelayedMovePacket(now);
                    }
                    if (deltaH == 0 && deltaY == 0) data.movement.setStandTicks(data.movement.standTicks + 1);
                    else data.movement.setStandTicks(0);
                }
                // Update contacting stuff

            }

            // HANDLE ALL ROTATION HERE!
            if (fly.isLook()) {
                final PacketPlayClientLook wrapper = (PacketPlayClientLook) fly;
                final PlayerRotation rot = new PlayerRotation(player, wrapper.getYaw(), wrapper.getPitch(), now);
                if (data.movement.rotation != null) data.movement.setLastRotation(data.movement.rotation);
                data.movement.setRotation(rot);
            }

            if (data.movement.movement != null) {
                data.movement.setLastMovement(data.movement.movement);
            }
            if (data.movement.location != null && data.movement.rotation != null) {
                data.movement.setMovement(new PlayerMovement(data.movement.location, data.movement.rotation));
            }

            data.movement.getPreviousPlayerPositions().add(data.movement.movement);

            if (fly.isOnGround() && !data.movement.isUnderBlock()) {
                if (data.movement.velocity != null) {
                    data.movement.setLastVelocity(data.movement.velocity);
                    data.movement.velocity.setHorizontal(0);
                    data.movement.velocity.setVertical(0);
                    data.movement.velocity.setX(0);
                    data.movement.velocity.setY(0);
                    data.movement.velocity.setZ(0);
                }
            }

            if (data.movement.getLastLocation() != null && data.movement.getLocation() != null) {
                final boolean invalidVer = data.getExemptManager().isExempt(ExemptType.NFPGAY);

                if (fly.isPos()) {
                    data.movement.setMoving((data.movement.getLastLocation().distance(data.movement.getLocation()) > 1E-4) || invalidVer);
                } else if (!fly.isPos()) {
                    data.movement.setMoving(false);
                }
            }

            data.user.setLastFlyingPacket(now);


        } /*else if (packet instanceof GPacketPlayServerVelocity) {
            GPacketPlayServerVelocity vel = (GPacketPlayServerVelocity) packet;

            Velocity velocity = new Velocity(vel.getX(), vel.getY(), vel.getZ());

            if (vel.getId() == processor.getPlayer().getEntityId()) {
                /*if (processor.movement.getVelocity() != null) processor.movement.setLastVelocity(processor.movement.getVelocity());
                processor.movement.setVelocity(velocity);
                processor.movement.setLastVelocityPacket(System.currentTimeMillis());
                processor.movement.velocities.add(velocity);
            }

        }*//*-else if (packet instanceof GPacketPlayServerPosition) {
            GPacketPlayServerPosition out = (GPacketPlayServerPosition) packet;

            PlayerPosition position = new PlayerPosition(data.getPlayer(), out.getX(), out.getY(), out.getZ(),
                    packet.getTimestamp());

            if (data.movement.getLastLocation() != null && data.movement.getLocation() != null) {
                if (position.distanceXZ(data.movement.getLastLocation()) < 0.5
                        || position.distanceXZ(data.movement.getLocation()) < 0.5) {
                    data.movement.setMoveCancel(true, packet.getTimestamp());
                }

                if (data.movement.getLastLocation().getWorld() != data.movement.getLocation().getWorld()) {
                    data.movement.setTeleportTicks(50);
                } else {
                    data.movement.setTeleportTicks(20);
                }


            }


            data.movement.setLastTeleportOutbound(packet.getTimestamp());
            data.user.setLastTp(System.currentTimeMillis());

        }*/ else if (packet instanceof GPacketPlayClientSteerVehicle) {
            data.movement.setLastSteerPacket(packet.getTimestamp());
            data.movement.setInVehiclePacket(true);
        }

        data.timing.movementTiming.addTime(now, System.currentTimeMillis());
    }

    private int dcrBy1(final int i) {
        return Math.max(0, i - 1);
    }

}
