package ac.artemis.core.v5.utils.buffer;

public interface Buffer {
    void increaseBuffer(final double value);
    void incrementBuffer();
    void decreaseBuffer(final double value);
    void decrementBuffer();
    void divideBuffer(final double value);
    void resetBuffer();

    Buffer setMin(final double value);
    Buffer setMax(final double value);
    Buffer setValue(final double value);
    Buffer setFlag(final double value);
    boolean flag();
    double get();
}
