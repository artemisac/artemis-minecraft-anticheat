package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockFenceGate extends BlockDirectional {

    private boolean open;

    public BlockFenceGate(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.OAK_FENCE_GATE, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        if (!open) {
            final EnumFacing.Axis axis = direction.getAxis();

            if (axis == EnumFacing.Axis.Z) {
                return Collections.singletonList(getFromPoint(location, 0.0F, 0.0F, 0.375F, 1.0F, 1.5F, 0.625F));
            } else {
                return Collections.singletonList(getFromPoint(location, 0.375F, 0.0F, 0.0F, 0.625F, 1.5F, 1.0F));
            }
        }

        return new ArrayList<>();
    }

    @Override
    public void readData(final int data) {
        this.direction = EnumFacing.getHorizontal(data);
        this.open = (data & 4) != 0;
    }
}
