package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.Collections;
import java.util.List;

public class BlockVine extends Block {

    public BlockVine(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.VINE, location, direction);
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        BoundingBox boundingBox;

        final float f = 0.0625F;
        float f1 = 1.0F;
        float f2 = 1.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag = false;

        switch (direction) {
            case WEST:
                f4 = Math.max(f4, 0.0625F);
                f1 = 0.0F;
                f2 = 0.0F;
                f5 = 1.0F;
                f3 = 0.0F;
                f6 = 1.0F;
                flag = true;
                break;
            case EAST:
                f1 = Math.min(f1, 0.9375F);
                f4 = 1.0F;
                f2 = 0.0F;
                f5 = 1.0F;
                f3 = 0.0F;
                f6 = 1.0F;
                flag = true;
                break;
            case NORTH:
                f6 = Math.max(f6, 0.0625F);
                f3 = 0.0F;
                f1 = 0.0F;
                f4 = 1.0F;
                f2 = 0.0F;
                f5 = 1.0F;
                flag = true;
                break;
            case SOUTH:
                f3 = Math.min(f3, 0.9375F);
                f6 = 1.0F;
                f1 = 0.0F;
                f4 = 1.0F;
                f2 = 0.0F;
                f5 = 1.0F;
                flag = true;
                break;
        }

        return Collections.singletonList(new BoundingBox(f1, f2, f3, f4, f5, f6));
    }

}
