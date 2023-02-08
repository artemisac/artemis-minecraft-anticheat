package ac.artemis.core.v4.check.data;

import ac.artemis.core.v4.utils.lists.EvictingLinkedList;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * @author Ghast
 * @since 16-Feb-20
 */
public class ClickSample extends EvictingLinkedList<Long> {
    public double[] values;
    private DescriptiveStatistics stats;

    public ClickSample(int maxSize) {
        super(maxSize);
    }

    @Override
    public void addLast(Long along) {
        super.addLast(along);
        recalc();
    }

    @Override
    public void addFirst(Long along) {
        super.addFirst(along);
        recalc();
    }

    @Override
    public boolean add(Long along) {
        boolean x = super.add(along);
        recalc();
        return x;
    }

    private void recalc() {
        values = new double[this.size()];
        for (int i = 0; i < this.size(); i++) {
            values[i] = (double) get(i);
        }
        stats = new DescriptiveStatistics(stats);
    }

    public double getKurtosis() {
        return stats.getKurtosis();
    }

    public double getSkewness() {
        return stats.getSkewness();
    }

    public double getDeviation() {
        return stats.getStandardDeviation();
    }

    public double getMean() {
        return stats.getMean();
    }

    public double getQuadraticMean() {
        return stats.getQuadraticMean();
    }

    public double getGeometricMean() {
        return stats.getGeometricMean();
    }

    public double getVariance() {
        return stats.getVariance();
    }

    public double getMax() {
        return stats.getMax();
    }

    public double getMin() {
        return stats.getMin();
    }

    public double getOscillation() {
        return getMax() - getMin();
    }


}
