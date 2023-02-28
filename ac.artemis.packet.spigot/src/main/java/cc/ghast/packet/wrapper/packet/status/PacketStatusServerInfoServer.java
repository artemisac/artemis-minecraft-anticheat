package cc.ghast.packet.wrapper.packet.status;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;

import java.util.UUID;

public class PacketStatusServerInfoServer extends GPacket implements ReadableBuffer {

    public PacketStatusServerInfoServer(UUID player, ProtocolVersion version) {
        super("PacketStatusOutInfoServer", player, version);
    }


    @Override
    public void read(ProtocolByteBuf byteBuf) {

    }
}
