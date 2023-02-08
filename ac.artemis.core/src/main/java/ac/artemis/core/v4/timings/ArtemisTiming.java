package ac.artemis.core.v4.timings;

import lombok.Getter;

/**
 * @author Ghast
 * @since 24-Apr-20
 */

@Getter
public class ArtemisTiming {
    private long max, min, sum, quant;

    public void addTime(final long before, final long now) {
        final long delta = Math.abs(now - before);

        if (quant % 60 == 0) reset();

        if (delta > max) max = delta;
        if (delta < min) min = delta;
        sum += delta;
        quant++;
    }

    public double getAverage() {
        return (float) sum / (float) quant;
    }

    public void reset() {
        this.max = this.sum = this.quant = 0;
        this.min = 999999;
    }
}
