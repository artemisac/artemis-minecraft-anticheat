package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.ArrayList;
import java.util.List;

public class BlockEnderFrame extends Block {

    private boolean eye;

    public BlockEnderFrame(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.END_PORTAL_FRAME, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final List<BoundingBox> boundingBoxes = new ArrayList<>();

        if (eye) {
            boundingBoxes.add(getFromPoint(location, 0.3125F, 0.8125F, 0.3125F, 0.6875F, 1.0F, 0.6875F));
        }

        boundingBoxes.add(getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F));

        return boundingBoxes;
    }


    public boolean isEye() {
        return eye;
    }

    public void setEye(final boolean eye) {
        this.eye = eye;
    }

    @Override
    public void readData(final int data) {
        this.setEye((data & 4) != 0);
    }
}
