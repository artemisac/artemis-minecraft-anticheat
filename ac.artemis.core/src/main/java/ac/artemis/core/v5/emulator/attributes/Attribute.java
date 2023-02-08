package ac.artemis.core.v5.emulator.attributes;

public interface Attribute<T> {
    T getBase();

    void set(final T t);
}
