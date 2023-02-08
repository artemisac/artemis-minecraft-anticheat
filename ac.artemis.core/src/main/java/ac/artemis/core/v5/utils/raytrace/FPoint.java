package ac.artemis.core.v5.utils.raytrace;

import ac.artemis.core.v5.utils.bounding.Vec3d;
import cc.ghast.packet.nms.MathHelper;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import lombok.Getter;
import ac.artemis.packet.minecraft.block.BlockFace;

/**
 * @author Ghast
 * @since 19-May-20
 */

@Getter
public class FPoint {
    private final float x, y, z;

    public FPoint(float x, float y, float z) {

        if (x == -0.0F) {
            x = 0.0F;
        }

        if (y == -0.0F) {
            y = 0.0F;
        }

        if (z == -0.0F) {
            z = 0.0F;
        }

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public FPoint(final Point pos){
        this((float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
    }

    public FPoint(final Vec3d vec){
        this((float) vec.getX(), (float) vec.getY(), (float) vec.getZ());
    }

    public FPoint(final NaivePoint vec){
        this(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Returns a new vector with the result of the specified vector minus this.
     */
    public FPoint subtractReverse(final FPoint vec)
    {
        return new FPoint(vec.x - this.x, vec.y - this.y, vec.z - this.z);
    }

    /**
     * Normalizes the vector to a length of 1 (except if it is the zero vector)
     */
    public FPoint normalize()
    {
        final float d0 = MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        return d0 < 1.0E-4D ? new FPoint(0.0F, 0.0F, 0.0F) : new FPoint(this.x / d0, this.y / d0, this.z / d0);
    }

    public float dotProduct(final FPoint vec)
    {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    /**
     * Returns a new vector with the result of this vector x the specified vector.
     */
    public FPoint crossProduct(final FPoint vec)
    {
        return new FPoint(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
    }

    public FPoint subtract(final FPoint vec)
    {
        return this.subtract(vec.x, vec.y, vec.z);
    }

    public FPoint subtract(final float x, final float y, final float z)
    {
        return this.addVector(-x, -y, -z);
    }

    public FPoint add(final FPoint vec)
    {
        return this.addVector(vec.x, vec.y, vec.z);
    }

    /**
     * Adds the specified x,y,z vector components to this vector and returns the resulting vector. Does not change this
     * vector.
     */
    public FPoint addVector(final float x, final float y, final float z)
    {
        return new FPoint(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Euclidean distance between this and the specified vector, returned as float.
     */
    public float distanceTo(final FPoint vec)
    {
        final float d0 = vec.x - this.x;
        final float d1 = vec.y - this.y;
        final float d2 = vec.z - this.z;
        return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    /**
     * The square of the Euclidean distance between this and the specified vector.
     */
    public float squareDistanceTo(final FPoint vec)
    {
        final float d0 = vec.x - this.x;
        final float d1 = vec.y - this.y;
        final float d2 = vec.z - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * Returns the length of the vector.
     */
    public float lengthVector()
    {
        return MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public FPoint getRelative(final BlockFace blockFace) {
        return new FPoint(x + blockFace.getModX(), y + blockFace.getModY(), z + blockFace.getModZ());
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public FPoint getIntermediateWithXValue(final FPoint vec, final float x)
    {
        final float d0 = vec.x - this.x;
        final float d1 = vec.y - this.y;
        final float d2 = vec.z - this.z;

        if (d0 * d0 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            final float d3 = (x - this.x) / d0;
            return d3 >= 0.0F && d3 <= 1.0F ? new FPoint(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public FPoint getIntermediateWithYValue(final FPoint vec, final float y)
    {
        final float d0 = vec.x - this.x;
        final float d1 = vec.y - this.y;
        final float d2 = vec.z - this.z;

        if (d1 * d1 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            final float d3 = (y - this.y) / d1;
            return d3 >= 0.0F && d3 <= 1.0F ? new FPoint(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public FPoint getIntermediateWithZValue(final FPoint vec, final float z)
    {
        final float d0 = vec.x - this.x;
        final float d1 = vec.y - this.y;
        final float d2 = vec.z - this.z;

        if (d2 * d2 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            final float d3 = (z - this.z) / d2;
            return d3 >= 0.0F && d3 <= 1.0F ? new FPoint(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }


    public FPoint rotatePitch(final float pitch)
    {
        final float f = MathHelper.cos(pitch);
        final float f1 = MathHelper.sin(pitch);
        final float d0 = this.x;
        final float d1 = this.y * f + this.z * f1;
        final float d2 = this.z * f - this.y * f1;
        return new FPoint(d0, d1, d2);
    }

    public FPoint rotateYaw(final float yaw)
    {
        final float f = MathHelper.cos(yaw);
        final float f1 = MathHelper.sin(yaw);
        final float d0 = this.x * f + this.z * f1;
        final float d1 = this.y;
        final float d2 = this.z * f - this.x * f1;
        return new FPoint(d0, d1, d2);
    }

    public Point toVector(){
        return new Point(x, y, z);
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

    public BlockPosition toBlockPost() {
        return new BlockPosition(this.getBlockX(), this.getBlockY(), this.getBlockZ());
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
