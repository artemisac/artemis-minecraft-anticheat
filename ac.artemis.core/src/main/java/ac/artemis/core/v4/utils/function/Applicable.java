package ac.artemis.core.v4.utils.function;

public interface Applicable<T, O> {
    void apply(T t, O object);
}
