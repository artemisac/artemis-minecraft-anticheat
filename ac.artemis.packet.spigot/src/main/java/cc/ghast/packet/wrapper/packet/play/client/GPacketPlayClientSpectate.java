package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientSpectate;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientSpectate.class)
public class GPacketPlayClientSpectate extends GPacket implements PacketPlayClientSpectate, ReadableBuffer {
    public GPacketPlayClientSpectate(UUID player, ProtocolVersion version) {
        super("PacketPlayInSpectate", player, version);
    }

    private UUID entityId;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.entityId = byteBuf.readUUID();
    }
}
