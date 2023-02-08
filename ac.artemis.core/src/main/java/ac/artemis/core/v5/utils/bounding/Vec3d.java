package ac.artemis.core.v5.utils.bounding;

import java.util.Objects;

public class Vec3d implements Comparable<Vec3d> {
    /**
     * The Null vector constant (0, 0, 0)
     */
    public static final Vec3d NULL_VECTOR = new Vec3d(0, 0, 0);

    /**
     * X coordinate
     */
    private final double x;

    /**
     * Y coordinate
     */
    private final double y;

    /**
     * Z coordinate
     */
    private final double z;

    public Vec3d() {
        this(0.D, 0.D, 0.D);
    }

    public Vec3d(final double xIn, final double yIn, final double zIn) {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
    }

    public boolean equals(final Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof Vec3d)) {
            return false;
        } else {
            final Vec3d vec3i = (Vec3d) p_equals_1_;
            return this.getX() == vec3i.getX() && (this.getY() == vec3i.getY() && this.getZ() == vec3i.getZ());
        }
    }

    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public int compareTo(Vec3d o) {
        return Double.compare(x, o.x) + Double.compare(y, o.y) + Double.compare(z, o.z);
    }

    /**
     * Get the X coordinate
     */
    public double getX() {
        return this.x;
    }

    /**
     * Get the Y coordinate
     */
    public double getY() {
        return this.y;
    }

    /**
     * Get the Z coordinate
     */
    public double getZ() {
        return this.z;
    }

    /**
     * Calculate the cross product of this and the given Vector
     */
    public Vec3d crossProduct(final Vec3d vec) {
        return new Vec3d(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
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
     * Compute square of distance from podouble x, y, z to center of this Block
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
    public double distanceSq(final Vec3d to) {
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
