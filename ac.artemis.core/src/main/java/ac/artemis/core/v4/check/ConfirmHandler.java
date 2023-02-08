package ac.artemis.core.v4.check;

public interface ConfirmHandler<T> {
    void confirm(T t);
}
