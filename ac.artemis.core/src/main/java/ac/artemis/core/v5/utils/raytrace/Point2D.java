package ac.artemis.core.v5.utils.raytrace;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Ghast
 * @since 17/07/2020
 * Ghast Holdings LLC / Artemis © 2020
 */

@Data
@RequiredArgsConstructor
public class Point2D {
    private final double x, y;


    /**
     * Inspired from Bukkit. This will just return the square of all the points factored
     * @param other Other Point2D
     * @return Dot of the angle
     */
    public double dot(final Point2D other) {
        return this.x * other.x + this.y * other.y;
    }

    /**
     * Basic vector maths at it's finest. Represents the formula sqrt(x^2 + y^2) = size of vector
     * @return Returns the size of the vector
     */
    public double length(){
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Inspired/Partly taken from Bukkit. This method will calculate the angle between 2 points based on the axis
     * @param other Other vector/point
     * @return Angle based on the axis
     */
    public float bukkitAngle(final Point2D other) {
        final double dot = this.dot(other) / (this.length() * other.length());
        return (float)Math.acos(dot);
    }

    public float axisAngle(final Point2D other) {
        final double x = this.x + other.x;
        final double y = this.y + other.y;

        return (float) Math.atan(Math.tan(x / y));
    }
}
