package ac.artemis.core.v4.utils.position;

import ac.artemis.packet.wrapper.client.PacketPlayClientPositionLook;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerPosition;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ghast
 * @since 15/02/2021
 * Artemis © 2021
 */

@Data
@NoArgsConstructor
public class ModifiableFlyingLocation {
    private double x, y, z;
    private float yaw, pitch;

    public ModifiableFlyingLocation(PacketPlayClientPositionLook loc) {
        update(loc);
    }

    public ModifiableFlyingLocation(GPacketPlayServerPosition loc) {
        update(loc);
    }

    public ModifiableFlyingLocation(GPacketPlayServerPosition tp, PlayerMovement data) {
        update(tp, data);
    }

    public void update(PacketPlayClientPositionLook packet) {
        this.yaw = packet.getYaw();
        this.pitch = packet.getPitch();
        this.x = packet.getX();
        this.y = packet.getY();
        this.z = packet.getZ();
    }

    public void update(GPacketPlayServerPosition packet) {
        this.x = packet.getX();
        this.y = packet.getY();
        this.z = packet.getZ();
        this.yaw = packet.getYaw();
        this.pitch = packet.getPitch();
    }

    public void update(GPacketPlayServerPosition tp, PlayerMovement data) {
        double x = tp.getX();
        double y = tp.getY();
        double z = tp.getZ();
        float yaw = tp.getYaw();
        float pitch = tp.getPitch();

        if (tp.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.X)) {
            x += data.getX();
        }

        if (tp.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.Y)) {
            y += data.getY();
        }

        if (tp.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.Z)) {
            z += data.getZ();
        }

        if (tp.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.X_ROT)) {
            pitch += data.getPitch();
        }

        if (tp.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.Y_ROT)) {
            yaw += data.getYaw();
        }

        this.setX(x);
        this.setY(y);
        this.setZ(z);
        this.setYaw(yaw % 360.0F);
        this.setPitch(pitch % 360.0F);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModifiableFlyingLocation that = (ModifiableFlyingLocation) o;

        if (Math.abs(that.x - x) > 1E-4) return false;
        if (Math.abs(that.y - y) > 1E-4) return false;
        if (Math.abs(that.z - z) > 1E-4) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (yaw != +0.0f ? Float.floatToIntBits(yaw) : 0);
        result = 31 * result + (pitch != +0.0f ? Float.floatToIntBits(pitch) : 0);
        return result;
    }
}
