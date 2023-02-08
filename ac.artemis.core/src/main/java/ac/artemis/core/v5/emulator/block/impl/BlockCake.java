package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.Collections;
import java.util.List;

public class BlockCake extends Block {
    private int bites;

    public BlockCake(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.CAKE, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final float f = 0.0625F;
        final float f1 = (float) (1 + bites * 2) / 16.0F;
        final float f2 = 0.5F;

        return Collections.singletonList(
                new BoundingBox(
                        (float) location.getX() + f1,
                        (float) location.getY(),
                        (float) location.getZ() + f,
                        (float) (location.getX() + 1) - f,
                        (float) location.getY() + f2,
                        (float) (location.getZ() + 1) - f
                )
        );
    }

    public int getBites() {
        return bites;
    }

    public void setBites(final int bites) {
        this.bites = bites;
    }

    @Override
    public void readData(final int data) {
        this.setBites(data);
    }
}
