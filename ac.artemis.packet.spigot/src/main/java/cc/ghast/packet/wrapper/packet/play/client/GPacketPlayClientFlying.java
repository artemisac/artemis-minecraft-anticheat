package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientLoc;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientLoc.class)
public class GPacketPlayClientFlying extends GPacket implements PacketPlayClientLoc, ReadableBuffer {
    public GPacketPlayClientFlying(UUID player, ProtocolVersion version) {
        super("PacketPlayInFlying", player, version);
    }

    private boolean onGround;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.onGround = byteBuf.readUnsignedByte() != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GPacketPlayClientFlying that = (GPacketPlayClientFlying) o;
        return onGround == that.onGround;
    }

    @Override
    public int hashCode() {
        return Objects.hash(onGround);
    }
}
