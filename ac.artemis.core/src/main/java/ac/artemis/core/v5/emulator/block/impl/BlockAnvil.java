package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.Collections;
import java.util.List;

/**
 * @author Ghast
 * @since 19/02/2021
 * Artemis © 2021
 */
public class BlockAnvil extends Block {

    private int damage;

    public BlockAnvil(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.ANVIL, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        // Actual collisions
        final BoundingBox boundingBox;

        if (direction.getAxis() == EnumFacing.Axis.X) {
            boundingBox = getFromPoint(location, 0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
        } else {
            boundingBox = getFromPoint(location, 0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
        }

        return Collections.singletonList(boundingBox);
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(final int damage) {
        this.damage = damage;
    }

    @Override
    public void readData(final int data) {
        this.setDamage((data & 15) >> 2);
    }
}
