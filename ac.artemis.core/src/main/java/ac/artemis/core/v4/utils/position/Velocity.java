package ac.artemis.core.v4.utils.position;

import ac.artemis.core.v5.utils.bounding.Vec3d;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ghast
 * @since 24-Apr-20
 */
@Getter
@Setter
@EqualsAndHashCode
public class Velocity {
    private double x;
    private double y;
    private double z;
    private double horizontal, vertical;

    public Velocity(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.horizontal = Math.sqrt(x * x + z * z);
        this.vertical = Math.abs(y);
    }

    public Velocity(Vec3d v) {
        this(v.getX(), v.getY(), v.getZ());
    }

    public Velocity add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public double getDistance() {
        return Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public double getSquaredHorizontal() {
        return (x * x + z * z);
    }

    public double getSqr() {
        return x + y + z;
    }

    @Override
    public String toString() {
        return "Velocity{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
