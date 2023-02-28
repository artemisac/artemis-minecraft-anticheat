package cc.ghast.packet.protocol;

import ac.artemis.packet.protocol.ProtocolDirection;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.wrappers.GPacket;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public interface EnumProtocol {
    GPacket getPacket(ProtocolDirection direction, int id, UUID playerId, ProtocolVersion version) throws IllegalAccessException, InvocationTargetException, InstantiationException;
    int getPacketId(ProtocolDirection direction, GPacket packet);
    Class<? extends GPacket> getPacketClass(ProtocolDirection direction, String name);
    int getOrdinal();
    EnumProtocol[] getValues();

    static EnumProtocol[] getProtocolByVersion(ProtocolVersion version) {
        return null;
    }
}
