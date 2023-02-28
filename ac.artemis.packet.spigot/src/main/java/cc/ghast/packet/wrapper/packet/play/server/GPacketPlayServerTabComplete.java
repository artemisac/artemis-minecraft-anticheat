package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerTabComplete;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;

import java.util.UUID;

@PacketLink(PacketPlayServerTabComplete.class)
public class GPacketPlayServerTabComplete extends GPacket implements PacketPlayServerTabComplete, ReadableBuffer {
    public GPacketPlayServerTabComplete(UUID player, ProtocolVersion version) {
        super("PacketPlayOutTabComplete", player, version);
    }

    @Override
    public void read(ProtocolByteBuf byteBuf) {

    }
}
