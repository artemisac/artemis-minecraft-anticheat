package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ghast
 * @since 16/02/2021
 * Artemis © 2021
 */
@Getter
public class BlockSlab extends BlockDirectional {

    private EnumHalf half;
    private boolean doubled;

    public BlockSlab(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.COBBLESTONE_SLAB, location, direction);
    }

    public static boolean isStairs(final Block block) {
        return block instanceof BlockSlab;
    }

    @Override
    public boolean canCollide() {
        return true;
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        final List<BoundingBox> boundingBoxes = new ArrayList<>();

        if (doubled) {
            boundingBoxes.add(getFromPoint(location, 0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F));
            return boundingBoxes;
        }

        if (half == EnumHalf.TOP) {
            boundingBoxes.add(getFromPoint(location, 0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F));
        } else {
            boundingBoxes.add(getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F));
        }

        return boundingBoxes;
    }

    @Override
    public void readData(final int data) {
        if (!isDoubled()) {
            this.half = (data & 8) == 0 ? EnumHalf.BOTTOM : EnumHalf.TOP;
        }
    }

    public void setHalf(final EnumHalf half) {
        this.half = half;
    }

    public void setDoubled(final boolean doubled) {
        this.doubled = doubled;
    }

    public boolean isSameStair(final Block block) {
        return isStairs(block) && ((BlockSlab) block).getHalf().equals(this.getHalf()) && block.getDirection().equals(this.getDirection());
    }

    public enum EnumHalf {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        EnumHalf(final String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum EnumShape {
        STRAIGHT("straight"),
        INNER_LEFT("inner_left"),
        INNER_RIGHT("inner_right"),
        OUTER_LEFT("outer_left"),
        OUTER_RIGHT("outer_right");

        private final String name;

        EnumShape(final String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }

}
