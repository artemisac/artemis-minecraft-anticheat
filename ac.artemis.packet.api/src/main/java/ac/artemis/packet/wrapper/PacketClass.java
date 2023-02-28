package ac.artemis.packet.wrapper;

import ac.artemis.packet.wrapper.Packet;

public class PacketClass {
    private final Class<? extends Packet> clazz;
    private final int id;

    public PacketClass(Class<? extends Packet> clazz, int id) {
        this.clazz = clazz;
        this.id = id;
    }

    public Class<? extends Packet> getClazz() {
        return clazz;
    }

    public int getId() {
        return id;
    }
}
