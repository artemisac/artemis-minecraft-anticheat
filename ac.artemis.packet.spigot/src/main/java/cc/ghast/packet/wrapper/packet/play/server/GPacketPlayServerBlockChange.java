package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerBlockChange;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerBlockChange.class)
public class GPacketPlayServerBlockChange extends GPacket implements PacketPlayServerBlockChange, ReadableBuffer {
    public GPacketPlayServerBlockChange(UUID player, ProtocolVersion version) {
        super("PacketPlayOutBlockChange", player, version);
    }

    private BlockPosition position;
    private int blockId;
    @Override
    public void read(ProtocolByteBuf byteBuf) {
        if (version.isLegacy()) {
            int x = byteBuf.readInt();
            int y = byteBuf.readUnsignedByte();
            int z = byteBuf.readInt();
            this.position = new BlockPosition(x, y, z);
        }
        else {
            this.position = byteBuf.readBlockPositionFromLong();
        }

        this.blockId = byteBuf.readVarInt();
    }
}
