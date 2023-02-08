package ac.artemis.core.v4.check.packet;

import ac.artemis.core.v4.utils.wrapper.Wrapper;
import ac.artemis.packet.spigot.wrappers.GPacket;

import java.util.HashMap;
import java.util.Map;

public interface PacketExcludable {

    /**
     * This static map computes the hashes at a speed of O(1). This is super cool :)
     * Also this gets the self class, which should normally invoke as the superclass.
     * Going to be testing to make sure
     */
    Map<Class<?>, Wrapper<Class<?>[]>> compatiblePackets = new HashMap<>();

    default boolean isCompatible(GPacket packet) {
        for (Class<?> packe : this.compatiblePackets.get(this.getClass()).getT()) {
            if (packe.isAssignableFrom(packet.getClass())) {
                return true;
            }
        }
        return false;
    }

    default void setCompatiblePackets(Class<?>... packets) {
        this.compatiblePackets.computeIfAbsent(this.getClass(), e -> new Wrapper<>()).setT(packets);
    }
}
