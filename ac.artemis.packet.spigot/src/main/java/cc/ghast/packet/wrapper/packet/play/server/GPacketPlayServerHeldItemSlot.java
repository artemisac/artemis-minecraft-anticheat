package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerItemHeldSlot;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;

import java.util.UUID;

@PacketLink(PacketPlayServerItemHeldSlot.class)
public class GPacketPlayServerHeldItemSlot extends GPacket implements PacketPlayServerItemHeldSlot, ReadableBuffer {
    public GPacketPlayServerHeldItemSlot(UUID player, ProtocolVersion version) {
        super("PacketPlayOutHeldItemSlot", player, version);
    }

    @Override
    public void read(ProtocolByteBuf byteBuf) {

    }
}
