package ac.artemis.core.v5.features.safety;

import ac.artemis.packet.spigot.wrappers.GPacket;

public interface SafetyFeature {
    boolean check(GPacket packet);
}
