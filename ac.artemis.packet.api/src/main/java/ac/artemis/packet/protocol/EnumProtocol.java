package ac.artemis.packet.protocol;


import ac.artemis.packet.wrapper.Packet;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public interface EnumProtocol {
    Packet getPacket(ProtocolDirection direction, int id, UUID playerId, ProtocolVersion version) throws IllegalAccessException, InvocationTargetException, InstantiationException;
    int getPacketId(ProtocolDirection direction, Packet packet);
    Class<? extends Packet> getPacketClass(ProtocolDirection direction, String name);
    int getOrdinal();
    EnumProtocol[] getValues();
}
