package ac.artemis.core.v5.utils.raytrace;

import ac.artemis.packet.minecraft.block.BlockFace;
import cc.ghast.packet.nms.EnumDirection;
import cc.ghast.packet.nms.MathHelper;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import ac.artemis.packet.minecraft.block.BlockFace;

import java.util.Objects;

@AllArgsConstructor
@Data
public class NaivePoint {
    protected int x, y, z;

    public NaivePoint(final double x, final double y, final double z) {
        this.x = (int) MathHelper.floor(x);
        this.y = (int) MathHelper.floor(y);
        this.z = (int) MathHelper.floor(z);
    }

    public NaivePoint getRelative(final BlockFace blockFace) {
        return new NaivePoint(x + blockFace.getModX(), y + blockFace.getModY(), z + blockFace.getModZ());
    }

    public BlockPosition toBlockPost() {
        return new BlockPosition(this.getX(), this.getY(), this.getZ());
    }

    /**
     * Offset this BlockPos 1 block in the given direction
     */
    public NaivePoint offset(final EnumDirection facing) {
        return this.offset(facing, 1);
    }

    /**
     * Offsets this BlockPos n blocks in the given direction
     */
    public NaivePoint offset(final EnumDirection facing, final int n) {
        return n == 0 ? this : new NaivePoint(this.getX() + facing.getAdjacentX(), this.getY() + facing.getAdjacentY(), this.getZ() + facing.getAdjacentZ());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final NaivePoint that = (NaivePoint) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "x=" + x +
                ", y=" + y +
                ", z=" + z;
    }
}
