package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerBed;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerBed.class)
public class GPacketPlayServerBed extends GPacket implements PacketPlayServerBed, ReadableBuffer {
    public GPacketPlayServerBed(UUID player, ProtocolVersion version) {
        super("PacketPlayOutBed", player, version);
    }

    private int playerID;
    private BlockPosition bedPos;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.playerID = byteBuf.readVarInt();
        this.bedPos = byteBuf.readBlockPositionFromLong();
    }
}
