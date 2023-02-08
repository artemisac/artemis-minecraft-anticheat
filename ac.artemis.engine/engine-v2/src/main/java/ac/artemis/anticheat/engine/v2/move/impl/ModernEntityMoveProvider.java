package ac.artemis.anticheat.engine.v2.move.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.block.CollisionBlock;
import ac.artemis.anticheat.engine.v2.block.BlockCollisionFactory;
import ac.artemis.anticheat.engine.v2.block.BlockCollisionProvider;
import ac.artemis.anticheat.engine.v2.liquid.LiquidCollisionFactory;
import ac.artemis.anticheat.engine.v2.liquid.LiquidCollisionProvider;
import ac.artemis.anticheat.engine.v2.move.EntityMoveProvider;
import ac.artemis.core.v5.emulator.utils.OutputAction;
import ac.artemis.core.v5.emulator.attributes.EntityAttributes;
import ac.artemis.core.v5.emulator.modal.Motion;
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
public class ModernEntityMoveProvider implements EntityMoveProvider {
    private final BlockCollisionProvider blockCollisionProvider = new BlockCollisionFactory().build();
    private final LiquidCollisionProvider liquidCollisionProvider = new LiquidCollisionFactory().build();

    @Override
    public TransitionData provide(final ArtemisData emulator, final TransitionData input) {
        double x = input.getMotionX();
        double y = input.getMotionY();
        double z = input.getMotionZ();

        if (input.isNoClip(emulator)) {
            final BoundingBox offset = input.getBoundingBox().offset(x, y, z);
            return input
                    .setBoundingBox(offset)
                    .setX(offset.getX())
                    .setY(offset.getY())
                    .setZ(offset.getZ());
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

                for (; x != 0.0D && emulator.getCollidingBoxes(input.getBoundingBox()
                        .offset(x, -1.0D, 0.0D), compensate).isEmpty();
                     copyX = x) {
                    if (x < offset && x >= -offset) {
                        x = 0.0D;
                    } else if (x > 0.0D) {
                        x -= offset;
                    } else {
                        x += offset;
                    }
                }

                for (; z != 0.0D && emulator.getCollidingBoxes(input.getBoundingBox()
                        .offset(0.0D, -1.0D, z), compensate).isEmpty();
                     copyZ = z) {
                    if (z < offset && z >= -offset) {
                        z = 0.0D;
                    } else if (z > 0.0D) {
                        z -= offset;
                    } else {
                        z += offset;
                    }
                }

                for (; x != 0.0D && z != 0.0D
                        && emulator.getCollidingBoxes(input.getBoundingBox()
                        .offset(x, -1.0D, z), compensate).isEmpty();
                     copyZ = z) {
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

            final boolean isGravityGround = emulator.isPreviousGround() || copyY != y && copyY < 0.0D;
            final boolean invert = Math.abs(x) < Math.abs(z);

            if (invert) {
                // Z
                for (BoundingBox axisalignedbb13 : motionColliding) {
                    z = axisalignedbb13.calculateZOffset(entityBoundingBox, z);
                }
                entityBoundingBox = entityBoundingBox.offset(0.0D, 0.0D, z);

                // X
                for (BoundingBox axisalignedbb2 : motionColliding) {
                    x = axisalignedbb2.calculateXOffset(entityBoundingBox, x);
                }
                entityBoundingBox = entityBoundingBox.offset(x, 0.0D, 0.0D);

            } else {
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

            }

            step: {
                final boolean flag = input.getStepHeight() > 0.0F && isGravityGround && (copyX != x || copyZ != z);

                if (!flag)
                    break step;

                double motionX = x;
                double motionY = y;
                double motionZ = z;

                BoundingBox backupMotionBoundingBox = entityBoundingBox;
                entityBoundingBox = axisalignedbb;

                y = input.getStepHeight();

                List<BoundingBox> list = emulator.getCollidingBoxes(
                        entityBoundingBox.addCoord(copyX, y, copyZ), compensate);

                BoundingBox stepBoundingBoxMotion = entityBoundingBox;
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
                    .setMotionX(x)
                    .setMotionY(y)
                    .setMotionZ(z)
                    .addAction(runnables)
                    .addAction(new OutputAction() {
                        @Override
                        public void accept(TransitionData outputData) {
                            outputData.setLastCollidedHorizontally(emulator.isCollidedHorizontally());
                            outputData.setCollidedHorizontally(finalCopyX != finalX || finalCopyZ != finalZ);
                            outputData.setCollidedVertically(copyY != finalY);
                            outputData.setCollidedGround(outputData.isCollidedVertically() && copyY < 0.0D);
                            outputData.setCollided(outputData.isCollidedHorizontally() || outputData.isCollidedVertically());

                            final int blockX = MathHelper.floor_double(outputData.getX());
                            final int blockY = MathHelper.floor_double(outputData.getY() - 0.20000000298023224D);
                            final int blockZ = MathHelper.floor_double(outputData.getZ());

                            Point blockPos = new Point(blockX, blockY, blockZ).getRelative(BlockFace.DOWN);

                            Block block = emulator.getWorld().getBlockAt(blockX, blockY, blockZ);

                            if (block != null && block.getMaterial().equals(NMSMaterial.AIR)) {
                                Block below = emulator.getWorld().getBlockAt(blockPos.getBlockX(), blockPos.getBlockY(), blockPos.getBlockZ());

                                if (below != null) {
                                    NMSMaterial material = below.getMaterial();

                                    if (BlockUtil.isFence(material)
                                            || BlockUtil.isFenceGate(material)
                                            || BlockUtil.isWall(material)) {
                                        block = below;
                                        blockPos = blockPos.addVector(0, -1, 0);
                                    }
                                }
                            }

                            if (block != null) {
                                ac.artemis.core.v5.emulator.block.Block block1 = emulator.getWorld().getBlockAt(
                                        block.getLocation().getX(),
                                        block.getLocation().getY(),
                                        block.getLocation().getZ()
                                );

                                if (Math.abs(copyY - finalY) > 1E-12D) {
                                    block1.onLanded(input);
                                }

                                if (!input.isInWater()) {
                                    final Motion motion = liquidCollisionProvider.provideMotion(
                                            input,
                                            input.getAttributeMap(),
                                            new Motion(input.getMotionX(), input.getMotionY(), input.getMotionZ())
                                    );
                                    input.setMotionX(motion.getX());
                                    input.setMotionY(motion.getY());
                                    input.setMotionZ(motion.getZ());
                                }

                                if (input.isCollidedGround() && !input.isSneaking() && block1 instanceof CollisionBlock) {
                                    final CollisionBlock collisionBlock = (CollisionBlock) block1;
                                    collisionBlock.onCollidedBlock(input);
                                }
                            }

                            if (finalCopyX != finalX) {
                                input.setMotionX(0.0D);
                            }

                            if (finalCopyZ != finalZ) {
                                input.setMotionZ(0.0D);
                            }

                            // if (emulator.canTriggerWalking() && !(emulator.isGround() && emulator.isSneaking() )
                            blockCollisionProvider.doBlockCollisions(input);

                        }
            });
        }
    }
}
