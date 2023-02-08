package ac.artemis.core.v4.emulator.move;

import ac.artemis.core.v5.utils.raytrace.Point;
import lombok.Data;
import lombok.ToString;

/**
 * @author Ghast
 * @since 03/02/2021
 * Artemis © 2021
 */

@Data
@ToString
public class Motion {
    private double x;
    private double y;
    private double z;

    private float forward;
    private float strafe;

    private boolean jumping;
    private boolean sprinting;

    private float friction;
    private float drag;
    private double gravity;
    private boolean liquid;

    public Motion(double x, double y, double z, float forward, float strafe, boolean jumping, boolean sprinting) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.forward = forward;
        this.strafe = strafe;
        this.jumping = jumping;
        this.sprinting = sprinting;
    }

    public void addX(double x) {
        this.x += x;
    }

    public void addY(double y) {
        this.y += y;
    }

    public void addZ(double z) {
        this.z += z;
    }

    public Motion copy() {
        return new Motion(x, y, z, forward, strafe, jumping, sprinting);
    }

    public Point toPoint() {
        return new Point(x, y, z);
    }

}
