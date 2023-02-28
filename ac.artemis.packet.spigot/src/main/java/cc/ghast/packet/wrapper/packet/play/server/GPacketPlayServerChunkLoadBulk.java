package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerChunkLoadBulk;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;

import java.util.UUID;

@PacketLink(PacketPlayServerChunkLoadBulk.class)
public class GPacketPlayServerChunkLoadBulk extends GPacket implements PacketPlayServerChunkLoadBulk, ReadableBuffer {
    public GPacketPlayServerChunkLoadBulk(UUID player, ProtocolVersion version) {
        super("PacketPlayOutMapChunkBulk", player, version);
    }

    @Override
    public void read(ProtocolByteBuf byteBuf) {

    }
}
