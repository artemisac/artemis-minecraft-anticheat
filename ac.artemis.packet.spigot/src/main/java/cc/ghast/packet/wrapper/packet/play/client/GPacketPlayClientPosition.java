package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.spigot.utils.ServerUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientLocPosition;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientLocPosition.class)
public class GPacketPlayClientPosition extends GPacket implements PacketPlayClientLocPosition, ReadableBuffer {
    public GPacketPlayClientPosition(UUID player, ProtocolVersion version) {
        super((ServerUtil.getGameVersion().isBelow(ProtocolVersion.V1_8) ? "" : "PacketPlayInFlying$")
                + "PacketPlayInPosition", player, version);
    }

    private double x;
    private double y;
    private double z;

    private boolean onGround;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.x = byteBuf.readDouble();
        this.y = byteBuf.readDouble();
        this.z = byteBuf.readDouble();

        this.onGround = byteBuf.readUnsignedByte() != 0;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public boolean isOnGround() {
        return this.onGround;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GPacketPlayClientPosition that = (GPacketPlayClientPosition) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0 && onGround == that.onGround;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, onGround);
    }
}
