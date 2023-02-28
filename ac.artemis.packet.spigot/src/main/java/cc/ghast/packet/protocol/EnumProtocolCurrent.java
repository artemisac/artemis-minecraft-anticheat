package cc.ghast.packet.protocol;

import ac.artemis.packet.protocol.ProtocolDirection;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.utils.ServerUtil;
import cc.ghast.packet.reflections.ReflectUtil;
import cc.ghast.packet.utils.Pair;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.PacketInformation;
import cc.ghast.packet.wrapper.packet.handshake.GPacketHandshakeClientSetProtocol;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.UUID;

/**
 * @author Ghast
 * @since 18/08/2020
 * Artemis © 2020
 */

@Getter
public enum EnumProtocolCurrent implements EnumProtocol {
    HANDSHAKE(-1, new Pair[][]{
            // Client
            new Pair[]{
                new Pair<>(GPacketHandshakeClientSetProtocol.class, new PacketInformation("PacketHandshakingInSetProtocol"))
            },

            // Server
            new Pair[]{}

    }),
    PLAY(0, new Pair[][]{
            // Client

    }),
    STATUS(1, new Pair[][]{
            // Client

    }),
    LOGIN(2, new Pair[][]{

    });

    private final int id;

    // Direction = Ordinal so 0 = IN and 1 = OUT
    // Second bit is just the id lol
    private final Pair<Class<? extends GPacket>, PacketInformation>[][] packets;
    private final Map<ProtocolDirection, Map<Integer, Class<? extends GPacket>>> packetMap;

    EnumProtocolCurrent(int id, Pair<Class<? extends GPacket>, PacketInformation>[][] packets) {
        this.id = id;
        this.packets = packets;
        this.packetMap = ReflectUtil.getPacketMap(this);
    }

    @Override
    @SneakyThrows
    public GPacket getPacket(ProtocolDirection direction, int id, UUID playerId, ProtocolVersion version) {
        Class<? extends GPacket> clazz = packetMap.get(direction).get(id);
        if (clazz == null) {
            System.out.println("Packet of id " + id + " in direction does not exist! - " + packetMap.get(direction));
            return null;
        }
        return clazz.getConstructor(UUID.class, ProtocolVersion.class).newInstance(playerId, version);
    }

    @Override
    public int getPacketId(ProtocolDirection direction, GPacket packet) {
        Map.Entry<Integer, Class<? extends GPacket>> v = packetMap
                .get(direction)
                .entrySet()
                .parallelStream()
                .filter(e -> e != null && e.getValue() != null && e.getValue().equals(packet.getClass()))
                .findFirst()
                .orElse(null);
        if (v == null) return -1;

        return v.getKey();
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }

    @Override
    public EnumProtocol[] getValues() {
        return values();
    }


    @Override
    public Class<? extends GPacket> getPacketClass(ProtocolDirection direction, String name){
        for (Pair<Class<? extends GPacket>, PacketInformation> pair : packets[direction.ordinal()]) {
            if (pair.getV().getNmsName().equalsIgnoreCase(name) && pair.getV().isValid(ServerUtil.getGameVersion())) {
                return pair.getK();
            }
        }
        return null;
    }


    
}
