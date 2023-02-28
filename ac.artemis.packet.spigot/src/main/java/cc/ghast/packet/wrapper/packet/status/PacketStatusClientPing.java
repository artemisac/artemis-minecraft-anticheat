package cc.ghast.packet.wrapper.packet.status;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;

import java.util.UUID;

public class PacketStatusClientPing extends GPacket implements ReadableBuffer {
    public PacketStatusClientPing(UUID player, ProtocolVersion version) {
        super("PacketStatusInPing", player, version);
    }

    @Override
    public void read(ProtocolByteBuf byteBuf) {

    }
}
