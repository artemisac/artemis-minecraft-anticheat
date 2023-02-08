package ac.artemis.checks.enterprise.velocity;

import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.packet.minecraft.world.Chunk;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.utils.blocks.BlockUtil;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.minecraft.MathHelper;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientLook;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientPosition;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientPositionLook;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientTransaction;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ghast
 * @since 05/12/2020
 * Artemis © 2020
 */
public class VelocityWithoutPredictions extends ArtemisCheck implements PacketHandler, PacketExcludable {
    public VelocityWithoutPredictions(PlayerData data, CheckInformation info) {
        super(data, info);
        this.setCompatiblePackets(
                GPacketPlayClientTransaction.class,
                PacketPlayClientFlying.class,
                GPacketPlayClientLook.class,
                GPacketPlayClientPosition.class,
                GPacketPlayClientPositionLook.class
        );
    }

    private double motionX, motionY, motionZ;
    private boolean check;

    @Override
    public void handle(GPacket packet) {
        /*
         * Here we register the the velocity hit once it's confirmed by a transaction.
         * This means that the player was hit and that the next flying packet will have
         * the first tick offset. The values here are integers multiplied by 8000.
         */
        if (packet instanceof GPacketPlayClientTransaction && data.movement.isProcessedVelocity()) {
            Velocity velocity = data.movement.getVelocity();
            this.motionX = velocity.getX();
            this.motionY = velocity.getY();
            this.motionZ = velocity.getZ();
            this.check = true;
            return;
        }

        final double distance = this.getSmallestDistance();
        final double received = data.movement.getMovement().distanceSquare(data.movement.getLastMovement());

        final double percentage = (received / distance) * 100.D;

        flag: {
            final boolean invalid = percentage > 99.69
                    || this.isExempt(
                            ExemptType.FLIGHT,
                            ExemptType.VEHICLE,
                            ExemptType.VOID,
                            ExemptType.JOIN,
                            ExemptType.WORLD,
                            ExemptType.GAMEMODE,
                            ExemptType.COLLIDE_ENTITY,
                            ExemptType.LIQUID,
                            ExemptType.FLIGHT,
                            ExemptType.PLACING,
                            ExemptType.TELEPORT,
                            ExemptType.LIQUID_WALK
                    );

            if (invalid)
                break flag;

            this.log(new Debug<>("percentage", percentage));
        }
    }

    public double getSmallestDistance() {
        final PlayerPosition pos = data.movement.getLastMovement();
        final Location loc = pos.toBukkitLocation();

        /*
         * We need to do some world checking for this check to work. Hence, if the chunk is unloaded,
         * we don't perform the check.
         */
        final boolean world = loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
        if (world) {
            this.debug("World is not loaded");
            return -1;
        }

        final float yaw = MathUtil.normalizeYaw(data.movement.lastMovement.getYaw());

        final boolean usingItem = data.entity.isUsingItem();
        final boolean ground = data.user.isOnFakeGround();
        final boolean ladder = data.movement.isOnLadder();
        final boolean web = data.movement.isInWeb();


        final boolean sneaking = data.user.isSneaking();
        final boolean sprinting = data.user.isSprinting();

        final boolean speedEffect = data.getPlayer().hasEffect(PotionEffectType.SPEED);

        /*
         * Speed modifying. In bukkit, the speed is multiplied by two for some reason. Henceforth,
         * we can just get the base player speed by divising it by two. This is done by this function
         * Furthermore, we need to add the valid attributes to it:
         */
        float speed = this.getSpeedModifier();

        /*
         * As per Minecraft's attribute system, sprinting gives exactly the following attribute: 0.30000001192092896D
         * Henceforth, we can do lossy double conversion to obtain the same result. It's near to insignificant
         */
        if (sprinting) {
            speed *= (double) 1.3F;
        }

        /*
         * Minecraft's attribute system for potions is split into 3 categories: type 0, type 1 and type 2.
         * Type 0: This is a value which will be added on top of the attribute
         * Type 1: This is a value which will multiply the attribute value
         * Type 2: This is a value which will be added based on a multiplication of the attribute value (1 + attr * value)
         *
         * Speed happens to be a type 2, as highlighted by the following code in MCP:
         *  private static final AttributeModifier sprintingSpeedBoostModifier = (new AttributeModifier(
         *         sprintingSpeedBoostModifierUUID, "Sprinting speed boost", Magic.SPRINT_MODIFIER, 2))
         *         .setSaved(false);
         *
         * It is henceforth safe to deem to define Speed as a type 2 effect, hence 1.F + (SprintEffectModifier * 0.2F)
         * with lossy conversion pretty much defines this.
         */
        if (speedEffect) {
            speed *= this.getSpeedEffectModifier();
        }

        /*
         * Friction is one of these important factors in Minecraft's sloppy physics engine. Friction in general
         * represents the speed at which an object can move at whilst colliding. The air friction so happens to
         * be a floating value of a static 0.91F whereas the ground friction is multiplied by slipperiness, a value
         * attributed to each block on the basis of how much they slow down a player when collided with.
         */
        float friction = 0.91F;

        if (ground) {
            friction *= this.getSlipperiness(data.movement.lastLocation);
        }

        /*
         * Drag, or so I like to call it, is a force proportional to the velocity which takes into account the friction.
         * As opposed to friction however, drag increases with speed. An example to drag is when you rub your hands or
         * have something which resists. As the motion increases, the drag will increase and hence will allow for an
         * acceleration.
         *
         * On the other hand, if you're not on the ground, the drag is a set pre-defined value. It's value increases
         * on the basis whether you are sprinting or not by very little nonetheless
         */
        final float drag = 0.16277136F / (friction * friction * friction);

        final float acceleration = ground
                ? speed * drag
                : sprinting
                    ? 0.026F
                    : 0.02F;



        final Point received = data.movement.getMovement().toPoint();
        double shortestDistance = 999.D;

        /*
         * Yes, this loop looks ugly. No, I'm not going to change it. Fuck you, I like my clarity. The reason it's not
         * compacted into a single loop is simply due to how it operates. I am not going to do silly maths to save one
         * or two IntNode calls. Fuck off
         */
        for (int forward = -1; forward < 2; forward++) {
            for (int strafe = -1; strafe < 2; strafe++) {

                /*
                 * This particular loop was first adapted by Artemis and discovered by yours truly: Ghast. It was found
                 * through trial and error that determining jump through the environment is a dump fucking idea and that
                 * one addition to the loop, which unfortunately double's it's repetition indeed, improves the accuracy
                 * by over 10%. This benefit is extremely good and also allows for a much more accurate detection of low
                 * hops through bad packets.
                 */
                for (int jump = 0; jump < 2; jump++) {

                    /*
                     * Default value in MCP are integers (forward and strafe) multiplied by this odd constant. For
                     * reasons unbeknownst to me, these values just seem to slow down the player a slight to give it
                     * more of a natural feeling. Interest stuff, Notch
                     */
                    float moveForward = forward * 0.98F;
                    float moveStrafe = strafe * 0.98F;

                    /*
                     * When a player uses an item, his move values are multiplied by a fifth. This significantly
                     * slows down the player's speed.
                     */
                    if (usingItem) {
                        moveForward *= 0.2F;
                        moveStrafe *= 0.2F;
                    }

                    /*
                     * Alike with when using an item, move values are slowed down when sneaking. This makes sense.
                     * Stop saying it doesn't. Really.
                     */
                    if (sneaking) {
                        moveForward *= (float) 0.3D;
                        moveStrafe *= (float) 0.3D;
                    }

                    /*
                     * Here as we are obviously iterating we copy the values temporarily to not disturb the rest
                     * of the loop. Pretty straight forward
                     */
                    double velocityX = motionX;
                    double velocityY = motionY;
                    double velocityZ = motionZ;


                    /*
                     * Corresponds to moveFlying in EntityLivingBase. This is the fundamental part which controls
                     * the motion acceleration based on moveForward and moveStrafe
                     */
                    keyboard: {
                        /*
                         * Don't ask why I called it key. I refer to the keyboard. This is the squared distance
                         * of a vector visualization of the WASD keys.
                         * In the case it's too small, movement remains untouched and stays at the set previous
                         * velocity
                         */
                        float key = moveStrafe * moveStrafe + moveForward * moveForward;
                        if (key < 1.0E-4F)
                            break keyboard;

                        /*
                         * In the case here, if the movement is too small, it's put back to a default minimum
                         */
                        if (key < 1.0F) {
                            key = 1.0F;
                        }

                        /*
                         * This bit happens to be very confusing for a lot of individuals, including myself.
                         *  In short, the WASD factor (moveForward and moveStrafe) are dividing factors to the
                         * acceleration, also known as friction. As we know that the key value is positive and
                         * superior to 1 at all times, we know this is a division which will at best remain identical
                         *
                         * -> motionStrafe/motionForward
                         * As for motionStrafe, this gives us a set directional value to apply. Would this not be
                         * the case, it would make movement very choppy and XYZ oriented instead of giving a larger
                         * range
                         */
                        key = acceleration / key;
                        final float motionStrafe = moveStrafe * key;
                        final float motionForward = moveForward * key;


                        /*
                         * This is pretty much the conversion to adding it to the velocity by granting it a direction
                         * as a vector. The trigonometry is rather simple. X and Z represent the vector point position
                         * on a circle.
                         * If you're curious about this, view :
                         * https://courses.lumenlearning.com/boundless-algebra/chapter/trigonometric-functions-and-the-unit-circle/
                         * https://textimgs.s3.amazonaws.com/boundless-algebra/lhntemhracxenling6eg.jpe
                         *
                         * The rest I have no idea. Sorry.
                         */
                        final float x = MathHelper.sin(yaw * 0.0174532925F);
                        final float z = MathHelper.cos(yaw * 0.0174532925F);
                        velocityX += motionStrafe * z - motionStrafe * x;
                        velocityZ += motionForward * z + motionForward * x;
                    }

                    jump: {
                        if (jump == 1) {
                            velocityY = 0.42F;
                            if (data.getPlayer().hasEffect(PotionEffectType.JUMP)) {
                                //this.motionY += (double) ((float) (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
                            }
                        }
                    }

                    ladder: {
                        if (!ladder) break ladder;

                        velocityX = MathHelper.clamp_double(velocityX, -0.15F, 0.15F);
                        velocityY = Math.max(velocityY, -0.15D);
                        velocityZ = MathHelper.clamp_double(velocityZ, -0.15F, 0.15F);

                        if (!sneaking || velocityY >= 0.D) break ladder;

                        velocityY = 0.0D;
                    }

                    web: {
                        if (!web) break web;

                        velocityX *= 0.25D;
                        velocityY *= 0.05000000074505806D;
                        velocityZ *= 0.25D;
                    }

                    double offsetX = velocityX;
                    double offsetY = velocityY;
                    double offsetZ = velocityZ;

                    sneak: {
                        if (!sneaking || !ground) break sneak;

                        final double offset = 0.05D;
                        while (velocityX != 0.0D &&
                                this.getCollidingBB(loc.getChunk(), pos.clone().add(velocityX, -1.0D, 0.0D))
                                        .isEmpty()) {
                            if (velocityX < offset && velocityX >= -offset) {
                                velocityX = 0.0D;
                            }
                            else if (velocityX > 0.0D) {
                                velocityX -= offset;
                            } else {
                                velocityX += offset;
                            }

                            offsetX = velocityX;
                        }

                        while (velocityZ != 0.0D &&
                                this.getCollidingBB(loc.getChunk(), pos.clone().add(0.0D, -1.0D, velocityZ))
                                        .isEmpty()) {
                            if (velocityZ < offset && velocityZ >= -offset) {
                                velocityZ = 0.0D;
                            } else if (velocityZ > 0.0D) {
                                velocityZ -= offset;
                            } else {
                                velocityZ += offset;
                            }

                            offsetZ = velocityZ;
                        }

                        while (velocityX != 0.0D && velocityZ != 0.0D &&
                                this.getCollidingBB(loc.getChunk(), pos.clone().add(velocityX, -1.0D, velocityZ))
                                        .isEmpty()) {
                            if (velocityX < offset && velocityX >= -offset) {
                                velocityX = 0.0D;
                            } else if (velocityX > 0.0D) {
                                velocityX -= offset;
                            } else {
                                velocityX += offset;
                            }

                            offsetX = velocityX;

                            if (velocityZ < offset && velocityZ >= -offset) {
                                velocityZ = 0.0D;
                            } else if (velocityZ > 0.0D) {
                                velocityZ -= offset;
                            } else {
                                velocityZ += offset;
                            }

                            offsetZ = velocityZ;
                        }
                    }

                    final PlayerPosition offsetCollisionPos = pos.clone().add(velocityX, velocityY, velocityZ);
                    final Set<BoundingBox> collisionsOnOffset = this.getCollidingBB(loc.getChunk(), offsetCollisionPos);

                    BoundingBox entityBoundingBox = pos.getBox().cloneBB();

                    for (BoundingBox bb : collisionsOnOffset) {
                        velocityY = bb.calculateYOffset(entityBoundingBox, velocityY);
                    }

                    entityBoundingBox.add(0.0D, velocityY, 0.0D);

                    for (BoundingBox bb : collisionsOnOffset) {
                        velocityX = bb.calculateXOffset(entityBoundingBox, velocityX);
                    }

                    entityBoundingBox.add(velocityX, 0.0D, 0.0D);

                    for (BoundingBox bb : collisionsOnOffset) {
                        velocityZ = bb.calculateZOffset(entityBoundingBox, velocityZ);
                    }

                    entityBoundingBox.add(0.0D, 0.0D, velocityZ);

                    step: {
                        final boolean invalid = !ground
                                && offsetY == velocityY
                                || velocityY > 0.0D
                                || offsetX == velocityX
                                && offsetZ == velocityZ;
                        if (invalid) {
                            break step;
                        }

                        final double copyX = velocityX;
                        final double copyY = velocityY;
                        final double copyZ = velocityZ;

                        final BoundingBox offsetCopy = entityBoundingBox.cloneBB();
                        entityBoundingBox = pos.getBox();

                        velocityY = 0.6F;

                        final PlayerPosition offset = pos.clone().add(offsetX, velocityY, offsetZ);
                        final Set<BoundingBox> colliding = this.getCollidingBB(loc.getChunk(), offset);

                        BoundingBox copyBB = entityBoundingBox.cloneBB();
                        final BoundingBox offsetBB = entityBoundingBox.add(offsetX, 0.0D, offsetZ);

                        double y = velocityY;

                        for (BoundingBox bb : colliding) {
                            y = bb.calculateYOffset(offsetBB, y);
                        }

                        copyBB = copyBB.offset(0.0D, y, 0.0D);
                        double x = offsetX;

                        for (BoundingBox bb : colliding) {
                            x = bb.calculateXOffset(copyBB, x);
                        }

                        copyBB = copyBB.offset(x, 0.0D, 0.0D);
                        double z = velocityZ;

                        for (BoundingBox bb : colliding) {
                            z = bb.calculateZOffset(copyBB, z);
                        }

                        copyBB = copyBB.offset(0.0D, 0.0D, z);

                        BoundingBox second = pos.getBox();
                        double y2 = velocityY;

                        for (BoundingBox bb : colliding) {
                            y2 = bb.calculateYOffset(second, y2);
                        }

                        second = second.offset(0.0D, y2, 0.0D);
                        double x2 = offsetX;

                        for (BoundingBox bb : colliding) {
                            x2 = bb.calculateXOffset(second, x2);
                        }

                        second = second.offset(x2, 0.0D, 0.0D);
                        double z2 = offsetZ;

                        for (BoundingBox bb : colliding) {
                            z2 = bb.calculateZOffset(second, z2);
                        }

                        second = second.offset(0.0D, 0.0D, z2);
                        double d20 = x * x + z * z;
                        double d10 = x2 * x2 + z2 * z2;

                        if (d20 > d10) {
                            velocityX = x;
                            velocityZ = z;
                            velocityY = -y;
                            entityBoundingBox = copyBB;
                        } else {
                            velocityX = x2;
                            velocityZ = z2;
                            velocityY = -y2;
                            entityBoundingBox = second;
                        }

                        for (BoundingBox bb : colliding) {
                            velocityY = bb.calculateYOffset(entityBoundingBox, y2);
                        }

                        entityBoundingBox = entityBoundingBox.offset(0.0D, velocityY, 0.0D);

                        if (copyX * copyX + copyZ * copyZ >= velocityX * velocityX + velocityZ * velocityZ) {
                            entityBoundingBox = offsetCopy;
                        }
                    }


                    final double x = (entityBoundingBox.minX + entityBoundingBox.maxX) / 2.0D;
                    final double y = entityBoundingBox.minY;
                    final double z = (entityBoundingBox.minZ + entityBoundingBox.maxZ) / 2.0D;

                    final Point predicted = new Point(x, y, z);

                    final double distance = received.squareDistanceTo(predicted);

                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                    }
                }
            }
        }
        return shortestDistance;
    }

    public Set<BoundingBox> getCollidingBB(Chunk chunk, PlayerPosition pos) {

        final int minX = (int) (pos.getMinX());
        final int maxX = (int) (pos.getMaxX() + 1.0D);
        final int minY = (int) (pos.getMinY());
        final int maxY = (int) (pos.getMaxY() + 1.0D);
        final int minZ = (int) (pos.getMinZ());
        final int maxZ = (int) (pos.getMaxZ() + 1.0D);

        final Set<BoundingBox> boundingBoxes = new HashSet<>();

        for (int x = minX; x < maxX; ++x) {
            for (int z = minZ; z < maxZ; ++z) {
                final Block temp = BlockUtil.getBlockAsync(pos.getWorld(), x, minY, z);

                if (temp == null) continue;

                for (int y = minY - 1; y < maxY; ++y) {
                    final Block block = BlockUtil.getBlockAsync(pos.getWorld(), x, y, z);
                    boundingBoxes.add(NMSManager.getInms().getBoundingBoxBlock(block));
                }
            }
        }

        for (Entity entity : chunk.getEntities()) {
            final BoundingBox boundingBox = NMSManager.getInms().getEntityBoundingBox(entity);

            if (!pos.getBox().intersectsWith(boundingBox)) continue;

            boundingBoxes.add(boundingBox);
        }

        return boundingBoxes;
    }

    public float getSlipperiness(PlayerPosition position) {
        final Block block = BlockUtil.getBlockAsync(position.toBukkitLocation());

        if (block == null) return 0.6F;

        switch (NMSMaterial.matchXMaterial(block.getType())) {
            case SLIME_BLOCK:
                return 0.8F;
            case ICE:
            case PACKED_ICE:
            case BLUE_ICE:
            case FROSTED_ICE:
                return 0.98F;
            default:
                return 0.6F;
        }
    }

    public float getSpeedModifier() {
        return data.getPlayer().getWalkSpeed() / 2.F;
    }

    public float getSpeedEffectModifier() {
        return 1.F + (float) ((data.getPlayer().getActivePotionEffects()
                .stream()
                .filter(e -> e.getType().equals(PotionEffectType.SPEED))
                .findFirst()
                .orElseThrow(IllegalStateException::new)
                .getAmplifier()) * (double) 0.2F); // This is how you do lossy conversion
    }


}
