package ac.artemis.core.v5.utils.pledge;

import ac.artemis.core.Artemis;
import lombok.experimental.UtilityClass;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;

@SuppressWarnings({"ConstantConditions", "unchecked"})
@UtilityClass
public final class ReflectionUtil {
    private final Unsafe UNSAFE = ReflectionUtil.getUnsafeInstance();

    private Unsafe getUnsafeInstance() {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            return (Unsafe) unsafeField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Could not locate Unsafe object!");
            e.printStackTrace();
            return null;
        }
    }

    public Field get(Class<?> oClass, Class<?> type, int index) throws NoSuchFieldException {
        int i = 0;
        for (Field field : oClass.getDeclaredFields()) {
            if (field.getType() == type) {
                if (i == index) {
                    field.setAccessible(true);
                    return field;
                }
                i++;
            }
        }

        throw new NoSuchFieldException("Could not find field of class " + type.getName() + " with index " + index);
    }

    public Field getFieldByClassNames(Class<?> clazz, String... simpleNames)  throws NoSuchFieldException {
        for (Field field : clazz.getDeclaredFields()) {
            String typeSimpleName = field.getType().getSimpleName();
            for (String name : simpleNames) {
                if (name.equals(typeSimpleName)) {
                    field.setAccessible(true);
                    return field;
                }
            }
        }

        throw new NoSuchFieldException("Could not find field in class " + clazz.getName() + " with names " + Arrays.toString(simpleNames));
    }

    public Field getFieldByType(Class<?> clazz, Class<?> type) throws NoSuchFieldException {
        for (Field field : clazz.getDeclaredFields()) {
            Class<?> foundType = field.getType();
            if (type.isAssignableFrom(foundType)) {
                field.setAccessible(true);
                return field;
            }
        }

        throw new NoSuchFieldException("Could not find field in class " + clazz.getName() + " with type " + type.getName());
    }

    public Class<?> getSuperClassByName(Class<?> clazz, String simpleName) {
        if (!clazz.getSimpleName().equals(simpleName)) {
            Class<?> superClazz;
            while((superClazz = clazz.getSuperclass()) != null) {
                if (superClazz.getSimpleName().equals(simpleName)) {
                    break;
                }
            }

            return superClazz;
        } else {
            return clazz;
        }
    }

    public <T> void setUnsafe(Object object, Field field, T value) {
        ReflectionUtil.UNSAFE.putObject(object, ReflectionUtil.UNSAFE.objectFieldOffset(field), value);
    }

    public <T> T instantiateUnsafe(Class<T> clazz) throws Exception{
        return (T) ReflectionUtil.UNSAFE.allocateInstance(clazz);
    }
}