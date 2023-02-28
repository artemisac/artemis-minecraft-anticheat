package cc.ghast.packet.wrapper.packet.status;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;

import java.util.UUID;

public class PacketStatusClientStart extends GPacket implements ReadableBuffer {
    public PacketStatusClientStart(UUID player, ProtocolVersion version) {
        super("PacketStatusInStart", player, version);
    }

    @Override
    public void read(ProtocolByteBuf byteBuf) {

    }
}
