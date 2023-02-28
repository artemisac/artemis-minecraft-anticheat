package cc.ghast.packet.protocol;

import ac.artemis.packet.protocol.ProtocolDirection;
import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.exceptions.AlreadyConsumedPacketIdException;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.handshake.GPacketHandshakeClientSetProtocol;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.SneakyThrows;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public enum EnumProtocolLegacy implements EnumProtocol {
    HANDSHAKE(-1) {;
        {
            this.addPacket(ProtocolDirection.IN, GPacketHandshakeClientSetProtocol.class);
        }
    },

    PLAY(0) {;
        {

        }

    },

    STATUS(1) {;
        {

        }
    },

    LOGIN(2) {;
        {

        }
    };

    // SERVERBOUND = IN


    private int i;

    private final Map<ProtocolDirection, BiMap<Integer, Class<? extends GPacket>>> packetMap;

    EnumProtocolLegacy(int i) {
        this.i = i;
        this.packetMap = new EnumMap<>(ProtocolDirection.class);
    }

    public EnumProtocolLegacy addPacket(ProtocolDirection enumProtocolDirection, Class<? extends GPacket> clazz) {
        BiMap<Integer, Class<? extends GPacket>> object = this.packetMap.get(enumProtocolDirection);

        if (object == null) {
            object = HashBiMap.create();
            this.packetMap.put(enumProtocolDirection, object);
        }

        if (object.containsValue(clazz)) {
            throw new AlreadyConsumedPacketIdException(enumProtocolDirection, clazz, object.inverse().get(clazz));
        } else {
            object.put(object.size(), clazz);
            return this;
        }
    }



    @SneakyThrows
    public GPacket getPacket(ProtocolDirection enumProtocolDirection, int i, UUID player, ProtocolVersion version) {
        Class<? extends GPacket> clazz = this.packetMap.get(enumProtocolDirection).get(i);
        return clazz == null ? null : clazz.getConstructor(UUID.class, ProtocolVersion.class).newInstance(player, version);
    }

    @Override
    public int getPacketId(ProtocolDirection direction, GPacket packet) {
        return this.packetMap.get(direction).inverse().get(packet.getClass());
    }

    @Override
    public Class<? extends GPacket> getPacketClass(ProtocolDirection direction, String name) {
        return this.packetMap.get(direction).get(i);
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }

    @Override
    public EnumProtocol[] getValues() {
        return values();
    }


}
