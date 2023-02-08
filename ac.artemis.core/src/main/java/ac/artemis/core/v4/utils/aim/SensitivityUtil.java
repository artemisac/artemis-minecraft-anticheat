package ac.artemis.core.v4.utils.aim;

import ac.artemis.core.v5.utils.minecraft.MathHelper;

/**
 * @author Ghast
 * @since 16/07/2020
 * Ghast Holdings LLC / Artemis © 2020
 */
public class SensitivityUtil {
    public static final float SENSITIVITY_MIN = 0.0F;
    public static final float SENSITIVITY_MAX = 1.0F;

    public static float normalizeValue(float p_148266_1_) {
        return MathHelper.clamp_float((snapToStepClamp(p_148266_1_) - SENSITIVITY_MIN) / (SENSITIVITY_MAX - SENSITIVITY_MIN), SENSITIVITY_MIN, SENSITIVITY_MAX);
    }

    public static float denormalizeValue(float p_148262_1_) {
        return snapToStepClamp(SENSITIVITY_MAX * MathHelper.clamp_float(p_148262_1_, SENSITIVITY_MIN, SENSITIVITY_MAX));
    }

    public static float snapToStepClamp(float p_148268_1_) {
        return MathHelper.clamp_float(p_148268_1_, SENSITIVITY_MIN, SENSITIVITY_MAX);
    }

    public static double getSensitivityDelta(double delta, double sensitivity) {
        return (delta / 0.15) / sensitivity;
    }

    public static double getFormula(double sensitivity) {
        double f = sensitivity * 0.6F + 0.2F;
        double f1 = f * f * f * 8.0F;
        return f1;
    }
}
