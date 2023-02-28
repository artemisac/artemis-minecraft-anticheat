package ac.artemis.packet.minecraft;

public abstract class AbstractWrapper<T> implements Wrapped {
    protected final T wrapper;

    public AbstractWrapper(T wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public T v() {
        return wrapper;
    }
}
