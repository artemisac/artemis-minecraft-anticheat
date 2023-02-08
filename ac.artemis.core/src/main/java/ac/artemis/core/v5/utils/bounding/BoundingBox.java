package ac.artemis.core.v5.utils.bounding;

import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.core.v5.utils.block.BlockUtil;
import ac.artemis.core.v5.utils.raytrace.MovingPoint;
import ac.artemis.core.v5.utils.raytrace.Point;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Ghast
 * @since 11-Jan-20
 * Ghast CC © 2019
 */
@Getter
@EqualsAndHashCode
public class BoundingBox {

    public double minX, minY, minZ, maxX, maxY, maxZ;
    private final long timestamp;

    public BoundingBox(final double minX, final double minY, final double minZ,
                       final double maxX, final double maxY, final double maxZ,
                       final long timestamp) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.timestamp = timestamp;
    }

    public BoundingBox(final double minX, final double minY, final double minZ,
                       final double maxX, final double maxY, final double maxZ) {
        this(minX, minY, minZ, maxX, maxY, maxZ, System.currentTimeMillis());
    }

    public BoundingBox(final BoundingBox min, final long timestamp) {
        this(min.getMinX(), min.getMinY(), min.getMinZ(), min.getMaxX(), min.getMaxY(), min.getMaxZ(), timestamp);
    }

    public BoundingBox(final Point min, final Point max, final long timestamp) {
        this.minX = min.getX();
        this.minZ = min.getZ();
        this.minY = min.getY();
        this.maxX = max.getX();
        this.maxY = max.getY();
        this.maxZ = max.getZ();
        this.timestamp = timestamp;
    }

    public BoundingBox shrink(final double x, final double y, final double z) {
        minX += x;
        minY += y;
        minZ += z;
        maxX -= x;
        maxY -= y;
        maxZ -= z;
        return this;
    }

    public BoundingBox expand(final double x, final double y, final double z) {
        minX -= x;
        minY -= y;
        minZ -= z;
        maxX += x;
        maxY += y;
        maxZ += z;
        return this;
    }

    public BoundingBox expandMax(final double x, final double y, final double z) {
        maxX += x;
        maxY += y;
        maxZ += z;
        return this;
    }

    public BoundingBox expandMin(final double x, final double y, final double z) {
        minX -= x;
        minY -= y;
        minZ -= z;
        return this;
    }

    public BoundingBox add(final double x, final double y, final double z) {
        minX += x;
        minY += y;
        minZ += z;
        maxX += x;
        maxY += y;
        maxZ += z;
        return this;
    }

    public BoundingBox subtract(final double x, final double y, final double z) {
        minX -= x;
        minY -= y;
        minZ -= z;
        maxX -= x;
        maxY -= y;
        maxZ -= z;
        return this;
    }

    public BoundingBox subtractMin(final double x, final double y, final double z) {
        minX -= x;
        minY -= y;
        minZ -= z;
        return this;
    }

    public BoundingBox subtractMax(final double x, final double y, final double z) {
        maxX -= x;
        maxY -= y;
        maxZ -= z;
        return this;
    }

    public BoundingBox union(final BoundingBox other) {
        final double d0 = Math.min(this.minX, other.minX);
        final double d1 = Math.min(this.minY, other.minY);
        final double d2 = Math.min(this.minZ, other.minZ);
        final double d3 = Math.max(this.maxX, other.maxX);
        final double d4 = Math.max(this.maxY, other.maxY);
        final double d5 = Math.max(this.maxZ, other.maxZ);
        return new BoundingBox(d0, d1, d2, d3, d4, d5);
    }

    public BoundingBox offset(final double x, final double y, final double z) {
        return new BoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z, System.currentTimeMillis());
    }

    public BoundingBox contract(final double x, final double y, final double z) {
        final double d0 = this.minX + x;
        final double d1 = this.minY + y;
        final double d2 = this.minZ + z;
        final double d3 = this.maxX - x;
        final double d4 = this.maxY - y;
        final double d5 = this.maxZ - z;
        return new BoundingBox(d0, d1, d2, d3, d4, d5);
    }

    public boolean contains(final double x, final double y, final double z) {
        return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
    }

    public double middleX() {
        return (minX + maxX) / 2.0D;
    }

    public double middleY() {
        return (minY + maxY) / 2.0D;
    }

    public double middleZ() {
        return (minZ + maxZ) / 2.0D;
    }

    public double getX() {
        return (minX + maxX) / 2.0D;
    }

    public double getY() {
        return minY;
    }

    public double getZ() {
        return (minZ + maxZ) / 2.0D;
    }


    public double sizeX() {
        return Math.abs(minX - maxX);
    }

    public double sizeZ() {
        return Math.abs(minZ - maxZ);
    }

    public double distance(final BoundingBox box) {

        final double x = Math.abs(middleX() - box.middleX());
        final double y = Math.abs(middleY() - box.middleY());
        final double z = Math.abs(middleZ() - box.middleZ());
        return Math.sqrt(x * x + z * z + y * y);
    }

    public double distanceXZ(final BoundingBox box) {

        final double x = Math.abs(middleX() - box.middleX());
        final double z = Math.abs(middleZ() - box.middleZ());
        return Math.sqrt(x * x + z * z);
    }

    public double distanceXZ(final Point vec) {

        final double x = Math.abs(middleX() - vec.getX());
        final double z = Math.abs(middleZ() - vec.getZ());
        return Math.sqrt(x * x + z * z);
    }

    public double distanceMinMax() {

        // Pythagoras
        final double minXZDiagonal = Math.sqrt((minX * minX) + (minZ * minZ));
        return Math.sqrt((minXZDiagonal * minXZDiagonal) + (maxY * maxY));
    }

    public boolean checkCollision(final Player data, final Predicate<Material> predicate) {
        for (final Material material : collidingMaterials(data)) {
            if (predicate.test(material)) return true;
        }

        return false;
    }

    public Set<Material> collidingMaterials(final Player data) {
        final Set<Material> mats = new HashSet<>();

        final int x = (int) Math.floor(minX);
        final int y = (int) Math.max(Math.floor(minY), 0);
        final int z = (int) Math.floor(minZ);

        final int x2 = (int) Math.floor(maxX + 1.0D);
        final int y2 = (int) Math.floor(maxY + 1.0D);
        final int z2 = (int) Math.floor(maxZ + 1.0D);

        for (int ax = x; ax < x2; ax++) {
            for (int az = z; az < z2; az++) {
                if (data.getWorld().isChunkLoaded(ax >> 4, az >> 4)) {
                    for (int ay = y; ay < y2; ay++) {
                        final Block block = BlockUtil.getBlockAsync(data.getWorld(), ax, ay, az);

                        if (block == null || mats.contains(block.getType())) continue;
                        //Bukkit.broadcastMessage("Hurray found block at x=" + x + " y=" + y + " z=" + z + " of type " + block.getType());
                        mats.add(block.getType());
                    }
                }
            }

        }

        /*while (x < x2) {
            while (y < y2) {
                while (z < z2) {
                    Location loc = new Location(world, x, y, z);
                    if (world.isChunkLoaded(loc.getBlockX(), loc.getBlockZ())){
                        Block block = world.getBlockAt(loc);
                        mats.add(block.getType());
                    }
                    z++;
                }
                y++;
            }
            x++;
        }*/

        return mats;
    }

    public boolean isVectorInside(final Point var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1.getX() >= minX && var1.getX() <= maxX
                    && var1.getZ() >= minZ && var1.getZ() <= maxZ
                    && var1.getX() >= minY && var1.getY() <= maxY;
        }
    }

    public double getXSize() {
        return this.maxX - this.minX;
    }

    public double getYSize() {
        return this.maxY - this.minY;
    }

    public double getZSize() {
        return this.maxZ - this.minZ;
    }


    public BoundingBox cloneBB() {
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ, timestamp);
    }


    /**
     * if instance and the argument bounding boxes overlap in the Y and Z dimensions, calculate the offset between them
     * in the X dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateXOffset(final BoundingBox other, double offsetX) {
        if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
            if (offsetX > 0.0D && other.maxX <= this.minX) {
                final double d1 = this.minX - other.maxX;

                if (d1 < offsetX) {
                    offsetX = d1;
                }
            } else if (offsetX < 0.0D && other.minX >= this.maxX) {
                final double d0 = this.maxX - other.minX;

                if (d0 > offsetX) {
                    offsetX = d0;
                }
            }

            return offsetX;
        } else {
            return offsetX;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the X and Z dimensions, calculate the offset between them
     * in the Y dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateYOffset(final BoundingBox other, double offsetY) {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ) {
            if (offsetY > 0.0D && other.maxY <= this.minY) {
                final double d1 = this.minY - other.maxY;

                if (d1 < offsetY) {
                    offsetY = d1;
                }
            } else if (offsetY < 0.0D && other.minY >= this.maxY) {
                final double d0 = this.maxY - other.minY;

                if (d0 > offsetY) {
                    offsetY = d0;
                }
            }

            return offsetY;
        } else {
            return offsetY;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and X dimensions, calculate the offset between them
     * in the Z dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateZOffset(final BoundingBox other, double offsetZ) {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY) {
            if (offsetZ > 0.0D && other.maxZ <= this.minZ) {
                final double d1 = this.minZ - other.maxZ;

                if (d1 < offsetZ) {
                    offsetZ = d1;
                }
            } else if (offsetZ < 0.0D && other.minZ >= this.maxZ) {
                final double d0 = this.maxZ - other.minZ;

                if (d0 > offsetZ) {
                    offsetZ = d0;
                }
            }

            return offsetZ;
        } else {
            return offsetZ;
        }
    }

    public BoundingBox addCoord(final double x, final double y, final double z) {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;

        if (x < 0.0D) {
            d0 += x;
        } else if (x > 0.0D) {
            d3 += x;
        }

        if (y < 0.0D) {
            d1 += y;
        } else if (y > 0.0D) {
            d4 += y;
        }

        if (z < 0.0D) {
            d2 += z;
        } else if (z > 0.0D) {
            d5 += z;
        }

        return new BoundingBox(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Checks if the specified vector is within the YZ dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInYZ(final Point vec) {
        return vec != null && (vec.getY() >= this.minY && vec.getY() <= this.maxY && vec.getZ() >= this.minZ && vec.getZ() <= this.maxZ);
    }

    /**
     * Checks if the specified vector is within the XZ dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInXZ(final Point vec) {
        return vec != null && (vec.getX() >= this.minX && vec.getX() <= this.maxX && vec.getZ() >= this.minZ && vec.getZ() <= this.maxZ);
    }

    /**
     * Checks if the specified vector is within the XY dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInXY(final Point vec) {
        return vec != null && (vec.getX() >= this.minX && vec.getX() <= this.maxX && vec.getY() >= this.minY && vec.getY() <= this.maxY);
    }

    public MovingPoint calculateIntercept(final Point vecA, final Point vecB) {
        Point vec3 = vecA.getIntermediateWithXValue(vecB, this.minX);
        Point vec31 = vecA.getIntermediateWithXValue(vecB, this.maxX);
        Point vec32 = vecA.getIntermediateWithYValue(vecB, this.minY);
        Point vec33 = vecA.getIntermediateWithYValue(vecB, this.maxY);
        Point vec34 = vecA.getIntermediateWithZValue(vecB, this.minZ);
        Point vec35 = vecA.getIntermediateWithZValue(vecB, this.maxZ);

        if (!this.isVecInYZ(vec3)) {
            vec3 = null;
        }

        if (!this.isVecInYZ(vec31))
        {
            vec31 = null;
        }

        if (!this.isVecInXZ(vec32))
        {
            vec32 = null;
        }

        if (!this.isVecInXZ(vec33))
        {
            vec33 = null;
        }

        if (!this.isVecInXY(vec34))
        {
            vec34 = null;
        }

        if (!this.isVecInXY(vec35))
        {
            vec35 = null;
        }

        Point vec36 = null;

        if (vec3 != null)
        {
            vec36 = vec3;
        }

        if (vec31 != null && (vec36 == null || vecA.squareDistanceTo(vec31) < vecA.squareDistanceTo(vec36)))
        {
            vec36 = vec31;
        }

        if (vec32 != null && (vec36 == null || vecA.squareDistanceTo(vec32) < vecA.squareDistanceTo(vec36)))
        {
            vec36 = vec32;
        }

        if (vec33 != null && (vec36 == null || vecA.squareDistanceTo(vec33) < vecA.squareDistanceTo(vec36)))
        {
            vec36 = vec33;
        }

        if (vec34 != null && (vec36 == null || vecA.squareDistanceTo(vec34) < vecA.squareDistanceTo(vec36)))
        {
            vec36 = vec34;
        }

        if (vec35 != null && (vec36 == null || vecA.squareDistanceTo(vec35) < vecA.squareDistanceTo(vec36)))
        {
            vec36 = vec35;
        }

        if (vec36 == null)
        {
            return null;
        }
        else
        {
            EnumFacing enumfacing = null;

            if (vec36 == vec3)
            {
                enumfacing = EnumFacing.WEST;
            }
            else if (vec36 == vec31)
            {
                enumfacing = EnumFacing.EAST;
            }
            else if (vec36 == vec32)
            {
                enumfacing = EnumFacing.DOWN;
            }
            else if (vec36 == vec33)
            {
                enumfacing = EnumFacing.UP;
            }
            else if (vec36 == vec34)
            {
                enumfacing = EnumFacing.NORTH;
            }
            else
            {
                enumfacing = EnumFacing.SOUTH;
            }

            return new MovingPoint(vec36, enumfacing);
        }
    }

    /**
     * Returns whether the given bounding box intersects with this one. Args: axisAlignedBB
     */
    public boolean intersectsWith(final BoundingBox other) {
        return other.maxX > this.minX && other.minX < this.maxX && (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ);
    }



    public Point toVector() {
        return new Point((maxX + minX) / 2.D, minY, (maxZ + minZ) / 2.D);
    }

}
