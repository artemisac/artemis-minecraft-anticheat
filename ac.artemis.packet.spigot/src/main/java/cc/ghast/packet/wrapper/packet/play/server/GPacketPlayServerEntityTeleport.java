package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerEntityTeleport;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import cc.ghast.packet.wrapper.packet.WriteableBuffer;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@PacketLink(PacketPlayServerEntityTeleport.class)
public class GPacketPlayServerEntityTeleport extends GPacket implements PacketPlayServerEntityTeleport, ReadableBuffer, WriteableBuffer {
    public GPacketPlayServerEntityTeleport(UUID player, ProtocolVersion version) {
        super("PacketPlayOutEntityTeleport", player, version);
    }

    private int entityId;
    private int x;
    private int y;
    private int z;
    private byte yaw;
    private byte pitch;
    private boolean onGround;


    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.entityId = byteBuf.readVarInt();
        this.x = byteBuf.readInt();
        this.y = byteBuf.readInt();
        this.z = byteBuf.readInt();
        this.yaw = byteBuf.readByte();
        this.pitch = byteBuf.readByte();
        this.onGround = byteBuf.readBoolean();
    }

    @Override
    public void write(ProtocolByteBuf byteBuf) {
        byteBuf.writeVarInt(entityId);
        byteBuf.writeInt(x);
        byteBuf.writeInt(y);
        byteBuf.writeInt(z);
        byteBuf.writeByte(yaw);
        byteBuf.writeByte(pitch);
        byteBuf.writeBoolean(onGround);
    }

    public double getValueX() {
        return x / 32.D;
    }

    public double getValueY() {
        return y / 32.D;
    }

    public double getValueZ() {
        return z / 32.D;
    }

    public float getValueYaw() {
        return (yaw * 360.0F / 256.0F);
    }

    public float getValuePitch() {
        return (pitch * 360.0F / 256.0F);
    }
}
