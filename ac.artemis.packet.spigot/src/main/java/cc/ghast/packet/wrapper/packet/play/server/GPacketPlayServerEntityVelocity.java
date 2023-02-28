package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.spigot.utils.ServerUtil;
import ac.artemis.packet.wrapper.server.PacketPlayServerEntityVelocity;
import cc.ghast.packet.nms.MathHelper;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import cc.ghast.packet.wrapper.packet.WriteableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerEntityVelocity.class)
public class GPacketPlayServerEntityVelocity extends GPacket implements PacketPlayServerEntityVelocity, ReadableBuffer, WriteableBuffer {
    public GPacketPlayServerEntityVelocity(UUID player, ProtocolVersion version) {
        super("PacketPlayOutEntityVelocity", player, version);
    }

    public GPacketPlayServerEntityVelocity(int entityId, short x, short y, short z) {
        super("PacketPlayOutEntityVelocity");
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GPacketPlayServerEntityVelocity(int entityId, double x, double y, double z) {
        super("PacketPlayOutEntityVelocity");
        this.entityId = entityId;
        this.x = (short) MathHelper.floor(x / 8000.D);
        this.y = (short) MathHelper.floor(y / 8000.D);
        this.z = (short) MathHelper.floor(z / 8000.D);
    }

    private int entityId;
    private short x;
    private short y;
    private short z;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        if (version.isLegacy()) {
            this.entityId = byteBuf.readInt();
        } else {
            this.entityId = byteBuf.readVarInt();
        }

        this.x = byteBuf.readShort();
        this.y = byteBuf.readShort();
        this.z = byteBuf.readShort();
    }

    @Override
    public void write(ProtocolByteBuf byteBuf) {
        if (version.isLegacy()) {
            byteBuf.writeInt(entityId);
        } else {
            byteBuf.writeVarInt(entityId);
        }
        byteBuf.writeShort(x);
        byteBuf.writeShort(y);
        byteBuf.writeShort(z);
    }

    public double getValueX() {
        return (x / 8000.D);
    }

    public double getValueY() {
        return (y / 8000.D);
    }

    public double getValueZ() {
        return (z / 8000.D);
    }
}
