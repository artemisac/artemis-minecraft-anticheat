package ac.artemis.anticheat.engine.v2.liquid.impl;

import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.attributes.AttributeMap;
import ac.artemis.core.v5.emulator.attributes.EntityAttributes;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.block.impl.BlockLava;
import ac.artemis.core.v5.emulator.block.impl.BlockWater;
import ac.artemis.core.v5.emulator.modal.Motion;
import ac.artemis.anticheat.engine.v2.liquid.LiquidCollisionProvider;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;
import cc.ghast.packet.nms.MathHelper;

public class LegacyLiquidCollisionProvider implements LiquidCollisionProvider {
    @Override
    public Motion provideMotion(TransitionData emulator, AttributeMap attributeMap, Motion motion) {
        final BoundingBox bb = emulator.getBoundingBox().cloneBB()
                .expand(0.0D, -0.4000000059604645D, 0.0D)
                .contract(0.001D, 0.001D, 0.001D);
        final int minX = MathHelper.floor(bb.minX);
        final int minY = MathHelper.floor(bb.minY);
        final int minZ = MathHelper.floor(bb.minZ);
        final int maxX = MathHelper.floor(bb.maxX + 1.0D);
        final int maxY = MathHelper.floor(bb.maxY + 1.0D);
        final int maxZ = MathHelper.floor(bb.maxZ + 1.0D);

        if (!emulator.getData().getEntity().getWorld().isLoaded(minX, minZ, maxX, maxZ)) {
            return motion;
        } else {
            boolean water = false;
            boolean lava = false;
            Point accel = new Point(0.0D, 0.0D, 0.0D);

            for (int x = minX; x < maxX; ++x) {
                for (int y = minY; y < maxY; ++y) {
                    for (int z = minZ; z < maxZ; ++z) {
                        final NaivePoint bp = new NaivePoint(x,y,z);
                        final Block block = emulator.getData().getEntity().getWorld().getBlockAt(bp.getX(), bp.getY(), bp.getZ());

                        if (block instanceof BlockWater) {
                            final BlockWater blockWater = (BlockWater) block;
                            double maxHeight = (float)(y + 1) - blockWater.getHeight();

                            if ((double) maxY >= maxHeight) {
                                water = true;
                                accel = blockWater.modifyAcceleration(emulator, bp, accel);
                            }
                        } else if (block instanceof BlockLava) {
                            lava = true;
                            break;
                        }
                    }
                }
            }

            if (water && !lava && accel.lengthVector() > 0.0D
                    && !emulator.getData().getPlayer().isAllowedFlight()) {
                accel = accel.normalize();
                double d1 = 0.014D;

                motion.setX(motion.getX() + accel.getX() * d1);
                motion.setY(motion.getY() + accel.getY() * d1);
                motion.setZ(motion.getZ() + accel.getZ() * d1);
            }

            if (water && !lava) {
                attributeMap.get(EntityAttributes.WATER).set(true);
                attributeMap.get(EntityAttributes.LAVA).set(false);
            } else if (lava) {
                attributeMap.get(EntityAttributes.WATER).set(false);
                attributeMap.get(EntityAttributes.LAVA).set(true);
            } else {
                attributeMap.get(EntityAttributes.WATER).set(false);
                attributeMap.get(EntityAttributes.LAVA).set(false);
            }

            return motion;
        }
    }
}
