package ac.artemis.core.v4.emulator.entity.utils;

import ac.artemis.packet.minecraft.Minecraft;
import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.core.v4.emulator.moderna.ModernaMathHelper;
import ac.artemis.core.v4.utils.blocks.BlockUtil;
import ac.artemis.core.v5.emulator.Emulator;
import ac.artemis.core.v5.utils.minecraft.MathHelper;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.emulator.magic.Magic;
import ac.artemis.core.v4.nms.minecraft.INMS;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.packet.protocol.ProtocolVersion;

import java.util.List;

/**
 * @author Ghast
 * @since 17/08/2020
 * Artemis © 2020
 */
public class MoveUtil {
    public static int[] getMoveForwardIteration(Emulator base, Point received) {
        double shortestDistance = 999.d;
        double maxDistance = 0;
        int[] shortest = {0,0,0,0,0,0,0};
        Point shortestPoint = received;

        final boolean invalid = !base.isWasPos();

        if (invalid) return shortest;

        final boolean isFucked = base.getData().getVersion().isOrAbove(ProtocolVersion.V1_15);

        for (int i = 0; i < 9; i++) {
            final int[] vars = getMoveStrafe(i);
            final int x = vars[0];
            final int z = vars[1];

            for (int jump = 0; jump < 2; jump++) {
                final boolean jumping = jump == 1;

                for (int sprint = 0; sprint < 2; sprint++) {
                    final boolean sprinting = sprint == 1;

                    for (int vel = 0; vel < 2; vel++) {
                        final boolean velocity = vel == 1;

                        if (velocity && base.getData().prediction.getQueuedVelocity().peek() == null)
                            continue;

                        // We exempt velocity thingy right now because it ain't needed
                        /*if (velocity)
                            continue;*/

                        for (int eat = 0; eat < 2; eat++) {
                            final boolean eating = eat == 1;

                            /*if (eating && (TimeUtil.elapsed(base.getLastItemInUse(), 50L * 3) || !base.isUsingItem()))
                                continue;*/
                            for (int fly = -1; fly < 2; fly++) {
                                if (fly != 0 && !base.getCapabilities().isFlying()) continue;

                                float forward = (float) x * Magic.MOVE_BIND_MODIFIER;
                                float strafe = (float) z * Magic.MOVE_BIND_MODIFIER;

                                if (base.isSneaking()) {
                                    forward *= (float) 0.3D;
                                    strafe *= (float) 0.3D;
                                }

                                if (eating) {
                                    forward *= 0.2D;
                                    strafe *= 0.2D;
                                }

                                final float deltaY = (float) (received.getY() - base.getPosition().getY());

                                if (deltaY >= 0.1175F && deltaY <= 0.1177F && base.isOnLadder() && x + z == 0) {
                                    continue;
                                }

                                // Increase the motion by the magic value!
                                double motionX = velocity
                                        ? base.getData().prediction.getQueuedVelocity().peek().getX()
                                        : base.getMotionX();
                                double motionY = velocity
                                        ? base.getData().prediction.getQueuedVelocity().peek().getY()
                                        : base.getMotionY();
                                double motionZ = velocity
                                        ? base.getData().prediction.getQueuedVelocity().peek().getZ()
                                        : base.getMotionZ();

                                // Fly patch
                                motionY += base.getCapabilities().getFlySpeed() * 3.0F * fly;

                                if (jumping) {
                                    if (base.isInWater() || base.isInLava()) {
                                        motionY += Magic.AI_TICK_MODIFIER;
                                    }
                                    else {
                                        motionY = Magic.JUMP_UPWARDS_MOTION;

                                        if (base.getData().getPlayer().hasEffect(PotionEffectType.JUMP)) {
                                            // TODO Do the potion thingy instead of relying on this old hag's shit
                                            motionY += (float) (base.getJumpBoostAmplifier(base.getData().getPlayer())) * 0.1F;
                                        }

                                        // If user is sprinting, add the sprinting velocity
                                        if (sprinting) {
                                            // Get the jump rotation yaw
                                            if (isFucked) {
                                                float f1 = base.getRotationYaw() * ((float) Math.PI / 180F);
                                                motionX += -ModernaMathHelper.sin(f1) * 0.2F;
                                                motionZ += ModernaMathHelper.cos(f1) * 0.2F;

                                            } else {
                                                float f = base.getRotationYaw() * Magic.MOTION_XZ_YAW_JUMP_MODIFIER;
                                                motionX -= MathHelper.sin(f) * Magic.MOTION_XZ_JUMP_MODIFIER;
                                                motionZ += MathHelper.cos(f) * Magic.MOTION_XZ_JUMP_MODIFIER;
                                            }
                                        }
                                    }

                                }

                                regular: {
                                    Point d = arrayToPoint(moveEntityWithHeading(base, motionX, motionY, motionZ, forward, strafe, sprinting));
                                    final double dist = received.squareDistanceTo(d);
                                    final double length = base.getData().prediction.getLastLocation().squareDistanceTo(d);

                                    if (length > maxDistance) {
                                        maxDistance = length;
                                    }

                                    if (dist > shortestDistance) break regular;

                                    shortestDistance = dist;
                                    shortestPoint = d;
                                    shortest[0] = x;
                                    shortest[1] = z;
                                    shortest[2] = jump;
                                    shortest[3] = 0;
                                    shortest[4] = sprint;
                                    shortest[5] = vel;
                                    shortest[6] = eat;
                                }
                            }



                        /*ladder: {
                            if (!base.getData().getCollision().getCollidingMaterials1().contains(NMSMaterial.LADDER)
                                    && !base.getData().getCollision().getCollidingMaterials1().contains(NMSMaterial.VINE))
                                break ladder;

                            Point d = arrayToPoint(moveEntityWithHeading(base,
                                    motionX,
                                    0.1176D, // (0.2 - 0.08) * 0.98D
                                    motionZ,
                                    forward, strafe)
                            );
                            final double dist = received.squareDistanceTo(d);

                            if (dist >= shortestDistance) break ladder;

                            shortestDistance = dist;
                            shortestPoint = d;
                            shortest[0] = x;
                            shortest[1] = z;
                            shortest[2] = jump;
                            shortest[3] = 1;
                        }*/
                        }
                    }

                }

            }
        }

        base.setMaxDistance(maxDistance);

        // Return the distance
        return shortest;
    }

    public static int[] getMoveStrafe(int index) {
        switch (index) {
            case 0: return new int[]{-1,-1};
            case 1: return new int[]{-1,1};
            case 2: return new int[]{0,1};
            case 3: return new int[]{0,-1};
            case 4: return new int[]{-1,0};
            case 5: return new int[]{1,1};
            case 6: return new int[]{1,-1};
            case 7: return new int[]{0,0};
            default: return new int[]{1,0};
        }
    }

    public static Point arrayToPoint(double[] array){
        return new Point(array[0], array[1], array[2]);
    }

    /**
     * Moves the entity based on the specified heading. Args: strafe, forward
     * @param entity Entity being iterated
     * @param forward MoveForward
     * @param strafe MoveStrafing
     */
    public static double[] moveEntityWithHeading(Emulator entity, double motionX, double motionY, double motionZ,
                                                 float forward, float strafe, boolean sprinting) {
        final boolean isFucked = entity.getData().getVersion().isOrAbove(ProtocolVersion.V1_15);

        double fallDistance = entity.getFallDistance();
        // If the chunk is loaded
        if (entity.isChunkLoaded()) {

            // If the user does not collide with water nor is flying
            if (!entity.isInWater() || entity.isFlying()) {

                // If the user does not collide with lava nor is flying
                if (!entity.isInLava() || entity.isFlying()) {

                    float jumpMovementFactor = Magic.JUMP_MOVE_FACTOR;

                    if (sprinting) {
                        jumpMovementFactor = isFucked
                                ? (float) ((double)jumpMovementFactor + 0.005999999865889549D)
                                : (float) ((double) jumpMovementFactor + (double) Magic.JUMP_MOVE_FACTOR * 0.3D)
                        ;
                    }

                    if (entity.isFlying()) {
                        jumpMovementFactor = entity.getCapabilities().getFlySpeed() * (float) (sprinting ? 2 : 1);
                    }

                    // These values are directly from NMS, quite useful most the time, these have to be improved nonetheless
                    double aiMoveSpeed = entity.getAIMoveSpeed();

                    // Grab the magic friction value, equivalent of 0.91F
                    float friction = Magic.FRICTION;

                    // These values are directly from NMS, quite useful most the time, these have to be improved nonetheless
                    // Check if the user was on ground before as we're a tick behind since we're predicting the position
                    // Apply the block slipperiness to the friction
                    final double remove = isFucked ? 0.5000001D : 1.D;

                    final float slipperiness = BlockUtil.getSlipperiness(
                            Minecraft.v().createLocation(
                                    entity.getData().getPlayer().getWorld(),
                                    cc.ghast.packet.nms.MathHelper.floor(entity.getPosition().getX()),
                                    cc.ghast.packet.nms.MathHelper.floor(entity.getPosition().getY()) - remove,
                                    cc.ghast.packet.nms.MathHelper.floor(entity.getPosition().getZ())
                            )
                    );

                    if (entity.getData().prediction.isLastGround()) {
                        friction *= slipperiness;
                    }

                    // This is the odd value "f" is in the formula.
                    final float tempFriction = isFucked
                            ? 0.21600002F / (slipperiness * slipperiness * slipperiness)
                            : 0.16277136F / (friction * friction * friction);

                    float shiftedFriction;

                    if (entity.getData().prediction.isLastGround()) {
                        shiftedFriction = (float) (aiMoveSpeed * tempFriction);
                    } else {
                        shiftedFriction = jumpMovementFactor;
                    }

                    // Move the entity's motion
                    double[] motion = isFucked
                            ? moveFlyingNew(entity.getRotationYaw(), motionX, motionZ, strafe, forward, shiftedFriction)
                            : moveFlying(entity.getRotationPitch(), motionX, motionZ, strafe, forward, shiftedFriction);

                    motionX = motion[0];
                    motionZ = motion[1];

                    // This bit is entirely taken from NMS. It'll make the motionY of a data static if such user
                    // Is colliding with a ladder.
                    if (entity.isOnLadder()) {
                        float f6 = Magic.MOTION_H_MAX_LADDER;
                        motionX = MathHelper.clamp_double(motionX, -f6, f6);
                        motionZ = MathHelper.clamp_double(motionZ, -f6, f6);

                        if (motionY < -Magic.MOTION_Y_MAX_LADDER) {
                            motionY = -Magic.MOTION_Y_MAX_LADDER;
                        }

                        boolean flag = entity.isSneaking();

                        if (flag && motionY < 0.0D) {
                            motionY = 0.0D;
                        }
                    }

                    // Moves the entity based on it's motion
                    return /*isFucked
                            ? moveEntityNew((BntityPlayerXYZ_1_13) entity, motionX, motionY, motionZ)
                            :*/ moveEntity(entity, motionX, motionY, motionZ);
                }

                // LAVA COLLISION / FLYING
                else {
                    // Move with a low friction
                    double[] motion = moveFlying(entity.getRotationYaw(), motionX, motionZ, strafe, forward, 0.02F);
                    motionX = motion[0];
                    motionZ = motion[1];
                    return moveEntity(entity, motionX, motionY, motionZ);
                }
            }
            // LIQUID COLLISION / FLYING
            else {
                double d0 = entity.getPosition().getY();
                float f1 = 0.8F;
                float f2 = 0.02F;
                float f3 = (float) ac.artemis.core.v5.utils.EntityUtil.getDepthStrider(entity.getData().getPlayer());

                if (f3 > 3.0F) {
                    f3 = 3.0F;
                }

                if (!entity.isOnGround()) {
                    f3 *= 0.5F;
                }

                if (f3 > 0.0F) {
                    f1 += (0.54600006F - f1) * f3 / 3.0F;
                    f2 += (entity.getAIMoveSpeed() - f2) * f3 / 3.0F;
                }

                double[] motion = moveFlying(entity.getRotationYaw(), motionX, motionZ, strafe, forward, f2);
                motionX = motion[0];
                motionZ = motion[1];
                return moveEntity(entity, motionX, motionY, motionZ);
            }

            // Todo LimbSwing?
        }
        return getFromAxis(entity.getEntityBoundingBox());
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    public static double[] moveEntity(Emulator entity, double x, double y, double z) {
        // If the entity is in noClip, just return the motion's equivalent in offset as we're not colliding with anything
        if (entity.isNoClip()) {
            BoundingBox bb = entity.getEntityBoundingBox().offset(x, y, z);

            // Return the getFromAxis bounding box
            return getFromAxis(bb);
        } else {
            // Get the NMS Loader
            INMS inms = NMSManager.getInms();

            // Create some saved samples to restore them later
            double d3 = x;
            double d4 = y;
            double d5 = z;

            // If the user is in a web, reset the status (updated later)
            if (entity.isInWeb()) {
                // Apply motion modifiers; Makes user super slow as you can see
                x *= 0.25D;
                y *= 0.05000000074505806D;
                z *= 0.25D;
            }

            // If the user is sneaking
            final boolean sneaking = entity.isOnGround() && entity.isSneaking();

            BoundingBox box = entity.getEntityBoundingBox().cloneBB();

            if (sneaking) {
                double d6;

                for (d6 = 0.05D; x != 0.0D && inms.getCollidingBoxes(entity.getData().getPlayer(),
                        box.offset(x, -1.0D, 0.0D), entity.getGhostBlocks()).isEmpty();
                     d3 = x) {
                    if (x < d6 && x >= -d6) {
                        x = 0.0D;
                    } else if (x > 0.0D) {
                        x -= d6;
                    } else {
                        x += d6;
                    }
                }

                for (; z != 0.0D && inms.getCollidingBoxes(entity.getData().getPlayer(),
                        box.offset(0.0D, -1.0D, z), entity.getGhostBlocks()).isEmpty();
                     d5 = z) {
                    if (z < d6 && z >= -d6) {
                        z = 0.0D;
                    } else if (z > 0.0D) {
                        z -= d6;
                    } else {
                        z += d6;
                    }
                }

                for (; x != 0.0D && z != 0.0D
                        && inms.getCollidingBoxes(entity.getData().getPlayer(),
                        box.offset(x, -1.0D, z), entity.getGhostBlocks()).isEmpty();
                     d5 = z) {
                    if (x < d6 && x >= -d6) {
                        x = 0.0D;
                    } else if (x > 0.0D) {
                        x -= d6;
                    } else {
                        x += d6;
                    }

                    d3 = x;

                    if (z < d6 && z >= -d6) {
                        z = 0.0D;
                    } else if (z > 0.0D) {
                        z -= d6;
                    } else {
                        z += d6;
                    }
                }
            }

            List<BoundingBox> list1 = inms.getCollidingBoxes(entity.getData().getPlayer(),
                    box.addCoord(x, y, z), entity.getGhostBlocks());

            //boolean flag1 = processor.user.isOnGround() || d4 != y && d4 < 0.0D;
            BoundingBox axisalignedbb = box;
            // X
            for (BoundingBox axisalignedbb1 : list1) {
                y = axisalignedbb1.calculateYOffset(box, y);
            }
            box = box.offset(0.0D, y, 0.0D);
            boolean flag1 = entity.isOnGround() || d4 != y && d4 < 0.0D;
            // Y
            for (BoundingBox axisalignedbb2 : list1) {
                x = axisalignedbb2.calculateXOffset(box, x);
            }
            box = box.offset(x, 0.0D, 0.0D);

            // Z
            for (BoundingBox axisalignedbb13 : list1) {
                z = axisalignedbb13.calculateZOffset(box, z);
            }
            box = box.offset(0.0D, 0.0D, z);

            if (flag1 && (d3 != x || d5 != z)) {
                double d11 = x;
                double d7 = y;
                double d8 = z;
                BoundingBox axisalignedbb3 = box;
                box = axisalignedbb;
                y = entity.getStepHeight();
                List<BoundingBox> list = NMSManager.getInms().getCollidingBoxes(entity.getData().getPlayer(),
                        box.addCoord(d3, y, d5), entity.getGhostBlocks());
                BoundingBox axisalignedbb4 = box;
                BoundingBox axisalignedbb5 = axisalignedbb4.addCoord(d3, 0.0D, d5);
                double d9 = y;

                for (BoundingBox axisalignedbb6 : list) {
                    d9 = axisalignedbb6.calculateYOffset(axisalignedbb5, d9);
                }

                axisalignedbb4 = axisalignedbb4.offset(0.0D, d9, 0.0D);
                double d15 = d3;

                for (BoundingBox axisalignedbb7 : list) {
                    d15 = axisalignedbb7.calculateXOffset(axisalignedbb4, d15);
                }

                axisalignedbb4 = axisalignedbb4.offset(d15, 0.0D, 0.0D);
                double d16 = d5;

                for (BoundingBox axisalignedbb8 : list) {
                    d16 = axisalignedbb8.calculateZOffset(axisalignedbb4, d16);
                }

                axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, d16);
                BoundingBox axisalignedbb14 = box;
                double d17 = y;

                for (BoundingBox axisalignedbb9 : list) {
                    d17 = axisalignedbb9.calculateYOffset(axisalignedbb14, d17);
                }

                axisalignedbb14 = axisalignedbb14.offset(0.0D, d17, 0.0D);
                double d18 = d3;

                for (BoundingBox axisalignedbb10 : list) {
                    d18 = axisalignedbb10.calculateXOffset(axisalignedbb14, d18);
                }

                axisalignedbb14 = axisalignedbb14.offset(d18, 0.0D, 0.0D);
                double d19 = d5;

                for (BoundingBox axisalignedbb11 : list) {
                    d19 = axisalignedbb11.calculateZOffset(axisalignedbb14, d19);
                }

                axisalignedbb14 = axisalignedbb14.offset(0.0D, 0.0D, d19);
                double d20 = d15 * d15 + d16 * d16;
                double d10 = d18 * d18 + d19 * d19;

                if (d20 > d10) {
                    x = d15;
                    z = d16;
                    y = -d9;
                    box = axisalignedbb4;
                } else {
                    x = d18;
                    z = d19;
                    y = -d17;
                    box = axisalignedbb14;
                }

                for (BoundingBox axisalignedbb12 : list) {
                    y = axisalignedbb12.calculateYOffset(box, y);
                }

                box = box.offset(0.0D, y, 0.0D);

                if (d11 * d11 + d8 * d8 >= x * x + z * z) {
                    box = axisalignedbb3;
                }
            }

            // Update collided horizontally
            return getFromAxis(box);
            // TODO Handle the rest afterwards
        }
    }

    public static double[] getFromAxis(BoundingBox bb){
        return new double[]{(bb.minX + bb.maxX) / 2.0D, bb.minY, (bb.minZ + bb.maxZ) / 2.0D};
    }

    /**
     * Taken directly from NMS. This is what updates properly the MotionXZ of a data
     *
     * @param strafe   Horizontal (to the data) movement based on the Euler Axis
     * @param forward  Vertical (to the data) movement based on the Euler Axis
     * @param friction Friction of the current data
     */
    public static double[] moveFlying(float rotationYaw, double motionX, double motionZ, float strafe, float forward, float friction) {
        float f = strafe * strafe + forward * forward;

        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt_float(f);

            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;
            float f1 = MathHelper.sin(rotationYaw * (float) Math.PI / 180.0F);
            float f2 = MathHelper.cos(rotationYaw * (float) Math.PI / 180.0F);
            motionX += strafe * f2 - forward * f1;
            motionZ += forward * f2 + strafe * f1;
        }
        return new double[]{motionX, motionZ};
    }

    public static double[] moveFlyingNew(float rotationYaw, double motionX, double motionZ, float strafe, float forward, float friction) {
        Point point = new Point(strafe, 0.D, forward);
        final double d0 = point.lengthSquared();
        if (d0 < 1.0E-7D) {
            return new double[]{motionX, motionZ};
        } else {
            Point vector3d = (d0 > 1.0D ? point.normalize() : point).scale(friction);
            float f = ModernaMathHelper.sin(rotationYaw * ((float)Math.PI / 180F));
            float f1 = ModernaMathHelper.cos(rotationYaw * ((float)Math.PI / 180F));

            motionX += vector3d.getX() * (double)f1 - vector3d.getZ() * (double)f;
            motionZ += vector3d.getZ() * (double)f1 + vector3d.getX() * (double)f;

            return new double[]{motionX, motionZ};
        }
    }
}
