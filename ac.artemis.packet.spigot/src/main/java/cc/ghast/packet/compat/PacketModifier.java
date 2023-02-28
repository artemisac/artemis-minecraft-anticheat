package cc.ghast.packet.compat;

import cc.ghast.packet.profile.ArtemisProfile;
import ac.artemis.packet.protocol.ProtocolDirection;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public interface PacketModifier {
    ProtocolByteBuf modify(ArtemisProfile profile, ProtocolDirection direction, ProtocolByteBuf byteBuf, int packetId);
}
