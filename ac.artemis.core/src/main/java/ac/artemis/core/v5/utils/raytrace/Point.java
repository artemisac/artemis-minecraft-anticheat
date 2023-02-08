package ac.artemis.core.v5.utils.raytrace;

import ac.artemis.packet.minecraft.block.BlockFace;
import cc.ghast.packet.nms.MathHelper;
import cc.ghast.packet.wrapper.bukkit.Vector3D;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ghast
 * @since 19-May-20
 */

@Getter
@Setter
public class Point implements Cloneable {
    private double x, y, z;

    public Point(double x, double y, double z) {

        if (x == -0.0D) {
            x = 0.0D;
        }

        if (y == -0.0D) {
            y = 0.0D;
        }

        if (z == -0.0D) {
            z = 0.0D;
        }

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(final Point pos){
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public Point(final Vector3D vec){
        this(vec.getX(), vec.getY(), vec.getZ());
    }

    public Point(final NaivePoint vec){
        this(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Returns a new vector with the result of the specified vector minus this.
     */
    public Point subtractReverse(final Point vec)
    {
        return new Point(vec.x - this.x, vec.y - this.y, vec.z - this.z);
    }

    /**
     * Normalizes the vector to a length of 1 (except if it is the zero vector)
     */
    public Point normalize()
    {
        final double d0 = MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        return d0 < 1.0E-4D ? new Point(0.0D, 0.0D, 0.0D) : new Point(this.x / d0, this.y / d0, this.z / d0);
    }

    public double dotProduct(final Point vec)
    {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    /**
     * Returns a new vector with the result of this vector x the specified vector.
     */
    public Point crossProduct(final Point vec)
    {
        return new Point(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
    }

    public Point subtract(final Point vec)
    {
        return this.subtract(vec.x, vec.y, vec.z);
    }

    public Point subtract(final double x, final double y, final double z)
    {
        return this.addVector(-x, -y, -z);
    }

    public Point add(final Point vec)
    {
        return this.addVector(vec.x, vec.y, vec.z);
    }

    /**
     * Adds the specified x,y,z vector components to this vector and returns the resulting vector. Does not change this
     * vector.
     */
    public Point addVector(final double x, final double y, final double z)
    {
        return new Point(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Euclidean distance between this and the specified vector, returned as double.
     */
    public double distanceTo(final Point vec)
    {
        final double d0 = vec.x - this.x;
        final double d1 = vec.y - this.y;
        final double d2 = vec.z - this.z;
        return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    /**
     * Euclidean distance between this and the specified vector, returned as double.
     */
    public double distanceXZTo(final Point vec)
    {
        final double d0 = vec.x - this.x;
        final double d2 = vec.z - this.z;
        return MathHelper.sqrt(d0 * d0 + d2 * d2);
    }

    /**
     * The square of the Euclidean distance between this and the specified vector.
     */
    public double squareDistanceTo(final Point vec)
    {
        final double d0 = vec.x - this.x;
        final double d1 = vec.y - this.y;
        final double d2 = vec.z - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * The square of the Euclidean distance between this and the specified vector.
     */
    public double squareScaledDistanceTo(final Point vec, final double x, final double y, final double z)
    {
        final double d0 = vec.x - this.x;
        final double d1 = vec.y - this.y;
        final double d2 = vec.z - this.z;
        return d0 * d0 * x + d1 * d1 * y + d2 * d2 * z;
    }

    /**
     * The square of the Euclidean distance between this and the specified vector.
     */
    public double squareDistanceXZTo(final Point vec)
    {
        final double d0 = vec.x - this.x;
        final double d2 = vec.z - this.z;
        return d0 * d0 + d2 * d2;
    }

    /**
     * The square of the Euclidean distance between this and the specified vector.
     */
    public double squareDistanceYTo(final Point vec)
    {
        return vec.y - y;
    }

    /**
     * Returns the length of the vector.
     */
    public double lengthVector()
    {
        return MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double lengthXZSquared() {
        return this.x * this.x + this.z * this.z;
    }

    public Point getRelative(final BlockFace blockFace) {
        return new Point(
                x + blockFace.getModX(),
                y + blockFace.getModY(),
                z + blockFace.getModZ()
        );
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Point getIntermediateWithXValue(final Point vec, final double x)
    {
        final double d0 = vec.x - this.x;
        final double d1 = vec.y - this.y;
        final double d2 = vec.z - this.z;

        if (d0 * d0 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            final double d3 = (x - this.x) / d0;
            return d3 >= 0.0D && d3 <= 1.0D ? new Point(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Point getIntermediateWithYValue(final Point vec, final double y)
    {
        final double d0 = vec.x - this.x;
        final double d1 = vec.y - this.y;
        final double d2 = vec.z - this.z;

        if (d1 * d1 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            final double d3 = (y - this.y) / d1;
            return d3 >= 0.0D && d3 <= 1.0D ? new Point(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Point getIntermediateWithZValue(final Point vec, final double z)
    {
        final double d0 = vec.x - this.x;
        final double d1 = vec.y - this.y;
        final double d2 = vec.z - this.z;

        if (d2 * d2 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            final double d3 = (z - this.z) / d2;
            return d3 >= 0.0D && d3 <= 1.0D ? new Point(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }


    public Point rotatePitch(final float pitch)
    {
        final float f = MathHelper.cos(pitch);
        final float f1 = MathHelper.sin(pitch);
        final double d0 = this.x;
        final double d1 = this.y * (double)f + this.z * (double)f1;
        final double d2 = this.z * (double)f - this.y * (double)f1;
        return new Point(d0, d1, d2);
    }

    public Point rotateYaw(final float yaw)
    {
        final float f = MathHelper.cos(yaw);
        final float f1 = MathHelper.sin(yaw);
        final double d0 = this.x * (double)f + this.z * (double)f1;
        final double d1 = this.y;
        final double d2 = this.z * (double)f - this.x * (double)f1;
        return new Point(d0, d1, d2);
    }

    public int getBlockX() {
        return MathHelper.floor(x);
    }

    public int getBlockY() {
        return MathHelper.floor(y);
    }

    public int getBlockZ() {
        return MathHelper.floor(z);
    }

    public NaivePoint toBlockPost() {
        return new NaivePoint(this.getBlockX(), this.getBlockY(), this.getBlockZ());
    }

    public Point scale(final double factor) {
        return this.mul(factor, factor, factor);
    }

    public Point mul(final double factorX, final double factorY, final double factorZ) {
        return new Point(this.x * factorX, this.y * factorY, this.z * factorZ);
    }

    public double dot(final Point other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public float angle(final Point other) {
        final double dot = this.dot(other) / (this.lengthVector() * other.lengthVector());
        return (float) Math.acos(dot);
    }

    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    @Override
    public Point clone() {
        return new Point(x, y, z);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
