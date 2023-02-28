package ac.artemis.packet.callback;

public class PacketCallback {
    private final long time;
    private final Type type;

    public PacketCallback(long time, Type type) {
        this.time = time;
        this.type = type;
    }

    public enum Type {
        SUCCESS,
        FAILED;
    }

    public long getTime() {
        return time;
    }

    public Type getType() {
        return type;
    }
}
