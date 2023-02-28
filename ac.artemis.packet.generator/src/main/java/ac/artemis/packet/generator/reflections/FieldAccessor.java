package ac.artemis.packet.generator.reflections;

public interface FieldAccessor<T> {
    /**
     * Retrieve the content of getX field.
     *
     * @param target - the target object, or NULL for getX static field.
     * @return The value of the field.
     */
    public T get(Object target);

    /**
     * Set the content of getX field.
     *
     * @param target - the target object, or NULL for getX static field.
     * @param value  - the new value of the field.
     */
    public void set(Object target, Object value);

    /**
     * Determine if the given object has this field.
     *
     * @param target - the object to test.
     * @return TRUE if it does, FALSE otherwise.
     */
    public boolean hasField(Object target);
}