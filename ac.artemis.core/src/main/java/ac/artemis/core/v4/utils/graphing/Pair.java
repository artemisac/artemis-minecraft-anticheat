package ac.artemis.core.v4.utils.graphing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ghast
 * @since 24-Apr-20
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Deprecated
public class Pair<X, Y> {
    private X x;
    private Y y;

    public X getX() {
        return x;
    }

    public X getFirst() {
        return x;
    }

    public Y getY() {
        return y;
    }

    public Y getSecond() {
        return y;
    }

    public static <X, Y> Pair<X, Y> of(final X x, final Y y) {
        return new Pair<>(x, y);
    }
}
