package ac.artemis.core.v4.processor.emulator;

import ac.artemis.packet.minecraft.Unsafe;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.FastProcessHandler;
import ac.artemis.core.v4.check.VelocityHandler;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.data.holders.ConnectionHolder;
import ac.artemis.core.v4.emulator.damage.DamageSource;
import ac.artemis.core.v4.emulator.entity.utils.ItemUtil;
import ac.artemis.core.v4.emulator.potion.PotionEffect;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v4.utils.function.PacketAction;
import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v5.emulator.EmulatorManager;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.block.BlockFactory;
import ac.artemis.core.v5.emulator.block.impl.BlockAir;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.core.v5.utils.block.BlockUtil;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.packet.PacketManager;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.utils.ServerUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.client.PacketPlayClientLook;
import ac.artemis.packet.wrapper.client.PacketPlayClientPosition;
import cc.ghast.packet.PacketAPI;
import cc.ghast.packet.nms.EnumDirection;
import cc.ghast.packet.nms.MathHelper;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.*;
import cc.ghast.packet.wrapper.packet.play.server.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The Emulator pre-processor is the handler for the Emulator which is
 * ran before the main Emulator is ticked. This provides all the necessary
 * data, including duplicate one from other handlers, in order to entirely
 * isolate the process. This is designed to allow for a complete shift to
 * solely the emulator handler and data in the future.
 *
 * @author Ghast
 */
public class EmulatorPreProcessor extends AbstractHandler implements FastProcessHandler {
    /**
     * Instantiates a new Emulator pre-processor.
     *
     * @param data the data
     */
    public EmulatorPreProcessor(PlayerData data) {
        super("Emulator [0x01]", data);
    }

    private final double multiplier = data.getVersion().isOrAbove(ProtocolVersion.V1_9) ? 4096.0D : 32.D;

    /**
     * Handles the threaded packet input and output
     * @param packet Packet from the packet api
     */
    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            final PacketPlayClientFlying fly = (PacketPlayClientFlying) packet;
            final long now = System.nanoTime();

            /*
             *      __  __                ____
             *    / / / /___ _____  ____/ / /__  _____
             *   / /_/ / __ `/ __ \/ __  / / _ \/ ___/
             *  / __  / /_/ / / / / /_/ / /  __/ /
             * /_/ /_/\__,_/_/ /_/\__,_/_/\___/_/
             *
             */

            /*
             * Sets all the data to the previous ticked to maintain a consistent
             * location history. Despite if the packet does not contain a position
             * aspect, the previous position is still needed to be updated (this
             * ensures deltaX, Y and Z are 0.0)
             */
            data.prediction.setLastX(data.prediction.getX());
            data.prediction.setLastY(data.prediction.getY());
            data.prediction.setLastZ(data.prediction.getZ());

            /*
             * If the packet is a position packet, update the X coordinate with
             * the ones from the packet.
             */
            if (fly instanceof PacketPlayClientPosition) {
                final PacketPlayClientPosition wrapper = (PacketPlayClientPosition) packet;
                data.prediction.setX(wrapper.getX());
                data.prediction.setY(wrapper.getY());
                data.prediction.setZ(wrapper.getZ());
            }

            /*
             * Update the last Yaw and Pitch coordinate of the player. Just like
             * with the position, this needs to be updated regardless if the
             * packet contains a rotation or not.
             */
            data.prediction.setLastYaw(data.prediction.getYaw());
            data.prediction.setLastPitch(data.prediction.getPitch());

            /*
             * If the packet is a look packet (PacketPlayInLook or PacketPlayInPositionLook),
             * update the aim coordinates with their respective NON MODULO-ed values.
             */
            if (fly instanceof PacketPlayClientLook) {
                final PacketPlayClientLook wrapper = (PacketPlayClientLook) packet;
                data.prediction.setYaw(wrapper.getYaw());
                data.prediction.setPitch(wrapper.getPitch());
            }

            /*
             * Here, we update both respectively the last delta X, Y and Z with the previous
             * values and the new ones. The formula for delta calculation is as follows:
             *
             * d(x) = x(n) - x(n+1)
             *
             * d(x): delta of x
             * x(n): x coordinate at n instant
             */
            data.prediction.setLastDeltaX(data.prediction.getDeltaX());
            data.prediction.setLastDeltaY(data.prediction.getDeltaY());
            data.prediction.setLastDeltaZ(data.prediction.getDeltaZ());

            data.prediction.setDeltaX(data.prediction.getX() - data.prediction.getLastX());
            data.prediction.setDeltaY(data.prediction.getY() - data.prediction.getLastY());
            data.prediction.setDeltaZ(data.prediction.getZ() - data.prediction.getLastZ());

            /*
             * Update the client-side ground status of the player. This is NOT A VALUE
             * THAT SHOULD BE TRUSTED UNDER ANY CIRCUMSTANCE. This, as any client input,
             * can be manipulated (and often is!). This should solely be used to optimize
             * / create misc detections.
             */
            data.prediction.setLastGround(data.prediction.isGround());
            data.prediction.setGround(fly.isOnGround());

            /*
             * In this context, we seek to save whether the previous 2 packets and the
             * current flying packet held a position status. This is in particular used
             * to optimize 0.03 2nd degree predictions.
             */
            data.prediction.setLastLastPos(data.prediction.isLastPos());
            data.prediction.setLastPos(data.prediction.isPos());
            data.prediction.setPos(fly instanceof PacketPlayClientPosition);

            /*
             *      ______      __  _ __
             *     / ____/___  / /_(_) /___  __
             *    / __/ / __ \/ __/ / __/ / / /
             *   / /___/ / / / /_/ / /_/ /_/ /
             *  /_____/_/ /_/\__/_/\__/\__, /
             *                        /____/
             *
             */

            data.entity.setChunkLoaded(data.entity.getWorld().isLoaded(
                    MathHelper.floor(data.prediction.getX()) >> 4,
                    MathHelper.floor(data.prediction.getZ()) >> 4
                    )
            );

            /*
             * Update the entity status ground position. Just like the previous,
             * this is used to properly handle 0.03
             */
            data.entity.setWasPos(fly.isPos());

            /*
             * If the last movement was null, the player has most likely only just
             * joined the server. In which case, we have to re-initialize the motion
             * by sending an entity velocity packet to ensure the motion is re-synced
             * with the server.
             */
            if (data.getMovement().getLastMovement() == null) {
                final GPacket velocityPacket = new GPacketPlayServerEntityVelocity(
                        data.getPlayer().getEntityId(),
                        0,
                        0,
                        0
                );

                PacketManager.getApi().sendPacket(
                        data.getPlayer().getUniqueId(),
                        velocityPacket
                );
            }

            /*
             *
             */
            else {
                if (fly instanceof PacketPlayClientLook) {
                    data.entity.setRotation(
                            data.prediction.getYaw(),
                            data.prediction.getPitch()
                    );
                }

                if (fly instanceof PacketPlayClientPosition) {
                    data.entity.setServerPosition(
                            data.prediction.getX(),
                            data.prediction.getY(),
                            data.prediction.getZ()
                    );
                }

                data.entity.setGround(data.prediction.isLastGround() || data.entity.isOnGround());

                int ticks = 1;

                save: {
                    final boolean isFucked = data.getVersion().isOrAbove(ProtocolVersion.V1_9);
                    final boolean flag = isFucked && !data.prediction.isGround() && !data.prediction.isLastGround();

                    if (!flag)
                        break save;

                    final boolean descend = data.prediction.getLastY() > data.prediction.getY();

                    if (!descend)
                        break save;

                    double deltaY = data.prediction.getY() - data.prediction.getLastY();
                    double motionY = data.entity.getMotionY();


                    while (Math.abs(motionY - deltaY) > 1E-4 && Math.abs(motionY) < 0.03D) {
                        ticks++;

                        motionY -= 0.08D;
                        motionY *= 0.98D;
                    }
                }

                for (int i = 0; i < ticks; i++) {
                    data.entity.onUpdate();
                }
            }

            final long execution = System.nanoTime() - now;
            final double ms = (double) execution / 1000000.D;
            //System.out.println("[Artemis] Predictions took " + execution + " ns to process. This is around " + ms + "ms");

            data.prediction.setPredictionTime(ms);
            if (isReady()) {
                //System.out.println(":(");
                //handlePrediction(fly, new PlayerMovement(data.getPlayer(), data.data.entity.posX, data.data.entity.posY, data.data.entity.posZ, packet.getTimestamp()));
            }

            data.prediction.setLastFlying(packet.getTimestamp());
            data.prediction.getQueuedAttacks().clear();
        }

        /*else if (packet instanceof GPacketPlayClientEntityAction) {
            final GPacketPlayClientEntityAction act = (GPacketPlayClientEntityAction) packet;

            if (act.getEntityId() == data.getPlayer().getEntityId()) {
                switch (act.getAction()) {
                    case START_SPRINTING:
                        data.entity.setSprinting(true);
                        break;
                    case STOP_SPRINTING:
                        data.entity.setSprinting(false);
                        break;
                    case START_SNEAKING:
                        //data.entity.playerControls.setSneak(true);
                        data.entity.setSneaking(true); 
                        break;
                    case STOP_SNEAKING:
                        //data.entity.playerControls.setSneak(false);
                        data.entity.setSneaking(false);
                        break;
                }

            }
        }*/

        else if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity use = (GPacketPlayClientUseEntity) packet;

            switch (use.getType()) {
                case ATTACK: {
                    final Entity entity = NMSManager.getInms().getEntity(data.getPlayer().getWorld(), use.getEntityId());
                    data.entity.attackTargetEntityWithCurrentItem(entity);
                    data.prediction.getQueuedAttacks().add(entity);
                    break;
                }
                case INTERACT:
                case INTERACT_AT: {
                    //data.entity.setItemInUse(data.getPlayer().getItemInHand(), ItemUtil.getItemUseByItem(data.entity, data.getPlayer().getItemInHand()));
                }
            }
        }

        else if (packet instanceof GPacketPlayClientAbilities) {
            final GPacketPlayClientAbilities ab = (GPacketPlayClientAbilities) packet;

            if (ab.getWalkSpeed().isPresent())
                data.entity.getCapabilities().setWalkSpeed(ab.getWalkSpeed().get());
            if (ab.getFlySpeed().isPresent())
                data.entity.getCapabilities().setFlySpeed(ab.getFlySpeed().get());
            if (ab.getCreativeMode().isPresent())
                data.entity.getCapabilities().setCreativeMode(ab.getCreativeMode().get());
            if (ab.getInvulnerable().isPresent())
                data.entity.getCapabilities().setDisableDamage(ab.getInvulnerable().get());
            if (ab.getAllowedFlight().isPresent())
                data.entity.getCapabilities().setAllowFlying(ab.getAllowedFlight().get());
            if (ab.isFlying())
                data.entity.getCapabilities().setFlying(ab.isFlying());
        }

        else if (packet instanceof GPacketPlayClientBlockPlace) {
            final GPacketPlayClientBlockPlace plc = (GPacketPlayClientBlockPlace) packet;

            if (plc.getDirectionId() == 255) {
                final boolean main = plc.getHand().equals(PlayerEnums.Hand.MAIN_HAND);
                final ItemStack stack;

                try {
                    if (ServerUtil.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                        stack = main
                                ? data.getPlayer().getInventory().getItem(data.prediction.getSlot())
                                : data.getPlayer().getInventory().getItemInOffHand();
                    } else {
                        stack = data.getPlayer().getInventory().getItem(data.prediction.getSlot());
                    }

                    if (stack == null) {
                        return;
                    }

                    //System.out.println("Started using item " + stack.getType());
                    data.entity.setItemInUse(stack, plc.getHand(), ItemUtil.getItemUseByItem(data.entity, stack));
                } catch (ArrayIndexOutOfBoundsException e) {
                    // Still no idea where this issue originates from
                }
            }

            else if (plc.getDirection().isPresent() && plc.getItem().isPresent()
                    && plc.getItem().get().getType().isBlock() && plc.getPosition() != null) {


                final Material material = plc.getItem().get().getType();
                final EnumDirection enumfacing = plc.getDirection().get();
                final NaivePoint original = new NaivePoint(plc.getPosition().getX(), plc.getPosition().getY(), plc.getPosition().getZ());
                final NaivePoint relative = original.offset(enumfacing);


                final Block blockOriginal = BlockFactory.getBlock(
                        material,
                        EnumFacing.getFront(plc.getDirectionId()),
                        plc.getItem().get().getData(),
                        new NaivePoint(original.getX(), original.getY(), original.getZ()),
                        new Point(plc.getVector())
                );
                final Block blockRelative = BlockFactory.getBlock(
                        material,
                        EnumFacing.getFront(plc.getDirectionId()),
                        plc.getItem().get().getData(),
                        new NaivePoint(relative.getX(), relative.getY(), relative.getZ()),
                        new Point(plc.getVector())
                );

                boolean canReplaceOriginal;

                original: {
                    canReplaceOriginal = NMSManager.getInms().getReplaceAttributeBlock(data.entity.getWorld().getBukkitWorld(), original);

                    if (!canReplaceOriginal)
                        break original;

                    final List<BoundingBox> boundingBoxes = blockOriginal.getBoundingBox(data.entity.getWorld());
                    for (BoundingBox boundingBox : boundingBoxes) {
                        for (BoundingBox box : NMSManager.getInms().getCollidingEntities(null, data.getPlayer().getWorld(), boundingBox)) {
                            canReplaceOriginal = false;
                            break original;
                        }
                    }
                }


                boolean canReplaceRelative;

                relative: {
                    canReplaceRelative = NMSManager.getInms().getReplaceAttributeBlock(
                            data.entity.getWorld().getBukkitWorld(),
                            relative
                    );

                    if (!canReplaceRelative)
                        break relative;

                    final List<BoundingBox> boundingBoxes = blockRelative.getBoundingBox(data.entity.getWorld());
                    for (BoundingBox boundingBox : boundingBoxes) {
                        for (BoundingBox box : NMSManager.getInms().getCollidingEntities(null, data.getPlayer().getWorld(), boundingBox)) {
                            canReplaceRelative = false;
                            break relative;
                        }
                    }
                }


                if (canReplaceOriginal) {
                    data.entity.getWorld().updateMaterialAt(
                            blockOriginal,
                            original.getX(),
                            original.getY(),
                            original.getZ()
                    );
                } else if (canReplaceRelative) {
                    data.entity.getWorld().updateMaterialAt(
                            blockRelative,
                            relative.getX(),
                            relative.getY(),
                            relative.getZ()
                    );
                }

                final boolean main = plc.getHand().equals(PlayerEnums.Hand.MAIN_HAND);
                final ItemStack stack;

                if (ServerUtil.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                    stack = main
                            ? data.getPlayer().getInventory().getItemInMainHand()
                            : data.getPlayer().getInventory().getItemInOffHand();
                } else {
                    stack = data.getPlayer().getInventory().getItemInHand();
                }

                /*final GhostBlock ghostBlock = new GhostBlock(stack,
                        new Location(
                                plc.getPlayer().getWorld(),
                                plc.getPosition().getX() + plc.getDirection().get().getAdjacentX(),
                                plc.getPosition().getY() + plc.getDirection().get().getAdjacentY(),
                                plc.getPosition().getZ() + plc.getDirection().get().getAdjacentZ()
                        )
                );

                data.entity.getGhostBlocks().add(ghostBlock);
                //data.world.addBlock(stack, new NaivePoint(plc.getPosition().getX(), plc.getPosition().getY(), plc.getPosition().getZ()));*/
            }
        }

        else if (packet instanceof GPacketPlayClientHeldItemSlot) {
            final GPacketPlayClientHeldItemSlot slt = (GPacketPlayClientHeldItemSlot) packet;

            data.prediction.setSlot(slt.getSlot() > 9 || slt.getSlot() <= 0
                            ? data.prediction.getSlot()
                            : slt.getSlot()
            );

            if (data.entity.isUsingItem()) {
                final boolean main = PlayerEnums.Hand.MAIN_HAND.equals(data.entity.getItemInHand());
                if (main) {
                    data.entity.clearItemInUse();
                }
            }
        }

        else if (packet instanceof GPacketPlayClientItemUse) {
            final GPacketPlayClientItemUse use = (GPacketPlayClientItemUse) packet;

            final boolean main = use.getHand().equals(PlayerEnums.Hand.MAIN_HAND);
            final ItemStack stack;

            if (ServerUtil.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                stack = main
                        ? data.getPlayer().getInventory().getItemInMainHand()
                        : data.getPlayer().getInventory().getItemInOffHand();
            } else {
                stack = data.getPlayer().getInventory().getItem(data.prediction.getSlot());
            }

            data.entity.setItemInUse(stack, use.getHand(), ItemUtil.getItemUseByItem(data.entity, stack));
        }


        else if (packet instanceof GPacketPlayClientBlockDig) {
            final GPacketPlayClientBlockDig dig = (GPacketPlayClientBlockDig) packet;

            switch (dig.getType()) {
                case RELEASE_USE_ITEM:
                case SWAP_HELD_ITEMS:
                case DROP_ITEM:
                case DROP_ALL_ITEMS:
                    data.entity.stopUsingItem();
            }
        }

        else if (packet instanceof GPacketPlayClientClientCommand) {
            final GPacketPlayClientClientCommand command = (GPacketPlayClientClientCommand) packet;
            switch (command.getCommand()) {
                case PERFORM_RESPAWN:
                    try {
                        data.entity = EmulatorManager.getProvider()
                                .getFactory()
                                .setData(data)
                                .build();
                    } catch (NullPointerException e) {
                        Chat.sendConsoleMessage("&4Fatal issue with Artemis! Error code &cOxA4-02");
                        e.printStackTrace();
                        Chat.sendConsoleMessage(Chat.spacer());
                    }

                    data.getSetbackHandler().tick(data, false, false);
                    data.movement.setRespawnTicks(5);
                    PacketManager.getApi().sendPacket(
                            data.getPlayer().getUniqueId(),
                            new GPacketPlayServerEntityVelocity(data.getPlayer().getEntityId(), 0,0,0)
                    );
                    break;
                case OPEN_INVENTORY_ACHIEVEMENT:
                    data.user.setInventoryOpen(true);
                    data.entity.clearItemInUse();
                    break;
            }
        }


        /*else if (packet instanceof GPacketPlayServerUpdateAttributes) {
            final GPacketPlayServerUpdateAttributes atr = (GPacketPlayServerUpdateAttributes) packet;

            if (atr.getEntityId() == data.getPlayer().getEntityId()) {
                data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        atr.getAttributes().forEach(e -> {
                            IAttributeInstance instance = data.entity.getAttributeMap().getAttributeInstanceByName(e.getLocalName());
                            if (instance == null) {
                                instance = data.entity.getAttributeMap()
                                        .registerAttribute(new RangedAttribute(null,
                                                e.getLocalName(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
                            }

                            instance.setBaseValue(e.getBaseValue());
                            instance.removeAllModifiers();

                            for (GPacketPlayServerUpdateAttributes.Modifier atrm : e.getModifiers()) {
                                instance.applyModifier(new AttributeModifier(atrm.getId(), atrm.getName(), atrm.getAmount(), atrm.getOperation()));
                            }
                        });
                    }
                });
            }
        }*/
    }

    /**
     * Handles the packet directly from the Netty thread
     * @param packet Packet API packet
     */
    @Override
    public void fastHandle(GPacket packet) {
        if (packet instanceof GPacketPlayServerAbilities) {
            final GPacketPlayServerAbilities ab = (GPacketPlayServerAbilities) packet;

            data.connection.confirmFunctionAndTick(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                @Override
                public void pre() {
                    ab.getWalkSpeed().ifPresent(e -> data.entity.getCapabilities().setWalkSpeed(e));
                    ab.getFlySpeed().ifPresent(e -> data.entity.getCapabilities().setFlySpeed(e));
                    ab.getCreativeMode().ifPresent(e -> data.entity.getCapabilities().setCreativeMode(e));
                    ab.getInvulnerable().ifPresent(e -> data.entity.getCapabilities().setDisableDamage(e));
                    ab.getAllowedFlight().ifPresent(e -> data.entity.getCapabilities().setAllowFlying(e));
                    data.entity.getCapabilities().setFlying(ab.isFlying());
                }
            });
        }

        else if (packet instanceof GPacketPlayServerEntityStatus) {
            final GPacketPlayServerEntityStatus sts = (GPacketPlayServerEntityStatus) packet;

            if (sts.getEntityId() == data.getPlayer().getEntityId()) {
                switch (sts.getLogicOpcode()) {
                    case 3: {
                        data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                            @Override
                            public void pre() {
                                data.entity.setHealth(0.0F);
                                data.entity.onDeath(DamageSource.generic);
                            }
                        });
                        break;
                    }
                    case 9: {
                        data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                            @Override
                            public void pre() {
                                data.entity.onItemUseFinish();
                            }
                        });
                        break;
                    }
                    default:
                        return;
                }

            }
        }

        else if (packet instanceof GPacketPlayServerRespawn) {
            final GPacketPlayServerRespawn rsp = (GPacketPlayServerRespawn) packet;

            data.connection.confirmFunctionAndTick(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                @Override
                public void pre() {
                    // Dimension change
                    //data.entity = new BntityPlayerXYZ(data);

                    //data.entity = new BntityPlayerXYZ(data);
                    data.entity.resetWorld();
                    data.entity.prepareToSpawn();
                    data.movement.setRespawnTicks(150);
                    data.user.setInventoryOpen(false);
                    //System.out.println("Received teleport respawn");
                }
            });
            data.movement.setRespawnTicks(150);
        }

        else if (packet instanceof GPacketPlayServerWindowOpen) {
            data.connection.confirmFunctionAndTick(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                @Override
                public void pre() {
                    data.entity.stopUsingItem();
                    data.user.setInventoryOpen(true);
                }
            });
        }
        else if (packet instanceof GPacketPlayServerEntityVelocity) {

            // Assign the packet
            final GPacketPlayServerEntityVelocity vel = (GPacketPlayServerEntityVelocity) packet;

            // Ensure the packet's entity id corresponds to the player's
            if (vel.getEntityId() == data.getPlayer().getEntityId()) {

                // Create a new velocity object
                final Velocity velocity = new Velocity(vel.getValueX(), vel.getValueY(), vel.getValueZ());

                data.connection.confirmFunctionAndTick(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        data.prediction.getQueuedVelocity().add(velocity);
                        data.prediction.getQueuedVelocities().add(velocity);
                    }
                });

                CompletableFuture.runAsync(() -> {
                    data.connection.confirmFunctionAndTick(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                        @Override
                        public void pre() {
                            data.checkManager.forEach(cm -> cm.getChecks()
                                    .values()
                                    .stream()
                                    .filter(check -> check instanceof VelocityHandler)
                                    .filter(ArtemisCheck::canCheck)
                                    .map(check -> (VelocityHandler) check)
                                    .forEach(check -> {
                                        try {
                                            check.handle(velocity);
                                        } catch (Exception ex){
                                            ex.printStackTrace();
                                        }
                                    })
                            );

                            if (!data.prediction.getQueuedVelocities().isEmpty() && data.prediction.getQueuedVelocities().peek().equals(velocity)) {
                                //entity.setMotionX(velocity.getX());
                                //entity.setMotionY(velocity.getY());
                                //entity.setMotionZ(velocity.getZ());
                                data.prediction.getQueuedVelocities().poll();
                            }

                            if (!data.prediction.getQueuedVelocity().contains(velocity)) return;
                            data.entity.setVelocity(velocity.getX(), velocity.getY(), velocity.getZ());

                            while (!data.prediction.getQueuedVelocity().pop().equals(velocity));
                        }
                    });
                });
            }
        }

        else if (packet instanceof GPacketPlayServerEntityEffect) {
            final GPacketPlayServerEntityEffect eff = (GPacketPlayServerEntityEffect) packet;
            if (eff.getEntityId() == data.getPlayer().getEntityId()) {

                final PotionEffect effect = new PotionEffect(eff.getEffectId(), eff.getDuration(), eff.getAmplifier());

                data.connection.confirmFunctionAndTick(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        data.entity.addPotionEffect(effect);

                    }
                });
            }
        }

        else if (packet instanceof GPacketPlayServerEntityEffectRemove) {
            final GPacketPlayServerEntityEffectRemove eff = (GPacketPlayServerEntityEffectRemove) packet;
            if (eff.getEntityId() == data.getPlayer().getEntityId()) {

                data.connection.confirmFunctionAndTick(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        data.entity.removePotionEffect(eff.getEffectId());
                    }
                });
            }
        }

        else if (packet instanceof GPacketPlayServerEntityTeleport) {
            final GPacketPlayServerEntityTeleport pos = (GPacketPlayServerEntityTeleport) packet;

            if (pos.getEntityId() == data.getPlayer().getEntityId()) {
                data.connection.confirmFunctionAndTick(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        data.entity.handlePacket(pos);

                        data.prediction.setLastX(data.prediction.getX());
                        data.prediction.setLastY(data.prediction.getY());
                        data.prediction.setLastZ(data.prediction.getZ());
                        data.prediction.setLastYaw(data.prediction.getYaw());
                        data.prediction.setLastPitch(data.prediction.getPitch());
                        data.prediction.setLastGround(data.prediction.isGround());

                        data.prediction.setX(pos.getX() / multiplier);
                        data.prediction.setY(pos.getY() / multiplier);
                        data.prediction.setZ(pos.getZ() / multiplier);
                        data.prediction.setYaw(pos.getValueYaw());
                        data.prediction.setPitch(pos.getValuePitch());
                        data.prediction.setGround(pos.isOnGround());
                        //System.out.println("Received teleport entity");
                    }
                });

            }
        }

        else if (packet instanceof GPacketPlayServerUpdateAttributes) {
            final GPacketPlayServerUpdateAttributes att = (GPacketPlayServerUpdateAttributes) packet;

            if (att.getEntityId() == data.getPlayer().getEntityId()) {
                data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        data.entity.handlePacket(att);
                        //System.out.println("Received entity attributes");
                    }
                });
            }
        }

        else if (packet instanceof GPacketPlayServerEntityMetadata) {
            final GPacketPlayServerEntityMetadata mtt = (GPacketPlayServerEntityMetadata) packet;

            if (mtt.getEntityId() == data.getPlayer().getEntityId()) {
                data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        data.entity.getDataWatcher().updateWatchedObjectsFromList(data.entity.getDataWatcherFactory().readWatchedListFromPacketBuffer(mtt.getBuffer()));
                    }
                });
            }
        }

        else if (packet instanceof GPacketPlayServerExplosion) {
            final GPacketPlayServerExplosion exp = (GPacketPlayServerExplosion) packet;

            data.connection.confirmFunctionAndTick(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                @Override
                public void pre() {
                    data.entity.handleExplosion(exp);
                }
            });
        }

        else if (packet instanceof GPacketPlayServerBlockChange) {
            final GPacketPlayServerBlockChange chg = (GPacketPlayServerBlockChange) packet;

            //Bukkit.broadcastMessage("block change: " + chg.getPosition().getX() + ":" + chg.getPosition().getY() + ":" + chg.getPosition().getZ());

            data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                @Override
                public void pre() {
                    final BlockPosition pos = chg.getPosition();
                    final ac.artemis.packet.minecraft.block.Block bukkitBlock = BlockUtil.getBlockAsync(
                            data.getPlayer().getWorld(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ()
                    );

                    final NaivePoint point = new NaivePoint(pos.getX(), pos.getY(), pos.getZ());

                    if (bukkitBlock == null) {
                        data.getEntity().getWorld().updateMaterialAt(new BlockAir(point, EnumFacing.UP), pos.getX(), pos.getY(), pos.getZ());
                        return;
                    }

                    final Block block = BlockFactory.getBlock(
                            bukkitBlock.getType(),
                            null,
                            bukkitBlock.getData(),
                            point,
                            null
                    );

                    //Bukkit.broadcastMessage("Updated soocess at " + point.toString() + " of type " + block.getMaterial().toString());
                    data.getEntity().getWorld().updateMaterialAt(block, pos.getX(), pos.getY(), pos.getZ());
                }
            });

        }

        else if (packet instanceof GPacketPlayServerBlockChangeMulti) {
            final GPacketPlayServerBlockChangeMulti chg = (GPacketPlayServerBlockChangeMulti) packet;

            //Bukkit.broadcastMessage("block change multi: " + chg.getRecords().size());

            if (chg.getRecords() == null) return;

            data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                @Override
                public void pre() {
                    for (GPacketPlayServerBlockChangeMulti.BlockChange record : chg.getRecords()) {
                        final BlockPosition pos = record.getPosition();
                        final ac.artemis.packet.minecraft.block.Block bukkitBlock = BlockUtil.getBlockAsync(
                                data.getPlayer().getWorld(),
                                pos.getX(),
                                pos.getY(),
                                pos.getZ()
                        );

                        final NaivePoint point = new NaivePoint(pos.getX(), pos.getY(), pos.getZ());

                        if (bukkitBlock == null) {
                            data.getEntity().getWorld().updateMaterialAt(new BlockAir(point, EnumFacing.UP), pos.getX(), pos.getY(), pos.getZ());
                            return;
                        }

                        final Block block = BlockFactory.getBlock(
                                bukkitBlock.getType(),
                                null,
                                bukkitBlock.getData(),
                                point,
                                null
                        );


                        //Bukkit.broadcastMessage("Updated multi soocess at " + point.toString() + " of type " + block.getMaterial().toString());
                        data.getEntity().getWorld().updateMaterialAt(block, pos.getX(), pos.getY(), pos.getZ());
                    }
                }
            });
        }

    }

    /*
      __  __                                     _
     |  \/  |                                   | |
     | \  / | _____   _____ _ __ ___   ___ _ __ | |_
     | |\/| |/ _ \ \ / / _ \ '_ ` _ \ / _ \ '_ \| __|
     | |  | | (_) \ V /  __/ | | | | |  __/ | | | |_
     |_|  |_|\___/ \_/ \___|_| |_| |_|\___|_| |_|\__|

     */

    private boolean isReady() {
        return data.entity.getLastPositionPrevious() != null && data.entity.getLastPositionPrevious() != null;
    }

    private void preUpdate() {
        data.entity.setRotation(
                data.prediction.getYaw(),
                data.prediction.getPitch()
        );

        data.entity.setPosition(
                data.prediction.getLastX(),
                data.prediction.getLastY(),
                data.prediction.getLastZ()
        );

        data.entity.setServerPosition(
                data.prediction.getX(),
                data.prediction.getY(),
                data.prediction.getZ()
        );
    }

    private boolean preCheck() {
        //System.out.println("DATA IS NULL ");
        return data == null || data.movement == null;
    }

    /**
     * Is null location boolean.
     *
     * @return the boolean
     */
    public boolean isNullLocation() {
        return (preCheck() || data.movement.getLocation() == null || data.movement.getLastLocation() == null);
    }

    /**
     * Is null velocity boolean.
     *
     * @return the boolean
     */
    public boolean isNullVelocity() {
        return (preCheck() || data.movement.getVelocity() == null || data.movement.getLastVelocity() == null);
    }

    /**
     * Is null rotation boolean.
     *
     * @return the boolean
     */
    public boolean isNullRotation() {
        return (preCheck() || data.movement.getRotation() == null || data.movement.getLastRotation() == null);
    }

    /**
     * Is null movement boolean.
     *
     * @return the boolean
     */
    public boolean isNullMovement() {
        return (preCheck() || data.movement.getMovement() == null || data.movement.getLastMovement() == null);
    }
}
