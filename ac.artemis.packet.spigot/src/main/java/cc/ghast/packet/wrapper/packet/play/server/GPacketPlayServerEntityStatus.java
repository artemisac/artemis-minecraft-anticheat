package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerEntityStatus;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerEntityStatus.class)
public class GPacketPlayServerEntityStatus extends GPacket implements PacketPlayServerEntityStatus, ReadableBuffer {
    public GPacketPlayServerEntityStatus(UUID player, ProtocolVersion version) {
        super("PacketPlayOutEntityStatus", player, version);
    }

    private int entityId;
    private byte logicOpcode;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.entityId = byteBuf.readInt();
        this.logicOpcode = byteBuf.readByte();
    }
}
