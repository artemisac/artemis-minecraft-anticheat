package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.PacketPlayClientConfirmTeleport;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientConfirmTeleport.class)
public class GPacketPlayClientConfirmTeleport extends GPacket implements PacketPlayClientConfirmTeleport, ReadableBuffer {
    public GPacketPlayClientConfirmTeleport(UUID player, ProtocolVersion version) {
        super("PacketPlayInTeleportAccept", player, version, e -> e.isOrAbove(ProtocolVersion.V1_9));
    }

    private int teleportId;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.teleportId = byteBuf.readVarInt();
    }
}
