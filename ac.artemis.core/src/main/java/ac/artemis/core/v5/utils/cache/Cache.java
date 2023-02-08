package ac.artemis.core.v5.utils.cache;

public interface Cache<T> {
    void set(final T t);

    boolean isReset();

    T get();
}
