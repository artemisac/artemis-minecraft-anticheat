package cc.ghast.packet.utils;

import lombok.Data;

@Data
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
}
