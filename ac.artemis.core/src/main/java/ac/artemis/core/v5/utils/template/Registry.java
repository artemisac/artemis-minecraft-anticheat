package ac.artemis.core.v5.utils.template;

import java.util.Arrays;
import java.util.List;

public abstract class Registry<T> {
    private final List<T> values;

    public Registry() {
        this.values = Arrays.asList(setValues());;
    }

    public abstract T[] setValues();

    public T get(final int index) {
        return values.get(index);
    }

    public void set(final int index, final T value) {
        values.set(index, value);
    }

    public int index(final T value) {return values.indexOf(value);}

    public int size() {
        return values.size();
    }
}
