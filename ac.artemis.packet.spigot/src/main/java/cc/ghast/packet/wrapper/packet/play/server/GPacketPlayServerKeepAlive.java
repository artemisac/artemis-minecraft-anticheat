package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.spigot.utils.ServerUtil;
import ac.artemis.packet.wrapper.server.PacketPlayServerKeepAlive;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import cc.ghast.packet.wrapper.packet.WriteableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerKeepAlive.class)
public class GPacketPlayServerKeepAlive extends GPacket implements PacketPlayServerKeepAlive, ReadableBuffer, WriteableBuffer {
    public GPacketPlayServerKeepAlive(UUID player, ProtocolVersion version) {
        super("PacketPlayOutKeepAlive", player, version);
    }

    public GPacketPlayServerKeepAlive(long id) {
        super("PacketPlayOutKeepAlive");
        this.id = id;
    }

    private long id;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        if (version.isOrAbove(ProtocolVersion.V1_12_2)) {
            this.id = byteBuf.readLong();
        } else if (version.isLegacy()) {
            this.id = byteBuf.readInt();
        } else {
            this.id = byteBuf.readVarInt();
        }
    }

    @Override
    public void write(ProtocolByteBuf byteBuf) {
        if (version.isOrAbove(ProtocolVersion.V1_12_2)) {
            byteBuf.writeLong(id);
        } else if (version.isLegacy()) {
            byteBuf.writeInt((int) id);
        } else {
            byteBuf.writeVarInt((int) id);
        }

    }
}
