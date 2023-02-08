package ac.artemis.core.v5.utils;

import ac.artemis.core.v4.utils.graphing.Pair;
import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class MathUtil {

    public final double EXPANDER = Math.pow(2, 24);

    public double hypot(final double x, final double z) {
        return Math.sqrt(x * x + z * z);
    }

    public double distanceBetweenAngles(final float alpha, final float beta) {
        final float alphax = alpha % 360;
        final float betax = beta % 360;
        final float delta = Math.abs(alphax - betax);
        return Math.abs(Math.min(360.0 - delta, delta));
    }

    public double clamp(final double num, final double min, final double max) {
        return num < min ? min : Math.min(num, max);
    }

    public double except(final double num, final double min, final double max) {
        return num > min ? min : Math.max(num, max);
    }

    public double getVariance(final Collection<? extends Number> data) {
        int count = 0;

        double sum = 0.0;
        double variance = 0.0;

        final double average;

        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        average = sum / count;

        for (final Number number : data) {
            variance += Math.pow(number.doubleValue() - average, 2.0);
        }

        return variance;
    }

    public int getPingTicks(final long ping, final int extraTicks) {
        return (int) Math.floor(ping / 50.0D) + extraTicks;
    }

    public double getStandardDeviation(final Collection<? extends Number> data) {
        final double variance = getVariance(data);

        return Math.sqrt(variance);
    }

    public double getSkewness(final Collection<? extends Number> data) {
        double sum = 0;
        int count = 0;

        final List<Double> numbers = Lists.newArrayList();

        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;

            numbers.add(number.doubleValue());
        }

        Collections.sort(numbers);

        final double mean = sum / count;
        final double median = (count % 2 != 0) ? numbers.get(count / 2) : (numbers.get((count - 1) / 2) + numbers.get(count / 2)) / 2;
        final double dev = getStandardDeviation(data);

        return (mean - median) / dev;
    }

    public double getAverage(final Collection<? extends Number> data) {
        return data.stream().mapToDouble(Number::doubleValue).average().orElse(0D);
    }

    public double getAverage2(final List<Integer> data) {
        double average = 0d;

        for (final int i : data) {
            average += i;
        }

        return average / data.size();
    }

    public double getKurtosis(final Collection<? extends Number> data) {
        double sum = 0.0;
        int count = 0;

        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        if (count < 3.0) {
            return 0.0;
        }

        final double efficiencyFirst = count * (count + 1.0) / ((count - 1.0) * (count - 2.0) * (count - 3.0));
        final double efficiencySecond = 3.0 * Math.pow(count - 1.0, 2.0) / ((count - 2.0) * (count - 3.0));
        final double average = sum / count;

        double variance = 0.0;
        double varianceSquared = 0.0;

        for (final Number number : data) {
            variance += Math.pow(average - number.doubleValue(), 2.0);
            varianceSquared += Math.pow(average - number.doubleValue(), 4.0);
        }

        return efficiencyFirst * (varianceSquared / Math.pow(variance / sum, 2.0)) - efficiencySecond;
    }

    public static int getMode(final Collection<? extends Number> array) {
        int mode = (int) array.toArray()[0];
        int maxCount = 0;
        for (final Number value : array) {
            int count = 1;
            for (final Number i : array) {
                if (i.equals(value))
                    count++;
                if (count > maxCount) {
                    mode = (int) value;
                    maxCount = count;
                }
            }
        }
        return mode;
    }

    private double getMedian(final List<Double> data) {
        if (data.isEmpty()) return 0.0D;

        if (data.size() % 2 == 0) {
            return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
        } else {
            return data.get(data.size() / 2);
        }
    }

    public boolean isExponentiallySmall(final Number number) {
        return number.doubleValue() < 1 && Double.toString(number.doubleValue()).contains("E");
    }

    public boolean isExponentiallyLarge(final Number number) {
        return number.doubleValue() > 10000 && Double.toString(number.doubleValue()).contains("E");
    }

    public long getGcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }

    public double gcd(final double a, final double b) {
        if (a < b)
            return gcd(b, a);
        else if (Math.abs(b) < 1e-4) // base case
            return a;
        else
            return gcd(b, a - Math.floor(a / b) * b);
    }


    public Pair<List<Double>, List<Double>> getOutliers(final Collection<? extends Number> collection) {
        final List<Double> values = new ArrayList<>();

        for (final Number number : collection) {
            values.add(number.doubleValue());
        }

        final double q1 = getMedian(values.subList(0, values.size() / 2));
        final double q3 = getMedian(values.subList(values.size() / 2, values.size()));

        final double iqr = Math.abs(q1 - q3);
        final double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        final Pair<List<Double>, List<Double>> tuple = new Pair<>(new ArrayList<>(), new ArrayList<>());

        for (final Double value : values) {
            if (value < lowThreshold) {
                tuple.getX().add(value);
            } else if (value > highThreshold) {
                tuple.getY().add(value);
            }
        }

        return tuple;
    }

    public int getOutliers2(final Collection<? extends Number> samples) {
        final double avg = getAverage(samples);

        int outliers = 0;

        for (final Number n : samples) {
            final double n2 = n.doubleValue();

            if (Math.abs(avg - n2) > 2) ++outliers;
        }

        return outliers;
    }

    public double getGcd(final double a, final double b) {
        if (a < b) {
            return getGcd(b, a);
        }

        if (Math.abs(b) < 0.001) {
            return a;
        } else {
            return getGcd(b, a - Math.floor(a / b) * b);
        }
    }

    public double getCps(final List<Integer> data) {
        return 20 / getAverage2(data);
    }

    public int getDuplicates(final Collection<? extends Number> data) {
        return (int) (data.size() - data.stream().distinct().count());
    }

    public int getDistinct(final Collection<? extends Number> data) {
        return (int) data.stream().distinct().count();
    }

    public List<Float> skipValues(final double count, final double min, final double max){
        final List<Float> floats = new ArrayList<>();
        for (float x = (float) min; x <= max; x += count){
            floats.add(x);
        }
        return floats;
    }

    public <T> Collection<T> fromArray(final T[] t) {
        return new ArrayList<>(Arrays.asList(t));
    }

    public double getFluctuation(final Double[] array) {
        return getFluctuation(fromArray(array));
    }

    public double getFluctuation(final Collection<Double> array) {
        double max = 0;
        double min = Double.MAX_VALUE;
        double sum = 0;

        for (final double i : array) {
            sum += i;
            if (i > max) max = i;
            if (i < min) min = i;
        }

        final double average = sum / array.size();
        // example: 75 - ((75 - 35) / 2) = 75 - (40 / 2) = 75 - 20 = 55
        final double median = max - ((max - min) / 2);
        return (average / 50) / (median / 50);
    }

    public double roundToPlace(final double value, final int places) {
        final double multiplier = Math.pow(10, places);
        return Math.round(value * multiplier) / multiplier;
    }
}
