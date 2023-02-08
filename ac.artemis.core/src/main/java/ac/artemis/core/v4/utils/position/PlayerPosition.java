package ac.artemis.core.v4.utils.position;

import ac.artemis.packet.minecraft.Minecraft;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.Vec3d;
import ac.artemis.core.v5.utils.raytrace.Point;
import lombok.Getter;

/**
 * @author Ghast
 * @since 15-Mar-20
 */

@Getter
public class PlayerPosition extends SimplePosition {

    private final long timestamp;
    private final Player player;
    public World world;
    private double minX, centerX, maxX, minZ, centerZ, maxZ, minY, centerY, maxY;
    private BoundingBox box;

    public PlayerPosition(Player player, double x, double y, double z, long timestamp) {
        super(x, y, z);
        this.timestamp = timestamp;
        this.player = player;
        this.world = player == null ? null : player.getWorld();
        recalc();
        //box = new BoundingBox(this, timestamp);
    }

    public PlayerPosition(World player, double x, double y, double z, long timestamp) {
        super(x, y, z);
        this.timestamp = timestamp;
        this.player = null;
        this.world = player;
        recalc();
        //box = new BoundingBox(this, timestamp);
    }

    private void recalc() {
        // X value
        this.minX = x - 0.3;
        this.centerX = x;
        this.maxX = x + 0.3;

        // Y value
        this.minY = y;
        this.centerY = y + 0.925;
        this.maxY = y + 1.85;

        // Z value
        this.minZ = z - 0.3;
        this.centerZ = z;
        this.maxZ = z + 0.3;

        // Bounding box
        box = new BoundingBox(toBounding(), timestamp);
    }

    public PlayerPosition add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        recalc();
        return this;
    }

    public PlayerPosition addCoord(double x, double y, double z) {
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

        this.minX = d0;
        this.minY = d1;
        this.minZ = d2;
        this.maxX = d3;
        this.maxY = d4;
        this.maxZ = d5;

        return this;
    }


    public PlayerPosition add(SimplePosition p) {
        this.x += p.x;
        this.y += p.y;
        this.z += p.z;
        recalc();
        return this;
    }

    public Location toBukkitLocation() {
        return Minecraft.v().createLocation(world, x, y, z);
    }

    public Point toVector() {
        return new Point(x, y, z);
    }

    public double distanceXZ(PlayerPosition sec) {
        return Math.sqrt(Math.pow(sec.getX() - getX(), 2) + Math.pow(sec.getZ() - getZ(), 2));
    }

    public double distanceY(PlayerPosition sec) {
        return Math.abs(sec.getY() - y);
    }

    public double distance(PlayerPosition sec) {
        return Math.sqrt(distanceSquare(sec));
    }

    public double distanceSquare(PlayerPosition sec) {
        return Math.pow(sec.getX() - getX(), 2) + Math.pow(sec.getY() - getY(), 2) + Math.pow(sec.getZ() - getZ(), 2);
    }

    public double distanceSquareXZ(PlayerPosition sec) {
        return Math.pow(sec.getX() - getX(), 2) + Math.pow(sec.getZ() - getZ(), 2);
    }

    public BoundingBox toBounding() {
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public String toString() {
        return "PlayerPosition{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public Point toPoint() {
        return new Point(x, y, z);
    }

    public PlayerPosition clone() {
        return new PlayerPosition(world, x, y, z, timestamp);
    }
}
