package ac.artemis.core.v4.utils.function;

public interface PacketAction {
    default void pre() {};
    default void post() {};
}
