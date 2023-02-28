package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerSpawnObject;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;

import java.util.Optional;
import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerSpawnObject.class)
public class GPacketPlayServerSpawnObject extends GPacket implements PacketPlayServerSpawnObject, ReadableBuffer {
    public GPacketPlayServerSpawnObject(UUID player, ProtocolVersion version) {
        super("PacketPlayOutSpawnObject", player, version);
    }

    public GPacketPlayServerSpawnObject(String realName, UUID player, ProtocolVersion version) {
        super(realName, player, version);
    }

    private int entityId;
    private Optional<UUID> uniqueId;
    private int type;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
    private int data;
    private Optional<Short> velocityX;
    private Optional<Short> velocityY;
    private Optional<Short> velocityZ;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.entityId = byteBuf.readVarInt();

        if (version.isOrAbove(ProtocolVersion.V1_15)) {
            this.uniqueId = Optional.of(byteBuf.readUUID());
            this.type = byteBuf.readVarInt();
            this.x = byteBuf.readDouble();
            this.y = byteBuf.readDouble();
            this.z = byteBuf.readDouble();
        } else {
            this.uniqueId = Optional.empty();
            this.type = byteBuf.readByte();
            this.x = byteBuf.readInt() / 32.0D;
            this.y = byteBuf.readInt() / 32.0D;
            this.z = byteBuf.readInt() / 32.0D;
        }

        this.pitch = byteBuf.readByte() / 256.0F * 360.0F;
        this.yaw = byteBuf.readByte() / 256.0F * 360.0F;
        this.data = byteBuf.readInt();

        if (version.isOrAbove(ProtocolVersion.V1_8) && data > 0) {
            this.velocityX = Optional.of(byteBuf.readShort());
            this.velocityY = Optional.of(byteBuf.readShort());
            this.velocityZ = Optional.of(byteBuf.readShort());
        } else {
            this.velocityX = velocityY = velocityZ = Optional.empty();
        }
    }

    public Optional<Double> getMotionX() {
        return velocityX.map(e -> e * 8000.0D);
    }

    public Optional<Double> getMotionY() {
        return velocityY.map(e -> e * 8000.0D);
    }

    public Optional<Double> getMotionZ() {
        return velocityZ.map(e -> e * 8000.0D);
    }
}
