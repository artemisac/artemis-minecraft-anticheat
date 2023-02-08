package ac.artemis.core.v4.utils.bruteforce;

import java.util.function.Function;

/**
 * @author Ghast
 * @since 26/02/2021
 * Artemis © 2021
 */
public class ValueBruteforcer {
    private final Function<Float, Float> modifier;
    private float edgeStart;
    private float edgeEnd;

    public ValueBruteforcer(Function<Float, Float> modifier, float edgeStart, float edgeEnd) {
        this.modifier = modifier;
        this.edgeStart = edgeStart;
        this.edgeEnd = edgeEnd;
    }

    public float run(float maxOffset) {
        float offset = 1.F;
        while (offset > maxOffset) {
            final float offsetA = modifier.apply(edgeStart);
            final float offsetB = modifier.apply(edgeEnd);

            if (offsetA < offsetB) {
                edgeEnd -= (edgeEnd - edgeStart) / 2.F;
                offset = offsetA;
            } else {
                edgeStart += (edgeEnd - edgeStart) / 2.F;
                offset = offsetB;
            }
        }

        return (edgeEnd - edgeStart) / 2.F;
    }
}
