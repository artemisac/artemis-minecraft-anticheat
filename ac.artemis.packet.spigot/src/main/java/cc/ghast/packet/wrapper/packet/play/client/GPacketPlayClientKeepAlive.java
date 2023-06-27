package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientKeepAlive;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientKeepAlive.class)
public class GPacketPlayClientKeepAlive extends GPacket implements PacketPlayClientKeepAlive, ReadableBuffer {
    public GPacketPlayClientKeepAlive(UUID player, ProtocolVersion version) {
        super("PacketPlayInKeepAlive", player, version);
    }

    private long id;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        if (version.isBelow(ProtocolVersion.V1_12_2)) {
            this.id = byteBuf.readVarInt();
        } else {
            this.id = byteBuf.readLong();
        }
    }
}
