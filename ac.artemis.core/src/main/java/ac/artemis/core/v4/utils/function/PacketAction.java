package ac.artemis.core.v4.utils.function;

import java.lang.reflect.InvocationTargetException;

public interface PacketAction {
    default void pre() throws InvocationTargetException, InstantiationException, IllegalAccessException {};
    default void post() {};
}
