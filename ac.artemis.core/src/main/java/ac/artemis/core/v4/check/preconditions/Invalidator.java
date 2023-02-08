package ac.artemis.core.v4.check.preconditions;

import java.util.Map;

public interface Invalidator<T extends Enum<T>> {
    Map<T, String> getInvalidations();

    default void appendInvalid(T t, String var, Object... args) {
        this.getInvalidations().putIfAbsent(t, String.format(var, args));
    }

    default void clearInvalid() {
        this.getInvalidations().clear();
    }
}
