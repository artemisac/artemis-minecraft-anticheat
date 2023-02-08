package ac.artemis.core.v4.utils.lists;

import java.lang.reflect.Array;

/**
 * @author Ghast
 * @since 09-May-20
 */
public class EvictingArray<T> {
    private final T[] array;

    public EvictingArray(Class<?> type, int size) {
        array = (T[]) Array.newInstance(type, size);
    }

    public void add(T t) {
        for (int i = 0; i < (array.length - 1); i++) {
            array[i] = array[i + 1];
        }
        array[array.length - 1] = t;
    }


}
