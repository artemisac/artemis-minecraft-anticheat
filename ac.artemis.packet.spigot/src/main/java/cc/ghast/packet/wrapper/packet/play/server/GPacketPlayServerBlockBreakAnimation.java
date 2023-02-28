package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerBlockBreakAnimation;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;

import java.util.UUID;

@PacketLink(PacketPlayServerBlockBreakAnimation.class)
public class GPacketPlayServerBlockBreakAnimation extends GPacket implements PacketPlayServerBlockBreakAnimation, ReadableBuffer {
    public GPacketPlayServerBlockBreakAnimation(UUID player, ProtocolVersion version) {
        super("PacketPlayOutBlockBreakAnimation", player, version);
    }

    private int entityId;
    private BlockPosition position;
    private int destroyStage;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.entityId = byteBuf.readVarInt();

        if (version.isAbove(ProtocolVersion.V1_7_10)) {
            this.position = byteBuf.readBlockPositionFromLong();
        } else {
            int x = byteBuf.readInt();
            int y = byteBuf.readInt();
            int z = byteBuf.readInt();
            this.position = new BlockPosition(x, y, z);
        }
        
        this.destroyStage = byteBuf.readUnsignedByte();
    }
}
