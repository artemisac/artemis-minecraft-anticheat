package ac.artemis.core.v5.utils;

import lombok.experimental.UtilityClass;

import java.util.function.LongSupplier;

@UtilityClass
public class TimeUtil {
    private final LongSupplier nanoTimeSupplier = System::nanoTime;

    public double milliTimeWithAD() {
        return nanoTime() / 1000000D;
    }

    public long nanoTime() {
        return nanoTimeSupplier.getAsLong();
    }
}
