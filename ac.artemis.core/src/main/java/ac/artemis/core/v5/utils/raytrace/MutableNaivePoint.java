package ac.artemis.core.v5.utils.raytrace;

/**
 * @author Ghast
 * @since 10/07/2020
 * Ghast Holdings LLC / Artemis © 2020
 */
public class MutableNaivePoint extends NaivePoint {

    public MutableNaivePoint(final int x, final int y, final int z) {
        super(x, y, z);
    }

    public MutableNaivePoint(final double x, final double y, final double z) {
        super(x, y, z);
    }

    public void override(final double x, final double y, final double z) {
        this.x = (int) Math.floor(x);
        this.y = (int) Math.floor(y);
        this.z = (int) Math.floor(z);
    }

    public void override(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
