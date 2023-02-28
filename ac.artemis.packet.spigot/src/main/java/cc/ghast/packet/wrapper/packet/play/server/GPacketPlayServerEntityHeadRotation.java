package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerEntityHeadRotation;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;

import java.util.UUID;

@PacketLink(PacketPlayServerEntityHeadRotation.class)
public class GPacketPlayServerEntityHeadRotation extends GPacket implements PacketPlayServerEntityHeadRotation, ReadableBuffer {
    public GPacketPlayServerEntityHeadRotation(UUID player, ProtocolVersion version) {
        super("PacketPlayOutEntityHeadRotation", player, version);
    }

    @Override
    public void read(ProtocolByteBuf byteBuf) {

    }
}
