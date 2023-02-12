package ac.artemis.core.v4.utils.maths;


import ac.artemis.core.v5.utils.minecraft.MathHelper;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.http.HTTPRequest;
import ac.artemis.core.v4.utils.item.CheckRequest;
import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.net.URL;
import java.security.PublicKey;
import java.util.UUID;

/**
 * @author Ghast
 * @since 02-Apr-20
 */
public class OptifineUtil {

    public static final double MULTIPLIER = Math.pow(2, 24);

    public static long getMostCommonSensGCD(double x, double y) {
        long a = (long) (x * MULTIPLIER);
        long b = (long) (y * MULTIPLIER);


        double gcd = MathHelper.clamp_double(
                (getGcd(a, b) / MULTIPLIER), // get the divided GCD
                0.064, // Minimum value
                4.096 // Maximum value
        );

        return Math.round(((Math.cbrt(gcd / 8) - 0.2) / 0.6) * 100 * 2);
    }

    private static long gcd(long a, long b) {
        if (b <= (16384L)) {
            return a;
        }
        return gcd(b, a % b);
    }

    public static long getGcd(long current, long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }

    
    public static void checkOptifine(InitializeAction action, String license) {

    }
}
