package ac.artemis.core.v5.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class RandomUtil {
    private final Random random = new Random();

    public <T> T getRandomFromArray(final T[] array) {
        final int rand = random.nextInt(array.length);

        return array[rand];
    }

    public int integer(final int max) {
        return random.nextInt(max);
    }
}
