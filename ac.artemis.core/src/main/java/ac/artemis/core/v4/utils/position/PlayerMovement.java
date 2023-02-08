package ac.artemis.core.v4.utils.position;

import ac.artemis.packet.minecraft.Minecraft;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.core.v4.utils.maths.MathUtil;
import lombok.Getter;

/**
 * @author Ghast
 * @since 09-Apr-20
 */

@Getter
public class PlayerMovement extends PlayerPosition {

    public float yaw;
    public float pitch;

    public PlayerMovement(Player player, double x, double y, double z, float yaw, float pitch, long timestamp) {
        super(player, x, y, z, timestamp);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public PlayerMovement(World player, double x, double y, double z, float yaw, float pitch, long timestamp) {
        super(player, x, y, z, timestamp);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public PlayerMovement(PlayerPosition pos, PlayerRotation rot) {
        this(pos.getWorld(), pos.x, pos.y, pos.z, rot.yaw, rot.pitch, pos.getTimestamp());
    }

    @Override
    public PlayerMovement add(double x, double y, double z) {
        super.add(x, y, z);
        return this;
    }

    public float getYaw(){
        return yaw % 360;
    }

    public float getYawRaw(){
        return yaw;
    }

    @Override
    public Location toBukkitLocation() {
        return Minecraft.v().createLocation(getWorld(), x, y, z, yaw % 360, pitch);
    }

    public int getBlockX() {
        return (int) Math.round(x);
    }

    public int getBlockY() {
        return (int) Math.round(y);
    }

    public int getBlockZ() {
        return (int) Math.round(z);
    }

    public double getDeltaYaw(PlayerMovement two) {
        return MathUtil.distanceBetweenAngles(yaw, two.getYaw());
    }

    public double getDeltaPitch(PlayerMovement two) {
        return MathUtil.distanceBetweenAngles(pitch, two.getPitch());
    }

    public PlayerMovement setWorld(World world){
        this.world = world;
        return this;
    }

    public Point toPoint(){
        return new Point(x, y, z);
    }

    @Override
    public String toString() {
        return "PlayerMovement{" +
                "yaw=" + yaw +
                ", pitch=" + pitch +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
