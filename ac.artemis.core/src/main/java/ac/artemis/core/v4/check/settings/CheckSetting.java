package ac.artemis.core.v4.check.settings;

import ac.artemis.core.v4.check.exceptions.InvalidSettingValueCast;
import ac.artemis.core.v4.check.enums.ClazzType;
import lombok.Getter;

/**
 * @author Ghast
 * @since 15-Mar-20
 */

@Getter
public class CheckSetting {
    private final Object value;
    private final ClazzType type;

    public CheckSetting(Object value) {
        this.value = value;
        this.type = ClazzType.getType(value);
    }

    public double getAsDouble() {
        if (type.equals(ClazzType.BOOLEAN)) throw new InvalidSettingValueCast(value.toString(), "double");
        return Double.parseDouble(value.toString());
    }

    public int getAsInt() {
        double var = getAsDouble();
        if (var % 1 == 0) return (int) var;
        return (int) Double.doubleToLongBits(var);
    }

    public boolean getAsBoolean() {
        return Boolean.parseBoolean(value.toString());
    }


}
