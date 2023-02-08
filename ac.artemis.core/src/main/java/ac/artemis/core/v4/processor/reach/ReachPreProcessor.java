package ac.artemis.core.v4.processor.reach;

import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.check.FastProcessHandler;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.data.holders.ConnectionHolder;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v4.utils.function.PacketAction;
import ac.artemis.core.v4.utils.reach.ReachEntity;
import ac.artemis.core.v4.utils.reach.ReachModal;
import ac.artemis.core.v5.utils.ServerUtil;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.raytrace.MovingPoint;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.nms.MathHelper;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntity;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityDestroy;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityTeleport;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerSpawnNamedEntity;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class ReachPreProcessor extends AbstractHandler implements FastProcessHandler {
    public ReachPreProcessor(final PlayerData data) {
        super("Reach [0x01]", data);
    }

    private final double multiplier = data.getVersion().isOrAbove(ProtocolVersion.V1_9)
            ? 4096.0D
            : 32.D;
    private static final Timer timer = new Timer();
    private final Map<Integer, Integer> attempts = new WeakHashMap<>();
    private long lastPreTick, lastPostTick;

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            final boolean attacking = data.reach.isHasAttacked();

            final boolean clicking = data.reach.isHasClicked()
                    && data.reach.getLastAttackedEntity() != null;

            final Point lastPosition = data.prediction.getLastLocation();
            final float eyeHeight = data.prediction.getEyeHeight();

            final Point eyePos = lastPosition.addVector(0, eyeHeight, 0);
            Point resultEyeRot = null;

            final ReachEntity reachEntity = data.reach.getLastAttackedEntity();
            final ReachModal reachModal;

            /*
             * To run our reach check we have a couple of conditions: First of all, we only want to
             * handle attacks. Second of all, we will not handle entities not being the last processed
             * attacked player. Finally, we wish to exclusively keep it to the last attacked player.
             */
            if ((attacking || clicking) && reachEntity != null) {
                /*
                 * Here we set our atomic references as object wrappers. We could create our own object
                 * wrappers for lighter objects, but honestly I seriously cannot be asked. Sorry!
                 */
                final AtomicReference<Double> min = new AtomicReference<>(Double.MAX_VALUE);
                final AtomicReference<ReachEntity.ReachPosition> minTick = new AtomicReference<>();
                final AtomicReference<Point> minHit = new AtomicReference<>();

                /*
                 * Here we iterate the cached reach position list at the time of the attack. We have
                 * to go through every single scenario or else we'll end up having some false positives
                 * when the transaction falls on a different tick from the relative move. Either it's
                 * this, either it's a gamble.
                 */
                for (final ReachEntity.ReachPosition pair : new HashSet<>(reachEntity.getReachPositions())) {
                    /*
                     * You might be thinking what is hv? Well hv stands for hit vec we are doing this iteration as in
                     * vanilla Minecraft 1.8 our hit vec is 1 tick behind causing us to ray trace with the rotations
                     * 1 tick behind. Some pvp clients or mods like OptiFine and lunar client fix these issues, so we
                     * are going to have to account for these scenarios with this simple iteration we are doing.
                     */
                    for (int hv = 0; hv < 2; hv++) {
                        final float pitch = hv == 1 ? data.prediction.getPitch() : data.prediction.getLastPitch();
                        final float yaw = hv == 1 ? data.prediction.getYaw() : data.prediction.getLastYaw();

                        final Point eyeRot = this.getVectorForRotation(pitch, yaw);

                        /*
                         * As per our previous specifications, here we handle the scaled eye direction. We take the
                         * vector of our eye position and add a 6 block radius reach multiplied by the direction
                         * factor vector (eyeDir).
                         */
                        final Point scaledEyeDir = eyePos.addVector(
                                eyeRot.getX() * 6.0D,
                                eyeRot.getY() * 6.0D,
                                eyeRot.getZ() * 6.0D
                        );

                        /*
                         * As per minecraft standards, we grab the bounding box and expand it by 0.1F, giving us
                         * the opponent player's hit box. This does not vary much from version to version.
                         * Following the such, we calculate the intercept from the player's eye position with our
                         * trusty old scaled eye direction. This determines whether our vector intercepts with
                         * the bounding box or not.
                         */
                        final BoundingBox bb = pair.getEntityBoundingBox().cloneBB().expand(0.1F, 0.1F, 0.1F);
                        final MovingPoint mp = bb.calculateIntercept(eyePos, scaledEyeDir);

                        if (mp != null) {
                            /*
                             * Hurray, our aim does intercept. That's quite nifty. Anyhow, we calculate the sqrt-ed
                             * distance from our eyes to the hit vector. If it happens to be less than our minimum,
                             * we replace the minimum's values.
                             */
                            final double range = mp.hitVec.distanceTo(eyePos);

                            if (range < min.get()) {
                                min.set(range);
                                minTick.set(pair);
                                minHit.set(mp.hitVec);

                                resultEyeRot = eyeRot;
                            }
                        }

                        /*
                         * We're already saturating a lot of heap space and we've established this scenario to be
                         * improbable or straight up impossible. To save memory and optimize the system, we simply
                         * remove this reach position. This *can* become a source of false positives, though the
                         * likeliness of such happening is beyond low.
                         */
                        else if (reachEntity.getReachPositions().size() > 16) {
                            reachEntity.getReachPositions().remove(pair);
                        }
                    }
                }
                /*
                 * Here we grab the important data from the smallest iteration: distance, tick and the size
                 * of the reach positions. This is shared between both the reach and hitbox check, hence
                 * should be kept outside their respective labels
                 */
                final ReachEntity.ReachPosition reachPosition = minTick.get();
                final BoundingBox box = reachPosition == null ? null : reachPosition.getEntityBoundingBox().cloneBB();
                final int ticks = reachPosition == null ? -1 : reachPosition.getOtherPlayerMPPosRotationIncrements();

                final ReachModal.Type type;

                if (attacking) {
                    type = reachPosition == null ? ReachModal.Type.HIT_MISS : ReachModal.Type.HIT;
                } else {
                    type = reachPosition == null ? ReachModal.Type.CLICK_MISS : ReachModal.Type.CLICK;
                }

                reachModal = new ReachModal(type,
                        eyePos, resultEyeRot, minHit.get(), box, ticks, min.get());
            }

            else {
                reachModal = new ReachModal(ReachModal.Type.TICK, eyePos, resultEyeRot, null, null, -1,-1);
            }

            data.handleReach(reachModal, reachEntity);
            data.reach.setHasAttacked(false);
            data.reach.setHasClicked(false);
        }
        else if (packet instanceof GPacketPlayClientUseEntity) {

            // First render the packet since it's actually useful
            final GPacketPlayClientUseEntity use = (GPacketPlayClientUseEntity) packet;
            final int entity = use.getEntityId();

            // We don't need to redo it since it's already generated
            if (!data.reach.getEntities().containsKey(entity)){
                data.reach.setHasClicked(false);
                data.reach.setHasAttacked(false);

                //System.out.println("ENTITY OF ID " + entity + " LITERALLY NO EXIST!");
                return;
            }

            //System.out.println("ATTACK");
            final ReachEntity reachEntity = data.reach.getEntities().get(entity);

            data.reach.setHasAttacked(true);
            data.reach.setLastAttackedEntity(reachEntity);
            data.reach.setTarget((Player) NMSManager.getInms().getEntity(data.getPlayer().getWorld(), entity));
        }

        else if (packet instanceof GPacketPlayClientArmAnimation) {
            data.reach.setHasClicked(true);
        }

        /*
         * Handle entity spawning for exclusively players as mobs behave differently. Furthermore, Mob reach
         * in most cases is not an advantage that impacts gameplay as much as player reach does. It only gives
         * a slight benefit to a single player whilst not impacting the gameplay of others. Hence, complaints
         * will be near to none.
         */
        else if (packet instanceof GPacketPlayServerSpawnNamedEntity) {
            final GPacketPlayServerSpawnNamedEntity spawn = (GPacketPlayServerSpawnNamedEntity) packet;

            final ReachEntity reachEntity = new ReachEntity(spawn.getEntityId(),
                    (int) Math.round(spawn.getX() * multiplier),
                    (int) Math.round(spawn.getY() * multiplier),
                    (int) Math.round(spawn.getZ() * multiplier),
                    multiplier
            );

            //System.out.println("SPAWNING ENTITY OF ID " + spawn.getEntityId());

            data.reach.getEntities().put(spawn.getEntityId(), reachEntity);
            attemptAdd(spawn);
        }

        /*
         * To not have our server die, we also need to handle entity destruction. Great!
         */
        else if (packet instanceof GPacketPlayServerEntityDestroy) {
            final GPacketPlayServerEntityDestroy dst = (GPacketPlayServerEntityDestroy) packet;

            for (final int entity : dst.getEntities()) {
                data.reach.getEntities().remove(entity);
            }
        }

    }

    @Override
    public void fastHandle(final GPacket packet) {
        /*
         * We need to handle opponent player movement for this to work
         */
        if (packet instanceof GPacketPlayServerEntity) {
            final GPacketPlayServerEntity wrapper = (GPacketPlayServerEntity) packet;

            // Confirm actions
            final ReachEntity reachEntity = data.reach.getEntities().get(wrapper.getEntityId());

            if (reachEntity == null) return;

            //data.debug("Handled relative entity move in entity model tracker.");

            final PacketAction pre = new PacketAction() {
                @Override
                public void pre() {
                    int serverX = reachEntity.getServerPosX();
                    int serverY = reachEntity.getServerPosY();
                    int serverZ = reachEntity.getServerPosZ();

                    if (wrapper.isHasPos()) {
                        serverX += wrapper.getX();
                        serverY += wrapper.getY();
                        serverZ += wrapper.getZ();
                    }

                    final double x = (double) serverX / multiplier;
                    final double y = (double) serverY / multiplier;
                    final double z = (double) serverZ / multiplier;

                    reachEntity.setConfirming(true);
                    reachEntity.setNextReach(new Point(x, y, z));
                }
            };

            final PacketAction post = new PacketAction() {
                @Override
                public void pre() {
                    if (wrapper.isHasPos()) {
                        reachEntity.serverPosX += wrapper.getX();
                        reachEntity.serverPosY += wrapper.getY();
                        reachEntity.serverPosZ += wrapper.getZ();
                    }

                    final double x = (double) reachEntity.serverPosX / multiplier;
                    final double y = (double) reachEntity.serverPosY / multiplier;
                    final double z = (double) reachEntity.serverPosZ / multiplier;

                    for (final ReachEntity.ReachPosition reachPosition : reachEntity.getReachPositions()) {
                        if (reachPosition.skip) {
                            reachPosition.skip = false;
                            continue;
                        }

                        reachPosition.setPositionAndRotation2(x, y, z, 3);
                    }

                    reachEntity.setNextReach(null);
                    reachEntity.setConfirming(false);
                }
            };

            final boolean attacking = reachEntity.equals(data.reach.getLastAttackedEntity());
            final boolean rape = ConfigManager.getReach().isV1_deathMode();

            if (rape || attacking || (System.currentTimeMillis() - lastPreTick) > 5L) {
                data.connection.confirmFunctionAndTick(ConnectionHolder.ConfirmationType.TRANSACTION, pre);
                lastPreTick = System.currentTimeMillis();
            } else {
                data.connection.confirmFunctionLast(ConnectionHolder.ConfirmationType.TRANSACTION, pre);
            }

            data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, post);
            if (rape || !Artemis.v().getApi().getTickManager().isSdk() && (attacking || (System.currentTimeMillis() - lastPostTick) > 5L)) {
                CompletableFuture.runAsync(() -> {
                    data.connection.pushTick();
                    lastPostTick = System.currentTimeMillis();
                });
            }
        }

        /*
         * We need to handle opponent player teleports for this to work. This packet is not sent to the entity
         * itself, rather instead to other entities needing to account for the change if they remain in the render
         * distance between both teleports.
         */
        else if (packet instanceof GPacketPlayServerEntityTeleport) {
            final GPacketPlayServerEntityTeleport mov = (GPacketPlayServerEntityTeleport) packet;

            // Reach entity
            final ReachEntity reachEntity = data.reach.getEntities().get(mov.getEntityId());

            // First and foremost, ensure that our target exists
            if (reachEntity == null){
                return;
            }

            final PacketAction pre = new PacketAction() {
                @Override
                public void pre() {
                    final int serverX = mov.getX();
                    final int serverY = mov.getY();
                    final int serverZ = mov.getZ();

                    final double x = (double) serverX / multiplier;
                    final double y = (double) serverY / multiplier;
                    final double z = (double) serverZ / multiplier;

                    reachEntity.setConfirming(true);
                    reachEntity.setNextReach(new Point(x, y, z));
                }
            };

            final PacketAction post = new PacketAction() {
                @Override
                public void pre() {
                    reachEntity.serverPosX = mov.getX();
                    reachEntity.serverPosY = mov.getY();
                    reachEntity.serverPosZ = mov.getZ();

                    final double x = (double) reachEntity.serverPosX / multiplier;
                    final double y = (double) reachEntity.serverPosY / multiplier;
                    final double z = (double) reachEntity.serverPosZ / multiplier;

                    for (final ReachEntity.ReachPosition reachPosition : reachEntity.getReachPositions()) {
                        if (reachPosition.skip) {
                            reachPosition.skip = false;
                            continue;
                        }

                        if (Math.abs(reachPosition.posX - x) < 0.03125D
                                && Math.abs(reachPosition.posY - y) < 0.015625D
                                && Math.abs(reachPosition.posZ - z) < 0.03125D) {
                            reachPosition.setPositionAndRotation2(reachPosition.posX, reachPosition.posY, reachPosition.posZ, 3);
                        }
                        else {
                            reachPosition.setPositionAndRotation2(x, y, z, 3);
                        }
                    }

                    reachEntity.setNextReach(null);
                    reachEntity.setConfirming(false);
                }
            };
            final boolean rape = ConfigManager.getReach().isV1_deathMode();
            data.connection.confirmFunctionAndTick(ConnectionHolder.ConfirmationType.TRANSACTION, pre);
            data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, post);
            if (rape || !Artemis.v().getApi().getTickManager().isSdk()) {
                CompletableFuture.runAsync(() -> {
                    data.connection.pushTick();
                });
            }
        }
    }

    /**
     * Creates a Vec3 using the pitch and yaw of the entities rotation.
     */
    protected final Point getVectorForRotation(final float pitch, final float yaw) {
        final float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        final float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        final float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        final float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Point(f1 * f2, f3, f * f2);
    }

    private void attemptAdd(final GPacketPlayServerSpawnNamedEntity wrapper) {
        final Entity entity = NMSManager.getInms().getEntity(data.getPlayer().getWorld(), wrapper.getEntityId());

        if (entity != null) {
            if (!(entity instanceof Player)) {
                //System.out.println("ENTITY OF ID " + wrapper.getEntityId() + " IS NOT A FUCKING PLAYER");
                data.reach.getEntities().remove(wrapper.getEntityId());
                return;
            }
            return;
        }


        /*
         * This can sometimes happen. Fear not! We just run it a tick later (50ms). If this fails three
         * consecutive times, we can assume that the server is either lagging out, either something's
         * wrong with this player, either it's been destroyed instantly. Anywhom, we just fuckin
         * yoink it out and make a console error.
         */
        Integer attempted = attempts.get(wrapper.getEntityId());


        /*
         * Increment the attempts, if it's above 3, log the server warning. If not, fuck
         * them kids lets try again
         */
        if (attempted == null) {
            attempted = 0;
        }

        if (attempted > 3) {
            this.attempts.remove(wrapper.getEntityId());
            data.reach.getEntities().remove(wrapper.getEntityId());
            ServerUtil.console("Failed to find entity of ID &r" + wrapper.getEntityId() + "&b! Removing...");
            return;
        }

        final int attempts = attempted + 1;
        this.attempts.put(wrapper.getEntityId(), attempts);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                attemptAdd(wrapper);
            }
        }, 50);
    }
}
