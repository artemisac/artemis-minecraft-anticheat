package ac.artemis.core.v4.utils.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The type Wrapper.
 *
 * @param <T> the type parameter
 * @author Ghast
 * @since 21/10/2020
 * Artemis © 2020
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Wrapper<T> {
    /**
     * The T.
     */
    T t;

    /**
     * Get t.
     *
     * @return the t
     */
    public T get() {
        return t;
    }

    /**
     * Set.
     *
     * @param t the t
     */
    public void set(T t) {
        this.t = t;
    }
}
