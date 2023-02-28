package ac.artemis.packet;

import ac.artemis.packet.profile.Profile;
import ac.artemis.packet.wrapper.Packet;

public interface PacketListener {
    void onPacket(Profile profile, Packet packet);

    default boolean isAsync() {
        return false;
    }
}
