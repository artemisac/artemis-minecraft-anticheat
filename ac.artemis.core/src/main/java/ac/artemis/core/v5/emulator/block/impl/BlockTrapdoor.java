package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.Collections;
import java.util.List;

/**
 * @author Ghast
 * @since 12/02/2021
 * Artemis © 2021
 */
public class BlockTrapdoor extends Block {

    private Half half;
    private boolean open;

    public BlockTrapdoor(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.OAK_TRAPDOOR, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final boolean flag = this.half == Half.TOP;
        final BoundingBox boundingBox;

        if (open) {
            switch (direction) {
                case NORTH:
                    boundingBox = Block.getFromPoint(location, 0.0F, 0.0F, 0.8125F, 1.0F, 1.0F, 1.0F);
                    break;
                case SOUTH:
                    boundingBox = Block.getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1875F);
                    break;
                case WEST:
                    boundingBox = Block.getFromPoint(location, 0.8125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case EAST:
                    boundingBox = Block.getFromPoint(location, 0.0F, 0.0F, 0.0F, 0.1875F, 1.0F, 1.0F);
                    break;
                default:
                    boundingBox = null;
                    break;
            }
        } else {
            if (flag) {
                boundingBox = Block.getFromPoint(location, 0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
                boundingBox = Block.getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
            }
        }

        return Collections.singletonList(boundingBox);
    }

    public void toggle(final boolean var) {
        this.open = var;
    }

    public void invert(final boolean var) {
        this.half = var ? Half.BOTTOM : Half.TOP;
    }

    @Override
    public void readData(final int data) {
        this.toggle((data & 4) != 0);
        this.invert((data & 8) == 0);
    }

    public enum Half {

        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        Half(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }
}
