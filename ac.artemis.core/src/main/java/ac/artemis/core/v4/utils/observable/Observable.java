package ac.artemis.core.v4.utils.observable;

import java.util.HashSet;
import java.util.Set;

public final class Observable<T> {

    private final Set<ChangeObserver<T>> observers = new HashSet<>();
    private T value;

    public Observable(final T initialValue) {
        this.value = initialValue;
    }

    public T get() {
        return value;
    }

    public void set(final T value) {
        final T previousValue = this.value;

        this.value = value;

        observers.forEach((it) -> it.handle(previousValue, value));
    }


    public ChangeObserver<T> observe(final ChangeObserver<T> onChange) {
        observers.add(onChange);
        return onChange;
    }

    public void unobserve(final ChangeObserver<T> onChange) {
        observers.remove(onChange);
    }

    @FunctionalInterface
    interface ChangeObserver<T> {
        void handle(final T from, final T to);
    }
}
