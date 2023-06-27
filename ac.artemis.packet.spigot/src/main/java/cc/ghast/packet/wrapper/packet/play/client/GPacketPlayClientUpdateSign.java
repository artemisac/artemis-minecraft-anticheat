package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientUpdateSign;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientUpdateSign.class)
public class GPacketPlayClientUpdateSign extends GPacket implements PacketPlayClientUpdateSign, ReadableBuffer {
    public GPacketPlayClientUpdateSign(UUID player, ProtocolVersion version) {
        super("PacketPlayInUpdateSign", player, version);
    }

    private BlockPosition location;
    private String[] values = new String[4];

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        // 1.7.10
        if (version.isLegacy()) {
            final int x = byteBuf.readInt();
            final int y = byteBuf.readByte();
            final int z = byteBuf.readInt();
            this.location = new BlockPosition(x, y, z);
        }

        // 1.8+
        else {
            this.location = byteBuf.readBlockPositionFromLong();
        }

        // Values
        for(int i = 0; i < 4; ++i) {
            this.values[i] = byteBuf.readStringBuf(384);
        }

    }
}
