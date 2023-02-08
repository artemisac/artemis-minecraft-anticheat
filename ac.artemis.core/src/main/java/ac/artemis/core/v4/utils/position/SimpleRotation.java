package ac.artemis.core.v4.utils.position;

/**
 * @author Ghast
 * @since 15-Mar-20
 */
public class SimpleRotation {
    public float yaw, pitch;

    public SimpleRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw % 360;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYawRaw() {
        return yaw;
    }
}
