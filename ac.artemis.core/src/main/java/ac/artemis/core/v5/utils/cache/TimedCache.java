package ac.artemis.core.v5.utils.cache;

public class TimedCache<T> implements Cache<T> {
    private T value;
    private long lastSet;
    private final long limit;

    public TimedCache(long limit) {
        this.limit = limit;
    }

    @Override
    public void set(T t) {
        this.value = t;
        this.lastSet = System.currentTimeMillis();
    }

    @Override
    public boolean isReset() {
        return value == null || System.currentTimeMillis() - lastSet > limit;
    }

    @Override
    public T get() {
        return value;
    }
}
