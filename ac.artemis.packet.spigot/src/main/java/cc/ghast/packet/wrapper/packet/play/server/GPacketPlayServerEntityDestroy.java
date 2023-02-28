package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerEntityDestroy;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerEntityDestroy.class)
public class GPacketPlayServerEntityDestroy extends GPacket implements PacketPlayServerEntityDestroy, ReadableBuffer {
    public GPacketPlayServerEntityDestroy(UUID player, ProtocolVersion version) {
        super("PacketPlayOutEntityDestroy", player, version);
    }

    private int[] entities;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.entities = new int[byteBuf.readVarInt()];

        for (int i = 0; i < entities.length; i++) {
            this.entities[i] = byteBuf.readVarInt();
        }
    }
}
