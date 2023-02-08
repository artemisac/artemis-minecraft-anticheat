package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.Collections;
import java.util.List;

public class BlockCocoa extends Block {

    private int age;

    public BlockCocoa(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.COCOA, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        BoundingBox boundingBox = null;

        final int i = age;
        final int j = 4 + i * 2;
        final int k = 5 + i * 2;
        final float f = (float) j / 2.0F;

        switch (direction) {
            case SOUTH:
                boundingBox = getFromPoint(location, (8.0F - f) / 16.0F, (12.0F - (float)k) / 16.0F, (15.0F - (float)j) / 16.0F, (8.0F + f) / 16.0F, 0.75F, 0.9375F);
                break;

            case NORTH:
                boundingBox = getFromPoint(location, (8.0F - f) / 16.0F, (12.0F - (float)k) / 16.0F, 0.0625F, (8.0F + f) / 16.0F, 0.75F, (1.0F + (float)j) / 16.0F);
                break;

            case WEST:
                boundingBox = getFromPoint(location, 0.0625F, (12.0F - (float)k) / 16.0F, (8.0F - f) / 16.0F, (1.0F + (float)j) / 16.0F, 0.75F, (8.0F + f) / 16.0F);
                break;

            case EAST:
                boundingBox = getFromPoint(location, (15.0F - (float)j) / 16.0F, (12.0F - (float)k) / 16.0F, (8.0F - f) / 16.0F, 0.9375F, 0.75F, (8.0F + f) / 16.0F);
        }

        return Collections.singletonList(boundingBox);
    }


    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    @Override
    public void readData(final int data) {
        this.setAge((data & 15) >> 2);
    }
}
