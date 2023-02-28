package ac.artemis.packet.wrapper;

import ac.artemis.packet.protocol.ProtocolVersion;

import java.lang.reflect.Constructor;
import java.util.UUID;

public class PacketInfo {
    private final int id;
    private final PacketClass clazz;
    private final String nmsName;

    public PacketInfo(int id, Class<? extends Packet> clazz, String nmsName) {
        this.id = id;
        this.clazz = new PacketClass(clazz, PacketRepository.getPacketId(clazz));
        this.nmsName = nmsName;
    }

    public int getId() {
        return id;
    }

    public int getClazzId() {
        return clazz.getId();
    }

    public Class<? extends Packet> getClazz() {
        return clazz.getClazz();
    }

    public String getNmsName() {
        return nmsName;
    }

}
