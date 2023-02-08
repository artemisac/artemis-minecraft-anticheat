package ac.artemis.core.v5.utils.bounding;

import cc.ghast.packet.nms.MathHelper;

public class Vec3i implements Comparable<Vec3i> {
    /**
     * The Null vector constant (0, 0, 0)
     */
    public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);

    /**
     * X coordinate
     */
    private final int x;

    /**
     * Y coordinate
     */
    private final int y;

    /**
     * Z coordinate
     */
    private final int z;

    public Vec3i() {
        this(0, 0, 0);
    }

    public Vec3i(final int xIn, final int yIn, final int zIn) {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
    }

    public Vec3i(final double xIn, final double yIn, final double zIn) {
        this(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
    }

    public boolean equals(final Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof Vec3i)) {
            return false;
        } else {
            final Vec3i vec3i = (Vec3i) p_equals_1_;
            return this.getX() == vec3i.getX() && (this.getY() == vec3i.getY() && this.getZ() == vec3i.getZ());
        }
    }

    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    public int compareTo(final Vec3i p_compareTo_1_) {
        return this.getY() == p_compareTo_1_.getY() ? (this.getZ() == p_compareTo_1_.getZ() ? this.getX() - p_compareTo_1_.getX() : this.getZ() - p_compareTo_1_.getZ()) : this.getY() - p_compareTo_1_.getY();
    }

    /**
     * Get the X coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Get the Y coordinate
     */
    public int getY() {
        return this.y;
    }

    /**
     * Get the Z coordinate
     */
    public int getZ() {
        return this.z;
    }

    /**
     * Calculate the cross product of this and the given Vector
     */
    public Vec3i crossProduct(final Vec3i vec) {
        return new Vec3i(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    /**
     * Calculate squared distance to the given coordinates
     */
    public double distanceSq(final double toX, final double toY, final double toZ) {
        final double d0 = (double) this.getX() - toX;
        final double d1 = (double) this.getY() - toY;
        final double d2 = (double) this.getZ() - toZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * Compute square of distance from point x, y, z to center of this Block
     */
    public double distanceSqToCenter(final double xIn, final double yIn, final double zIn) {
        final double d0 = (double) this.getX() + 0.5D - xIn;
        final double d1 = (double) this.getY() + 0.5D - yIn;
        final double d2 = (double) this.getZ() + 0.5D - zIn;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * Calculate squared distance to the given Vector
     */
    public double distanceSq(final Vec3i to) {
        return this.distanceSq(to.getX(), to.getY(), to.getZ());
    }

    @Override
    public String toString() {
        return "Vec3i{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
