package ac.artemis.core.v4.check;

import ac.artemis.packet.spigot.wrappers.GPacket;

public interface FastProcessHandler {
    void fastHandle(GPacket packet);
}
