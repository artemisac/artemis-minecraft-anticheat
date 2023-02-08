package ac.artemis.core.v5.utils.buffer;

public class StandardBuffer implements Buffer {
    private double max;
    private double min;
    private double flag;
    private double value;

    public StandardBuffer(final double flag) {
        this.value = 0.0D;
        this.max = Double.MAX_VALUE;
        this.min = 0.D;
        this.flag = flag;
    }

    @Override
    public void increaseBuffer(final double value) {
        this.value = Math.min(this.value + value, max);
    }

    @Override
    public void incrementBuffer() {
        this.value = Math.min(this.value + 1.0D, max);
    }

    @Override
    public void decreaseBuffer(final double value) {
        if (value < min)
            return;

        this.value = Math.max(this.value - value, min);
    }

    @Override
    public void decrementBuffer() {
        if (value < min)
            return;

        this.value = Math.max(this.value - 1, min);
    }

    @Override
    public void divideBuffer(final double value) {
        this.value /= value;
    }

    @Override
    public void resetBuffer() {
        this.value = 0.d;
    }

    @Override
    public Buffer setMin(final double value) {
        this.min = value;
        return this;
    }

    @Override
    public Buffer setMax(final double value) {
        this.max = value;
        return this;
    }

    @Override
    public Buffer setValue(final double value) {
        this.value = value;
        return this;
    }

    @Override
    public Buffer setFlag(final double value) {
        this.flag = value;
        return this;
    }

    @Override
    public boolean flag() {
        return value >= flag;
    }

    @Override
    public double get() {
        return value;
    }
}
