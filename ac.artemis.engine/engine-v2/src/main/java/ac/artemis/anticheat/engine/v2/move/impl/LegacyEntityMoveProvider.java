package ac.artemis.anticheat.engine.v2.move.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.anticheat.engine.v2.block.BlockCollisionFactory;
import ac.artemis.anticheat.engine.v2.block.BlockCollisionProvider;
import ac.artemis.anticheat.engine.v2.liquid.LiquidCollisionFactory;
import ac.artemis.anticheat.engine.v2.liquid.LiquidCollisionProvider;
import ac.artemis.anticheat.engine.v2.move.EntityMoveProvider;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.attributes.EntityAttributes;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.block.CollisionBlock;
import ac.artemis.core.v5.emulator.modal.Motion;
import ac.artemis.core.v5.emulator.tags.Tags;
import ac.artemis.core.v5.emulator.utils.OutputAction;
import ac.artemis.core.v5.utils.block.BlockUtil;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.minecraft.MathHelper;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.packet.minecraft.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ghast
 * @since 03/02/2021
 * Artemis © 2021
 */
public class LegacyEntityMoveProvider implements EntityMoveProvider {
    private final BlockCollisionProvider blockCollisionProvider = new BlockCollisionFactory().build();
    private final LiquidCollisionProvider liquidCollisionProvider = new LiquidCollisionFactory().build();

    @Override
    public TransitionData provide(final ArtemisData emulator, final TransitionData input) {
        double x = input.getMotionX();
        double y = input.getMotionY();
        double z = input.getMotionZ();

        final BoundingBox offsetBB = input.getBoundingBox().offset(x, y, z);

        if (input.isNoClip(emulator)) {
            return input
                    .setBoundingBox(offsetBB)
                    .setX(offsetBB.getX())
                    .setY(offsetBB.getY())
                    .setZ(offsetBB.getZ());
        } else {
            final List<OutputAction> runnables = new ArrayList<>();
            final boolean compensate = input.getAttributeMap().poll(EntityAttributes.COMPENSATE_WORLD);
            double copyX = x;
            double copyY = y;
            double copyZ = z;

            if (input.isWeb()) {
                x *= 0.25D;
                y *= 0.05000000074505806D;
                z *= 0.25D;

                runnables.add(new OutputAction() {
                    @Override
                    public void accept(TransitionData outputData) {
                        outputData.push(EntityAttributes.WEB, false);
                        outputData.setMotionX(0.0D);
                        outputData.setMotionY(0.0D);
                        outputData.setMotionZ(0.0D);
                    }
                });
            }

            sneak: {
                final boolean flag = input.isGround() && input.isSneaking();

                if (!flag)
                    break sneak;

                final double offset = 0.05D;

                while (x != 0.0D && emulator.getCollidingBoxes(input.getBoundingBox()
                        .offset(x, -1.0D, 0.0D), compensate).isEmpty()) {
                    if (x < offset && x >= -offset) {
                        x = 0.0D;
                    } else if (x > 0.0D) {
                        x -= offset;
                    } else {
                        x += offset;
                    }

                    copyX = x;
                }

                while (z != 0.0D && emulator.getCollidingBoxes(input.getBoundingBox()
                        .offset(0.0D, -1.0D, z), compensate).isEmpty()) {
                       if (z < offset && z >= -offset) {
                           z = 0.0D;
                       } else if (z > 0.0D) {
                           z -= offset;
                       } else {
                           z += offset;
                       }

                       copyZ = z;
                   }

                while (x != 0.0D && z != 0.0D
                        && emulator.getCollidingBoxes(input.getBoundingBox()
                        .offset(x, -1.0D, z), compensate).isEmpty()) {
                    if (x < offset && x >= -offset) {
                        x = 0.0D;
                    } else if (x > 0.0D) {
                        x -= offset;
                    } else {
                        x += offset;
                    }

                    copyX = x;

                    if (z < offset && z >= -offset) {
                        z = 0.0D;
                    } else if (z > 0.0D) {
                        z -= offset;
                    } else {
                        z += offset;
                    }

                    copyZ = z;
                }
            }

            List<BoundingBox> motionColliding = emulator.getCollidingBoxes(input.getBoundingBox()
                    .addCoord(x, y, z), compensate);

            BoundingBox entityBoundingBox = input.getBoundingBox().cloneBB();

            final BoundingBox axisalignedbb = input.getBoundingBox().cloneBB();
            // Y
            for (BoundingBox axisalignedbb1 : motionColliding) {
                y = axisalignedbb1.calculateYOffset(entityBoundingBox, y);
            }
            entityBoundingBox = entityBoundingBox.offset(0.0D, y, 0.0D);

            final boolean isGravityGround = input.isGround() || copyY != y && copyY < 0.0D;
            // X
            for (BoundingBox axisalignedbb2 : motionColliding) {
                x = axisalignedbb2.calculateXOffset(entityBoundingBox, x);
            }
            entityBoundingBox = entityBoundingBox.offset(x, 0.0D, 0.0D);

            // Z
            for (BoundingBox axisalignedbb13 : motionColliding) {
                z = axisalignedbb13.calculateZOffset(entityBoundingBox, z);
            }
            entityBoundingBox = entityBoundingBox.offset(0.0D, 0.0D, z);

            step:{
                final boolean flag = input.getStepHeight() > 0.0F && isGravityGround && (copyX != x || copyZ != z);

                if (!flag)
                    break step;

                input.addTag(Tags.STEP);

                double motionX = x;
                double motionY = y;
                double motionZ = z;

                final BoundingBox backupMotionBoundingBox = entityBoundingBox.cloneBB();
                entityBoundingBox = axisalignedbb.cloneBB();

                y = input.getStepHeight();

                List<BoundingBox> list = emulator.getCollidingBoxes(
                        entityBoundingBox.addCoord(copyX, y, copyZ), compensate);

                BoundingBox stepBoundingBoxMotion = entityBoundingBox.cloneBB();
                BoundingBox stepBoundingBoxCopy = stepBoundingBoxMotion.addCoord(copyX, 0.0D, copyZ);
                double stepOffsetY = y;

                for (BoundingBox bb : list) {
                    stepOffsetY = bb.calculateYOffset(stepBoundingBoxCopy, stepOffsetY);
                }

                stepBoundingBoxMotion = stepBoundingBoxMotion.offset(0.0D, stepOffsetY, 0.0D);
                double stepOffsetX = copyX;

                for (BoundingBox bb : list) {
                    stepOffsetX = bb.calculateXOffset(stepBoundingBoxMotion, stepOffsetX);
                }

                stepBoundingBoxMotion = stepBoundingBoxMotion.offset(stepOffsetX, 0.0D, 0.0D);
                double stepOffsetZ = copyZ;

                for (BoundingBox bb : list) {
                    stepOffsetZ = bb.calculateZOffset(stepBoundingBoxMotion, stepOffsetZ);
                }

                stepBoundingBoxMotion = stepBoundingBoxMotion.offset(0.0D, 0.0D, stepOffsetZ);
                BoundingBox axisalignedbb14 = entityBoundingBox;
                double d17 = y;

                for (BoundingBox bb : list) {
                    d17 = bb.calculateYOffset(axisalignedbb14, d17);
                }

                axisalignedbb14 = axisalignedbb14.offset(0.0D, d17, 0.0D);
                double d18 = copyX;

                for (BoundingBox bb : list) {
                    d18 = bb.calculateXOffset(axisalignedbb14, d18);
                }

                axisalignedbb14 = axisalignedbb14.offset(d18, 0.0D, 0.0D);
                double d19 = copyZ;

                for (BoundingBox bb : list) {
                    d19 = bb.calculateZOffset(axisalignedbb14, d19);
                }

                axisalignedbb14 = axisalignedbb14.offset(0.0D, 0.0D, d19);
                double d20 = stepOffsetX * stepOffsetX + stepOffsetZ * stepOffsetZ;
                double d10 = d18 * d18 + d19 * d19;

                if (d20 > d10) {
                    x = stepOffsetX;
                    z = stepOffsetZ;
                    y = -stepOffsetY;
                    entityBoundingBox = stepBoundingBoxMotion;
                } else {
                    x = d18;
                    z = d19;
                    y = -d17;
                    entityBoundingBox = axisalignedbb14;
                }

                for (BoundingBox axisalignedbb12 : list) {
                    y = axisalignedbb12.calculateYOffset(entityBoundingBox, y);
                }

                entityBoundingBox = entityBoundingBox.offset(0.0D, y, 0.0D);

                if (motionX * motionX + motionZ * motionZ >= x * x + z * z) {
                    x = motionX;
                    y = motionY;
                    z = motionZ;
                    entityBoundingBox = backupMotionBoundingBox;
                }
            }


            final BoundingBox finalEntityBoundingBox = entityBoundingBox.cloneBB();
            final double finalCopyX = copyX;
            final double finalX = x;
            final double finalCopyZ = copyZ;
            final double finalZ = z;
            final double finalY = y;

            return input
                    .setBoundingBox(finalEntityBoundingBox)
                    .setX(finalEntityBoundingBox.getX())
                    .setY(finalEntityBoundingBox.getY())
                    .setZ(finalEntityBoundingBox.getZ())
                    .addAction(runnables)
                    .addAction(new OutputAction() {
                        @Override
                        public void accept(TransitionData outputData) {
//                            for (BoundingBox collidingBox : emulator.getCollidingBoxes(outputData.getBoundingBox().cloneBB().expand(0.001D, 0.001D, 0.001D), false)) {
//                                BoundingUtil.drawBox(collidingBox, Particle.REDSTONE, outputData.getData());
//                            }

                            outputData.setLastCollidedHorizontally(outputData.isCollidedHorizontally());
                            outputData.setCollidedHorizontally(finalCopyX != finalX || finalCopyZ != finalZ);
                            outputData.setCollidedVertically(copyY != finalY);
                            outputData.setCollidedGround(outputData.isCollidedVertically() && copyY < 0.0D);
                            outputData.setCollided(outputData.isCollidedHorizontally() || outputData.isCollidedVertically());

                            if (!outputData.isDumbFix()) {
                                // Test
                                outputData.setMotionX(outputData.getData().prediction.getDeltaX());
                                outputData.setMotionY(outputData.getData().prediction.getDeltaY());
                                outputData.setMotionZ(outputData.getData().prediction.getDeltaZ());
                            }

                            final int blockX = MathHelper.floor_double(outputData.getX());
                            final int blockY = MathHelper.floor_double(outputData.getY() - 0.20000000298023224D);
                            final int blockZ = MathHelper.floor_double(outputData.getZ());

                            Point blockPos = new Point(blockX, blockY, blockZ).getRelative(BlockFace.DOWN);

                            Block block = emulator.getWorld().getBlockAt(blockX, blockY, blockZ);

                            if (block != null && block.getMaterial().equals(NMSMaterial.AIR)) {
                                blockPos = blockPos.addVector(0, -1, 0);
                                Block below = emulator.getWorld().getBlockAt(blockPos.getBlockX(), blockPos.getBlockY(), blockPos.getBlockZ());

                                if (below != null) {
                                    NMSMaterial material = below.getMaterial();

                                    if (BlockUtil.isFence(material)
                                            || BlockUtil.isFenceGate(material)
                                            || BlockUtil.isWall(material)) {
                                        block = below;
                                    }
                                }
                            }

                            //Bukkit.broadcastMessage(block == null ? "null" : block.toString());

                            if (block != null) {
                                ac.artemis.core.v5.emulator.block.Block block1 = emulator.getWorld().getBlockAt(
                                        block.getLocation().getX(),
                                        block.getLocation().getY(),
                                        block.getLocation().getZ()
                                );

                                if (copyY != finalY) {
                                    block1.onLanded(outputData);

                                    outputData.addTag(Tags.COLLIDE_Y);
                                }

                                //if (outputData.isCollidedVertically()) Bukkit.broadcastMessage(copyY + "");

                                if (!input.isInWater()) {
                                    final Motion motion = liquidCollisionProvider.provideMotion(
                                            outputData,
                                            outputData.getAttributeMap(),
                                            new Motion(outputData.getMotionX(), outputData.getMotionY(), outputData.getMotionZ())
                                    );
                                    outputData.setMotionX(motion.getX());
                                    outputData.setMotionY(motion.getY());
                                    outputData.setMotionZ(motion.getZ());
                                }
                            }

                            else {
                                if (copyY != finalY) {
                                    outputData.setMotionY(0.0D);

                                    outputData.addTag(Tags.COLLIDE_Y_BLOCKLESS);
                                }
                            }

                            if (finalCopyX != finalX) {
                                outputData.setMotionX(0.0D);
                                outputData.addTag(Tags.COLLIDE_X);
                            }

                            if (finalCopyZ != finalZ) {
                                outputData.setMotionZ(0.0D);
                                outputData.addTag(Tags.COLLIDE_Z);
                            }

                            if (block != null) {
                                if (outputData.isCollidedGround() && !outputData.isSneaking() && block instanceof CollisionBlock) {
                                    final CollisionBlock collisionBlock = (CollisionBlock) block;
                                    collisionBlock.onCollidedBlock(outputData);
                                }
                            }

                            // if (emulator.canTriggerWalking() && !(emulator.isGround() && emulator.isSneaking() )
                            blockCollisionProvider.doBlockCollisions(outputData);

                        }
            });
        }
    }
}
