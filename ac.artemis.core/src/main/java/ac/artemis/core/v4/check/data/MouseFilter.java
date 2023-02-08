package ac.artemis.core.v4.check.data;

public class MouseFilter {
    public float x;
    public float y;
    public float z;

    /**
     * Smooths mouse input
     */
    public float smooth(float value, float sensitivity) {
        this.x += value;
        value = (this.x - this.y) * sensitivity;
        this.z += (value - this.z) * 0.5F;

        if (value > 0.0F && value > this.z || value < 0.0F && value < this.z) {
            value = this.z;
        }

        this.y += value;
        return value;
    }

    public void reset() {
        x = 0;
        y = 0;
        z = 0;
    }
}
