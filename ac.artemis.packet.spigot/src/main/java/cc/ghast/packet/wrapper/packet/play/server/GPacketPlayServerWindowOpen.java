package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerWindowOpen;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;

import java.util.UUID;

@PacketLink(PacketPlayServerWindowOpen.class)
public class GPacketPlayServerWindowOpen extends GPacket implements PacketPlayServerWindowOpen, ReadableBuffer {
    public GPacketPlayServerWindowOpen(UUID player, ProtocolVersion version) {
        super("PacketPlayOutOpenWindow", player, version);
    }

    @Override
    public void read(ProtocolByteBuf byteBuf) {

    }
}
