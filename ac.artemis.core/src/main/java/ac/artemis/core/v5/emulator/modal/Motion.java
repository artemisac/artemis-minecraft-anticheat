package ac.artemis.core.v5.emulator.modal;

import ac.artemis.core.v5.utils.raytrace.Point;

/**
 * @author Ghast
 * @since 03/02/2021
 * Warden © 2021
 */
public class Motion {
    private double x;
    private double y;
    private double z;

    public Motion(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(final double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(final double z) {
        this.z = z;
    }

    public void addX(final double x) {
        this.x += x;
    }

    public void addY(final double y) {
        this.y += y;
    }

    public void addZ(final double z) {
        this.z += z;
    }

    public Motion copy() {
        return new Motion(x, y, z);
    }

    public Point toPoint() {
        return new Point(x, y, z);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Motion motion = (Motion) o;

        if (Double.compare(motion.x, x) != 0) return false;
        if (Double.compare(motion.y, y) != 0) return false;
        return Double.compare(motion.z, z) == 0;
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
        return result;
    }


}
