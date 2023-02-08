package ac.artemis.checks.regular.v2.checks.impl.speed;

import ac.artemis.anticheat.api.check.type.Stage;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.TeleportHandler;
import ac.artemis.core.v4.check.VelocityHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.entity.utils.MoveUtil;
import ac.artemis.core.v4.emulator.magic.Magic;
import ac.artemis.core.v4.emulator.move.Motion;
import ac.artemis.core.v4.emulator.potion.Potion;
import ac.artemis.core.v4.emulator.potion.PotionEffect;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.utils.blocks.BlockUtil;
import ac.artemis.core.v4.utils.position.ModifiableFlyingLocation;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.utils.EntityUtil;
import ac.artemis.core.v5.utils.MathUtil;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.nms.MathHelper;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 25/03/2021
 * Artemis © 2021
 */

@Check(type = Type.SPEED, var = "Horizontal", threshold = 25)
@Experimental(stage = Stage.PRE_RELEASE)
public class SpeedHorizontalComplex extends ArtemisCheck implements VelocityHandler, PacketHandler, TeleportHandler {
    public SpeedHorizontalComplex(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private double motionX;
    private double motionY;
    private double motionZ;

    private float buffer = 0;

    @Override
    public void handle(GPacket packet) {
        /*
         * In here we handle all of the movement and so and forth. This is considered to be the heart of this
         * system. More particularly, it represents the onTick formula
         */
        if (packet instanceof PacketPlayClientFlying) {
            /*
             * We set our basic values for this as I needed to use many different packets to account for
             * velocity and teleports
             */
            final PlayerPosition from = data.prediction.getLastPosition();
            final PlayerPosition to = data.prediction.getPosition();

            final Point next = to.toPoint();

            final float rotationYaw = data.prediction.getYaw();
            final double deltaX = to.getX() - from.getX();
            final double deltaY = to.getY() - from.getY();
            final double deltaZ = to.getZ() - from.getZ();

            /*
             * Since we're looping through a bunch of stuff we need to have a smallest motion defined
             */
            Motion smallest = new Motion(999,999,999, 0.0F, 0.0F, false, false);
            double shortestDistance = 999;


            /*
             * LOOP #1 - Move Forward/Move Strafe
             * We in here provide a loop with both the values for forward and strafe. It doesn't actually matter
             * that much which one we press, all that matters is that our velocity is remotely accurate
             */
            for (int i = 0; i < 9; i++) {
                final int[] vars = MoveUtil.getMoveStrafe(i);
                final int x = vars[0];
                final int z = vars[1];

                /*
                 * LOOP #2 - Jumping
                 * You may either desire to make a gravity check or whatnot, but this is essentially super important
                 * for proper jumping. There's too many factors that can screw with a normal jump accounting, so it's
                 * best to actually have it looped
                 */
                for (final boolean jumping : new boolean[] {false, true}) {

                    /*
                     * LOOP #3 - Sprinting
                     * Same as for jumping, the metadata and sprint factor are desynced. It is hence annoying to patch
                     * wtapping and other variants of sprint 'abuse'. Hence, we just loop through it. This cannot cause
                     * an unfair advantage
                     */
                    for (final boolean sprinting : new boolean[] {false, true}) {

                        /*
                         * Loop #4 - Ground
                         * This is what I like to call 'risquer'. Yes, you'll need a ground spoof check. Yes, this will
                         * let groundspoof bypass and cause exploits. But that's not the purpose of this check. If you've
                         * got checks that detect roughly everything else and just want something solid to base yourself
                         * on, this is for you.
                         */
                        for (final boolean grounded : new boolean[] {false, true}) {
                            for (boolean using : new boolean[] {false, true}) {
                                /*
                                 * As per the physics engine, we multiply by 0.98F
                                 */
                                float forward = x * 0.98F;
                                float strafe = z * 0.98F;

                                /*
                                 * Sneakin' bastard
                                 */
                                if (data.user.isSneaking()) {
                                    forward *= (float) 0.3D;
                                    strafe *= (float) 0.3D;
                                }

                                /*
                                 * Usin' an item bastard
                                 */
                                if (using) {
                                    forward *= 0.2D;
                                    strafe *= 0.2D;
                                }

                                /*
                                 * Here we get the estimated maximum flying motion and compare it to the shortest.
                                 * Works roughly decently. When it doesn't it still doesn't go above 1.0. Note to mention
                                 * this is exclusively on an XZ basis
                                 */
                                final Motion motion = this.getMoveOffset(forward, strafe, rotationYaw, jumping, sprinting, grounded);

                                final Point distance = motion.toPoint();
                                final double motionDistance = Math.abs(next.squareDistanceTo(from.toPoint().add(distance)));

                                if (motionDistance < shortestDistance) {
                                    smallest = motion;
                                    shortestDistance = motionDistance;
                                }
                            }
                        }
                    }
                }
            }

            /*
             * Here we make two vectors of the distance travelled and debug it.
             */
            final Point delta = new Point(deltaX, deltaY, deltaZ);
            final Point predict = new Point(smallest.getX(), smallest.getY(), smallest.getZ());

            //this.debug(smallest.toString());

            log: {
                /*
                 * Here we do a casual comparison. We add 0.0005 to prevent an invalid numerical operation.
                 * That's about it really. Not much to comment on.
                 */
                final double deltaxyz = Math.abs(delta.lengthXZSquared());
                final double predictxyz = Math.abs(predict.lengthXZSquared());
                final double movementSpeed = MathUtil.roundToPlace(Math.max(0.0D, deltaxyz - predictxyz), 8);

                this.debug("offset=" + movementSpeed + " distance=" + Math.abs(predictxyz - deltaxyz));
                //this.debug("speed=" + movementSpeed + " buffer=" + buffer + " xz=" + delta.lengthXZSquared() + " motionXZ=" + predict.lengthXZSquared());

                if (!data.prediction.isPos() || !data.prediction.isLastPos()) {
                    debug("----gay----");
                    break log;
                }

                if (movementSpeed < 0.001) {
                    this.buffer = Math.max(buffer - 2, 0);
                    break log;
                }

                this.buffer = Math.min(500, buffer + 10); //We do this to prevent integer overflow.

                if (buffer > 60) {
                    this.log(
                            new Debug<>("speed", movementSpeed),
                            new Debug<>("buffer", buffer),
                            new Debug<>("gotXZ", delta.lengthXZSquared()),
                            new Debug<>("expectedXZ", predict.lengthXZSquared())
                    );

                    this.buffer /= 2;
                }
            }

            /*
             * We update the motion with out smallest
             */
            this.motionX = deltaX;
            this.motionY = deltaY;
            this.motionZ = deltaZ;

            if (!smallest.isLiquid()) {
                /*
                 * Hi. This is gravity. It likes to pull shit down. Say hi to gravity people.
                 */
                this.motionY -= smallest.getGravity();
            }

            /*
             * And just the casual frictional forces.
             */
            this.motionX *= smallest.getFriction();
            this.motionY *= smallest.getDrag();
            this.motionZ *= smallest.getFriction();

            if (smallest.isLiquid()) {
                /*
                 * Hi. This is gravity. It likes to pull shit down. Say hi to gravity people.
                 */
                this.motionY -= smallest.getGravity();
            }
        }

        else if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity use = (GPacketPlayClientUseEntity) packet;

            final Entity entity = NMSManager.getInms().getEntity(data.getPlayer().getWorld(), use.getEntityId());
            final boolean player = entity instanceof Player;

            if (player && !entity.isDead() && data.entity.getPlayerControls().isSprint()) {
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }
        }
    }

    @Override
    public void handle(Velocity velocity) {
        /*
         * Here we handle confirmed velocity. This is a cool system I implemented. I need to improve it
         * slightly nonetheless.
         */
        this.motionX = velocity.getX();
        this.motionY = velocity.getY();
        this.motionZ = velocity.getZ();

        this.debug("Velocity");
    }

    @Override
    public void handle(ModifiableFlyingLocation confirmedLocation) {
        this.motionY = 0.0D;
    }

    private Motion getMoveOffset(float forward, float strafe, float yaw, boolean jumping, boolean sprinting, boolean ground) {
        final boolean water = data.getEntity().isInWater();
        final boolean lava = data.getEntity().isInLava();
        final boolean flying = false;
        final boolean fucked = data.getVersion().isOrAbove(ProtocolVersion.V1_13);

        /*
         * We create a new motion with all the factors, including the ones we bruteforce
         * this will allow for us to properly handle movement.
         */
        final Motion motion = new Motion(motionX, motionY, motionZ, forward, strafe, jumping, sprinting);

        if (jumping) {
            if (water || lava) {
                motion.setY(motion.getY() + 0.03999999910593033D);
            } else {
                motion.setY(0.42f);

                if (data.entity.isPotionActive(Potion.jump)) {
                    motion.addY((float)(data.entity.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
                }

                if (sprinting) {
                    final float radiansYaw = yaw * 0.017453292F;
                    motion.addX(-((double) MathHelper.sin(radiansYaw) * 0.2F));
                    motion.addZ((double) MathHelper.cos(radiansYaw) * 0.2F);
                }
            }
        }

        if (water && !flying) {
            final double d0 = data.prediction.getLastY();
            float frictionFactor = 0.8F;
            float drag = 0.02F;
            float strider = (float) EntityUtil.getDepthStrider(data.getPlayer());

            if (strider > 3.0F) {
                strider = 3.0F;
            }

            if (!ground) {
                strider *= 0.5F;
            }

            if (strider > 0.0F) {
                frictionFactor += (0.54600006F - frictionFactor) * strider / 3.0F;
                drag += (this.getSpeed(sprinting) * 1.0F - drag) * strider / 3.0F;
            }

            // Move the entity's motion
            double[] flyings = fucked
                    ? MoveUtil.moveFlyingNew(yaw, motion.getX(), motion.getZ(), strafe, forward, drag)
                    : MoveUtil.moveFlying(yaw, motion.getX(), motion.getZ(), strafe, forward, drag);

            motion.setX(flyings[0]);
            motion.setZ(flyings[1]);
            motion.setFriction(frictionFactor);
            motion.setDrag(0.80F);
            motion.setGravity(0.02D);
            motion.setLiquid(true);

            return motion;
        }

        else if (lava && !flying) {
            // Move the entity's motion
            double[] flyings = fucked
                    ? MoveUtil.moveFlyingNew(yaw, motion.getX(), motion.getZ(), strafe, forward, 0.02F)
                    : MoveUtil.moveFlying(yaw, motion.getX(), motion.getZ(), strafe, forward, 0.02F);

            motion.setX(flyings[0]);
            motion.setZ(flyings[1]);
            motion.setFriction((float) 0.50D);
            motion.setDrag((float) 0.50D);
            motion.setGravity(0.02D);
            motion.setLiquid(true);

            return motion;
        }

        else {
            final double speed = this.getSpeed(sprinting);

            // Grab the magic friction value, equivalent of 0.91F
            float friction = Magic.FRICTION;

            // These values are directly from NMS, quite useful most the time, these have to be improved nonetheless
            // Check if the user was on ground before as we're a tick behind since we're predicting the position
            // Apply the block slipperiness to the friction
            final double remove = fucked ? 0.5000001D : 1.D;

            final float slipperiness = this.getFrictionAtBB(remove);

            if (ground) {
                friction *= slipperiness;
            }

            // This is the odd value "f" is in the formula.
            final float tempFriction = fucked
                    ? 0.21600002F / (slipperiness * slipperiness * slipperiness)
                    : 0.16277136F / (friction * friction * friction);

            float shiftedFriction;

            if (ground) {
                shiftedFriction = (float) (speed * tempFriction);
            } else {
                shiftedFriction = (float) (sprinting ? ((double) 0.02F + (double) 0.02F * 0.3D) : 0.02F);
            }

            // Move the entity's motion
            double[] flyings = fucked
                    ? MoveUtil.moveFlyingNew(yaw, motion.getX(), motion.getZ(), strafe, forward, shiftedFriction)
                    : MoveUtil.moveFlying(yaw, motion.getX(), motion.getZ(), strafe, forward, shiftedFriction);

            motion.setX(flyings[0]);
            motion.setZ(flyings[1]);
            motion.setGravity(0.08D);
            motion.setFriction(friction);
            motion.setDrag(0.98F);
            motion.setLiquid(false);
            return motion;
        }
    }

    public float getFrictionAtBB(double remove) {
        final int x = MathHelper.floor(data.prediction.getX());
        final int y = MathHelper.floor((MathHelper.floor(data.prediction.getY()) - remove));
        final int z = MathHelper.floor(data.prediction.getZ());

        final Block block = data.getEntity().getWorld().getBlockAt(x, y, z);

        return block == null || block.getMaterial() == null
                ? 0.6F
                : BlockUtil.getSlipperiness(block.getMaterial());
    }
    private double getSpeed(boolean sprinting) {
        // Speed is multiplied by 2 for some reason. Thanks CraftBukkit. Fuck you.
        double speed = data.getPlayer().getWalkSpeed() / 2.F;

        if (sprinting)
            speed *= 1.3F;

        final PotionEffect effect = data.entity.getActivePotionEffect(Potion.moveSpeed);

        if (effect != null) {
            speed *= 1.F + .2F * (effect.getAmplifier() + 1);
        }

        return speed;
    }

    private boolean almost(double a, double b) {
        return Math.abs(a - b) < 1E-3;
    }
}