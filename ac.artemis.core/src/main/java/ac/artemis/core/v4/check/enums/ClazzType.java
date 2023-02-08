package ac.artemis.core.v4.check.enums;

import lombok.AllArgsConstructor;

/**
 * @author Ghast
 * @since 15-Mar-20
 */

@AllArgsConstructor
public enum ClazzType {
    DOUBLE(double.class),
    INTEGER(int.class),
    BOOLEAN(boolean.class),
    STRING(String.class),
    FLOAT(float.class),
    LONG(long.class),
    UNKNOWN(Object.class);

    private final Class<?> clazz;

    public static ClazzType getType(Object a) {
        for (ClazzType value : values()) {
            if (value.clazz.isInstance(a)) return value;
        }
        if (a instanceof String) return STRING;
        if (a instanceof Double) return DOUBLE;
        if (a instanceof Integer) return INTEGER;
        if (a instanceof Boolean) return BOOLEAN;
        if (a instanceof Float) return FLOAT;
        return UNKNOWN;
    }

    public boolean isNumeral() {
        return (Number.class.isAssignableFrom(clazz));
    }


    public boolean extendsClass(Class<?> claza) {
        return (claza.isAssignableFrom(clazz));
    }
}
