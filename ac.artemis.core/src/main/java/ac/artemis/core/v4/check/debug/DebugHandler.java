package ac.artemis.core.v4.check.debug;

public interface DebugHandler {
    StringBuilder builder = new StringBuilder();

    default void append(String s, Object... args) {
        builder.append(String.format(s, args));
    }

    default String getDebug() {
        String debug = builder.toString();
        this.clear();
        return debug;
    }

    default void clear() {
        builder.delete(0, builder.length());
    }
}
