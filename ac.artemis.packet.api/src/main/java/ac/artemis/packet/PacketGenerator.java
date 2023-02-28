package ac.artemis.packet;

import ac.artemis.packet.protocol.ProtocolDirection;
import ac.artemis.packet.protocol.ProtocolState;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.wrapper.Packet;

import java.util.UUID;

public interface PacketGenerator {
    ProtocolVersion getVersion();

    Packet getPacketFromId(final ProtocolDirection direction, final ProtocolState protocol, final int id, final UUID uuid, final ProtocolVersion version);

    Integer getPacketId(final Packet packet);
}
