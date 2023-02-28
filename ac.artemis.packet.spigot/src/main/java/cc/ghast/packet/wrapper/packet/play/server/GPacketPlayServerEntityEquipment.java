package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerEntityEquipment;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;

import java.util.UUID;

@PacketLink(PacketPlayServerEntityEquipment.class)
public class GPacketPlayServerEntityEquipment extends GPacket implements PacketPlayServerEntityEquipment, ReadableBuffer {
    public GPacketPlayServerEntityEquipment(UUID player, ProtocolVersion version) {
        super("PacketPlayOutEntityEquipment", player, version);
    }

    @Override
    public void read(ProtocolByteBuf byteBuf) {

    }
}
