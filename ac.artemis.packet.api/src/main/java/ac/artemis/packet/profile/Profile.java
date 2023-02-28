package ac.artemis.packet.profile;

import ac.artemis.packet.protocol.ProtocolVersion;

import java.util.UUID;

public interface Profile {
    UUID getUuid();

    ProtocolVersion getVersion();
}
