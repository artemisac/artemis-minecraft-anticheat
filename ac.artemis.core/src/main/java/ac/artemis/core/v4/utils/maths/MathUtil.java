package ac.artemis.core.v4.utils.maths;

import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.potion.Effect;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.core.v4.utils.lists.Tuple;
import ac.artemis.core.v5.utils.raytrace.Point;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Ghast
 * @since 17-Mar-20
 */
public class MathUtil {

    public static final double EXPANDER = Math.pow(2, 24);

    public static double delta(double a, double b) {
        return Math.abs(a - b);
    }

    public static <T extends Number> double getDuplicates(final Collection<T> entry) {
        final double distinct = entry.stream().distinct().count();

        return entry.size() - distinct;
    }

    public static Tuple<List<Double>, List<Double>> getOutliers(final Collection<? extends Number> collection) {
        final List<Double> values = new ArrayList<>();

        for (final Number number : collection) {
            values.add(number.doubleValue());
        }

        final double q1 = getMedian(values.subList(0, values.size() / 2));
        final double q3 = getMedian(values.subList(values.size() / 2, values.size()));

        final double iqr = Math.abs(q1 - q3);
        final double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        final Tuple<List<Double>, List<Double>> tuple = new Tuple<>(new ArrayList<>(), new ArrayList<>());

        for (final Double value : values) {
            if (value < lowThreshold) {
                tuple.a().add(value);
            } else if (value > highThreshold) {
                tuple.b().add(value);
            }
        }

        return tuple;
    }

    public static double magnitude(final double... points) {
        double sum = 0.0;

        for (final double point : points) {
            sum += point * point;
        }

        return sum;
    }

    public static <T> boolean containsArray(T[] variable, Collection<T> collection) {
        for (T t : variable) {
            if (collection.contains(t)) return true;
        }
        return false;
    }

    public static float normalizeYaw(float f){
        f %= 360.0F;
        if (f < -180.0D) {
            f += 360.0F;
        }

        if (f >= 180.0D) {
            f -= 360.0F;
        }
        return f;
    }

    public static float normalizePitch(float f){
        return Math.max(Math.min(f, 90.F), -90.F);
    }

    private static double getMedian(final List<Double> data) {
        if (data.size() % 2 == 0) {
            return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
        } else {
            return data.get(data.size() / 2);
        }
    }

    public static float wrapAngleTo180(float value) {
        value = value % 360.0F;

        if (value >= 180.0F) {
            value -= 360.0F;
        }

        if (value < -180.0F) {
            value += 360.0F;
        }

        return value;
    }

    public static int getPotionLevel(final Player player, final PotionEffectType effect) {
        int effectId = effect.getId();
        return player
                .getActivePotionEffects()
                .stream()
                .filter(potionEffect -> potionEffect.getType().getId() == effectId)
                .map(Effect::getAmplifier)
                .findAny()
                .orElse(0) + 1;
    }

    public static double vectorDistance(final PlayerPosition from, final PlayerPosition to) {
        final Point vectorFrom = from.toVector();
        final Point vectorTo = to.toVector();

        vectorFrom.setX(0.0);
        vectorTo.setY(0.0);

        return vectorFrom.subtract(vectorTo).length();
    }

    public static float distanceBetweenAngles(float alpha, float beta) {
        float alphax = alpha % 360, betax = beta % 360;
        float delta = Math.abs(alphax - betax);
        return (float) Math.abs(Math.min(360.0 - delta, delta));
    }

    public static double distanceBetweenAngles(double alpha, double beta) {
        double alphax = alpha % 360, betax = beta % 360;
        double delta = Math.abs(alphax - betax);
        return Math.abs(Math.min(360.0 - delta, delta));
    }

    public static double getDistanceBetweenAngles360(double alpha, double beta) {
        double abs = Math.abs(alpha % 360.0 - beta % 360.0);
        return Math.abs(Math.min(360.0 - abs, abs));
    }

    public static double getDistanceBetweenAngles360Raw(double alpha, double beta) {
        return Math.abs(alpha % 360 - beta % 360);
    }

    public static double roundToPlace(double value, int places) {
        double multiplier = Math.pow(10, places);
        return Math.round(value * multiplier) / multiplier;
    }

    public static long gcd(long x, long y) {
        long gcd = 1;

        for (int i = 1; i <= x && i <= y; ++i) {
            // Checks if i is factor of both integers
            if (x % i == 0 && y % i == 0) gcd = i;
        }
        return gcd;
    }

    public static double mostFrequent(double[] arr) {

        // Insert all elements in hash
        Map<Double, Integer> hp = new HashMap<>();

        for (double key : arr) {
            if (hp.containsKey(key)) {
                int freq = hp.get(key);
                freq++;
                hp.put(key, freq);
            } else {
                hp.put(key, 1);
            }
        }

        // find max frequency.
        double max_count = 0, res = -1;

        for (Map.Entry<Double, Integer> val : hp.entrySet()) {
            if (max_count < val.getValue()) {
                res = val.getKey();
                max_count = val.getValue();
            }
        }

        return res;
    }

    /**
     * @param collect - The collection to get the mode
     * @param <T> - The object returned
     * @return the mode of the collection
     */
    public static <T extends Number> T getMode(final Collection<T> collect) {
        final Map<T, Integer> repeated = new HashMap<>();

        //Sorting each value by how to repeat into a map.
        collect.forEach(val -> {
            final int number = repeated.getOrDefault(val, 0);

            repeated.put(val, number + 1);
        });

        //Calculating the largest value to the key, which would be the mode.
        return repeated.keySet().stream()
                .map(key -> new Tuple<>(key, repeated.get(key))) //We map it into a Tuple for easier sorting.
                .max(Comparator.comparing(e -> e.b(), Comparator.naturalOrder()))
                .orElseThrow(NullPointerException::new).a();
    }

    /**
     * @param collect - The collection to get the mode
     * @return the mode of the collection
     */
    public static double getMode(final double[] collect) {
        final List<Double> doubles = new ArrayList<>();

        for (double v : collect) {
            doubles.add(v);
        }

        return getMode(doubles);
    }
    /**
     * @param collect - The collection to get the mode
     * @return the mode of the collection
     */
    public static <T> double getMean(final Collection<T> collect) {
        //Calculating the largest value to the key, which would be the mode.
        return collect.stream()
                .mapToDouble(e -> ((Number) e).doubleValue())
                .average()
                .orElse(0.0D);
    }

    /**
     * @param collect - The collection to get the mode
     * @return the mode of the collection
     */
    public static <T> double getSum(final Collection<T> collect) {
        //Calculating the largest value to the key, which would be the mode.
        return collect.stream()
                .mapToDouble(e -> ((Number) e).doubleValue())
                .sum();
    }


    public static double mode(double[] a) {
        double maxValue = 0, maxCount = 0;
        int i, j;
        for (i = 0; i < a.length; ++i) {
            int count = 0;
            for (j = 0; j < a.length; ++j) {
                if (a[j] == a[i])
                    ++count;
            }

            if (count > maxCount) {
                maxCount = count;
                maxValue = a[i];
            }
        }
        return maxValue;
    }


    public static double doubleDecimal(double i, int decimal) {
        try {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(decimal);
            df.setMinimumFractionDigits(0);
            df.setDecimalSeparatorAlwaysShown(false);
            return Double.parseDouble(df.format(i).replace(",", "."));
        } catch (Exception e) {
            return i;
        }
    }

    public static float floatDecimal(double i, int decimal) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(decimal);
        df.setMinimumFractionDigits(0);
        df.setDecimalSeparatorAlwaysShown(false);
        return Float.parseFloat(df.format(i).replace(",", "."));
    }

    public static long getSumLong(List<Long> longs) {
        long sum = 0;
        for (long l : longs) {
            sum += l;
        }
        return sum;
    }

    public static double trim(int degree, double d) {
        String format = "#.#";

        for (int i = 1; i < degree; i++) {
            format = format + "#";
        }
        DecimalFormat twoDForm = new DecimalFormat(format);
        return Double.parseDouble(twoDForm.format(d).replace(",", "."));
    }

    public static int hightestPing(long ping, int keepAlivePing, double transactionPing) {
        int highestPing = 0;
        if (ping > highestPing) {
            highestPing = (int) ping;
        }
        if (keepAlivePing > highestPing) {
            highestPing = keepAlivePing;
        }
        if (transactionPing > highestPing) {
            highestPing = (int) transactionPing;
        }
        return highestPing;
    }

    public static double angleOf(double minX, double minZ, double maxX, double maxZ) {
        // NOTE: Remember that most math has the Y axis as positive above the X.
        // However, for screens we have Y as positive below. For this reason,
        // the Y values are inverted to get the expected results.
        final double deltaY = (minZ - maxZ);
        final double deltaX = (maxX - minX);
        final double result = Math.toDegrees(Math.atan2(deltaY, deltaX));
        return (result < 0) ? (360d + result) : result;
    }

    public static double doPythagoras(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    public static double round(double value, int places) {
        try {
            if (places < 0) throw new IllegalArgumentException();

            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } catch (NumberFormatException e) {
            return value;
        }
    }

    public static double getFluctuation(double[] array) {
        double max = 0;
        double min = Double.MAX_VALUE;
        double sum = 0;

        for (double i : array) {
            sum += i;
            if (i > max) max = i;
            if (i < min) min = i;
        }

        double average = sum / array.length;
        // example: 75 - ((75 - 35) / 2) = 75 - (40 / 2) = 75 - 20 = 55
        double median = max - ((max - min) / 2);
        double range = max - min;
        return (average / 50) / (median / 50);
    }

    public static double getStandardDeviation(final List<? extends Number> list) {
        final double average = list.stream().mapToDouble(Number::doubleValue).average().orElse(0.0);

        double stdDeviation = 0.0;

        for (final Number number : list) {
            stdDeviation += Math.pow(number.doubleValue() - average, 2);
        }

        return Math.sqrt(stdDeviation /= list.size());
    }

    public static double getStandardDeviation(final Deque<? extends Number> deque) {
        final double average = deque.stream().mapToDouble(Number::doubleValue).average().orElse(0.0);

        double stdDeviation = 0.0;

        for (final Number number : deque) {
            stdDeviation += Math.pow(number.doubleValue() - average, 2);
        }

        return Math.sqrt(stdDeviation /= deque.size());
    }

    public static double getStandardDeviation(final LinkedList<? extends Number> list) {
        final double average = list.stream().mapToDouble(Number::doubleValue).average().orElse(0.0);

        double stdDeviation = 0.0;

        for (final Number number : list) {
            stdDeviation += Math.pow(number.doubleValue() - average, 2);
        }

        return Math.sqrt(stdDeviation /= list.size());
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

    public static List<Float> skipValues(double count, double min, double max){
        List<Float> floats = new ArrayList<>();
        for (float x = (float) min; x <= max; x += count){
            floats.add(x);
        }
        return floats;
    }

    public static long getGcd(long current, long previous){
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }
}
