package ac.artemis.packet.wrapper;

import java.util.HashMap;
import java.util.Map;

public class PacketCache extends HashMap<Integer, Class<? extends Packet>> {

    public void register(Class<? extends Packet> clazz) {
        put(size(), clazz);
    }

    public void register(int id, Class<? extends Packet> clazz) {
        put(id, clazz);
    }
}
