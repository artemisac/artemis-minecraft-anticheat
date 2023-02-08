package ac.artemis.core.v5.utils;

import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.potion.Effect;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Ghast
 * @since 17-Mar-20
 */

@UtilityClass
public class OldMathUtil {
    public final double EXPANDER = Math.pow(2, 24);

    public double delta(final double a, final double b) {
        return Math.abs(a - b);
    }

    public <T extends Number> double getDuplicates(final Collection<T> entry) {
        final double distinct = entry.stream().distinct().count();

        return entry.size() - distinct;
    }

    public double magnitude(final double... points) {
        double sum = 0.0;

        for (final double point : points) {
            sum += point * point;
        }

        return sum;
    }

    public <T> boolean containsArray(final T[] variable, final Collection<T> collection) {
        for (final T t : variable) {
            if (collection.contains(t)) return true;
        }
        return false;
    }

    public float normalizeYaw(float f){
        f %= 360.0F;
        if (f < -180.0D) {
            f += 360.0F;
        }

        if (f >= 180.0D) {
            f -= 360.0F;
        }
        return f;
    }

    public float normalizePitch(final float f){
        return Math.max(Math.min(f, 90.F), -90.F);
    }

    private double getMedian(final List<Double> data) {
        if (data.size() % 2 == 0) {
            return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
        } else {
            return data.get(data.size() / 2);
        }
    }

    public float wrapAngleTo180(float value) {
        value = value % 360.0F;

        if (value >= 180.0F) {
            value -= 360.0F;
        }

        if (value < -180.0F) {
            value += 360.0F;
        }

        return value;
    }

    public int getPotionLevel(final Player player, final PotionEffectType effect) {
        final int effectId = effect.getId();

        return player.getActivePotionEffects()
                .stream()
                .filter(potionEffect -> potionEffect.getType().getId() == effectId)
                .map(Effect::getAmplifier)
                .findAny()
                .orElse(0) + 1;
    }

    public double distanceBetweenAngles(final float alpha, final float beta) {
        final float alphax = alpha % 360;
        final float betax = beta % 360;
        final float delta = Math.abs(alphax - betax);
        return Math.abs(Math.min(360.0 - delta, delta));
    }

    public double distanceBetweenAngles(final double alpha, final double beta) {
        final double alphax = alpha % 360;
        final double betax = beta % 360;
        final double delta = Math.abs(alphax - betax);
        return Math.abs(Math.min(360.0 - delta, delta));
    }

    public double getDistanceBetweenAngles360(final double alpha, final double beta) {
        final double abs = Math.abs(alpha % 360.0 - beta % 360.0);
        return Math.abs(Math.min(360.0 - abs, abs));
    }

    public double getDistanceBetweenAngles360Raw(final double alpha, final double beta) {
        return Math.abs(alpha % 360 - beta % 360);
    }

    public double roundToPlace(final double value, final int places) {
        final double multiplier = Math.pow(10, places);
        return Math.round(value * multiplier) / multiplier;
    }

    public long gcd(final long x, final long y) {
        long gcd = 1;

        for (int i = 1; i <= x && i <= y; ++i) {
            // Checks if i is factor of both integers
            if (x % i == 0 && y % i == 0) gcd = i;
        }
        return gcd;
    }

    public double mostFrequent(final double[] arr) {

        // Insert all elements in hash
        final Map<Double, Integer> hp = new HashMap<>();

        for (final double key : arr) {
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

        for (final Map.Entry<Double, Integer> val : hp.entrySet()) {
            if (max_count < val.getValue()) {
                res = val.getKey();
                max_count = val.getValue();
            }
        }

        return res;
    }

    /**
     * @param collect - The collection to get the mode
     * @return the mode of the collection
     */
    public <T> double getMean(final Collection<T> collect) {
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
    public <T> double getSum(final Collection<T> collect) {
        //Calculating the largest value to the key, which would be the mode.
        return collect.stream()
                .mapToDouble(e -> ((Number) e).doubleValue())
                .sum();
    }


    public double mode(final double[] a) {
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


    public double doubleDecimal(final double i, final int decimal) {
        try {
            final DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(decimal);
            df.setMinimumFractionDigits(0);
            df.setDecimalSeparatorAlwaysShown(false);
            return Double.parseDouble(df.format(i).replace(",", "."));
        } catch (final Exception e) {
            return i;
        }
    }

    public float floatDecimal(final double i, final int decimal) {
        final DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(decimal);
        df.setMinimumFractionDigits(0);
        df.setDecimalSeparatorAlwaysShown(false);
        return Float.parseFloat(df.format(i).replace(",", "."));
    }

    public long getSumLong(final List<Long> longs) {
        long sum = 0;

        for (final long l : longs) {
            sum += l;
        }

        return sum;
    }

    public double trim(final int degree, final double d) {
        final StringBuilder format = new StringBuilder("#.#");

        for (int i = 1; i < degree; i++) {
            format.append("#");
        }

        final DecimalFormat twoDForm = new DecimalFormat(format.toString());

        return Double.parseDouble(twoDForm.format(d).replace(",", "."));
    }

    public int getHighestPing(final long ping, final int keepAlivePing, final double transactionPing) {
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

    public double angleOf(final double minX, final double minZ, final double maxX, final double maxZ) {
        // NOTE: Remember that most math has the Y axis as positive above the X.
        // However, for screens we have Y as positive below. For this reason,
        // the Y values are inverted to get the expected results.
        final double deltaY = (minZ - maxZ);
        final double deltaX = (maxX - minX);
        final double result = Math.toDegrees(Math.atan2(deltaY, deltaX));
        return (result < 0) ? (360d + result) : result;
    }

    public double doPythagoras(final double x, final double y) {
        return Math.sqrt(x * x + y * y);
    }

    public double round(final double value, final int places) {
        try {
            if (places < 0) throw new IllegalArgumentException();

            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } catch (final NumberFormatException e) {
            return value;
        }
    }

    public double getFluctuation(final double[] array) {
        double max = 0;
        double min = Double.MAX_VALUE;
        double sum = 0;

        for (final double i : array) {
            sum += i;
            if (i > max) max = i;
            if (i < min) min = i;
        }

        final double average = sum / array.length;
        // example: 75 - ((75 - 35) / 2) = 75 - (40 / 2) = 75 - 20 = 55
        final double median = max - ((max - min) / 2);
        final double range = max - min;
        return (average / 50) / (median / 50);
    }

    public double getStandardDeviation(final List<? extends Number> list) {
        final double average = list.stream().mapToDouble(Number::doubleValue).average().orElse(0.0);

        double stdDeviation = 0.0;

        for (final Number number : list) {
            stdDeviation += Math.pow(number.doubleValue() - average, 2);
        }

        return Math.sqrt(stdDeviation /= list.size());
    }

    public double getStandardDeviation(final Deque<? extends Number> deque) {
        final double average = deque.stream().mapToDouble(Number::doubleValue).average().orElse(0.0);

        double stdDeviation = 0.0;

        for (final Number number : deque) {
            stdDeviation += Math.pow(number.doubleValue() - average, 2);
        }

        return Math.sqrt(stdDeviation /= deque.size());
    }

    public double getStandardDeviation(final LinkedList<? extends Number> list) {
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

    public List<Float> skipValues(final double count, final double min, final double max){
        final List<Float> floats = new ArrayList<>();

        for (float x = (float) min; x <= max; x += count){
            floats.add(x);
        }

        return floats;
    }

    public long getGcd(final long current, final long previous){
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }
}
