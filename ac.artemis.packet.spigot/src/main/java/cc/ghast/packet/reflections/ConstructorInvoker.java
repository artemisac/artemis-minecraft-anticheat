package cc.ghast.packet.reflections;

public interface ConstructorInvoker {
    /**
     * Invoke getX constructor for getX specific class.
     *
     * @param arguments - the arguments to pass to the constructor.
     * @return The constructed object.
     */
    public Object invoke(Object... arguments);
}