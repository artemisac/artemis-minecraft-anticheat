package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerEntity;
import ac.artemis.packet.wrapper.server.PacketPlayServerEntityRelLook;
import ac.artemis.packet.wrapper.server.PacketPlayServerEntityRelMove;
import ac.artemis.packet.wrapper.server.PacketPlayServerEntityRelMoveLook;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import cc.ghast.packet.wrapper.packet.WriteableBuffer;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@PacketLink(PacketPlayServerEntity.class)
public class GPacketPlayServerEntity extends GPacket implements PacketPlayServerEntity, ReadableBuffer, WriteableBuffer {

    protected int entityId;
    protected short x;
    protected short y;
    protected short z;
    protected byte yaw;
    protected byte pitch;
    protected boolean onGround;
    protected boolean hasLook, hasPos;

    public GPacketPlayServerEntity(UUID player, ProtocolVersion version) {
        super("PacketPlayOutEntity", player, version);
    }

    public GPacketPlayServerEntity(String realName, UUID player, ProtocolVersion version) {
        super(realName, player, version);
    }

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.entityId = byteBuf.readVarInt();
    }

    @Override
    public void write(ProtocolByteBuf byteBuf) {
        byteBuf.writeVarInt(entityId);
    }

    @PacketLink(PacketPlayServerEntityRelMove.class)
    public static class GPacketPlayServerRelEntityMove extends GPacketPlayServerEntity {
        public GPacketPlayServerRelEntityMove(UUID player, ProtocolVersion version) {
            super("PacketPlayOutRelEntityMove", player, version);
        }

        @Override
        public void read(ProtocolByteBuf byteBuf) {
            super.read(byteBuf);

            if (version.isBelow(ProtocolVersion.V1_16)) {
                this.x = byteBuf.readByte();
                this.y = byteBuf.readByte();
                this.z = byteBuf.readByte();
            } else {
                this.x = byteBuf.readShort();
                this.y = byteBuf.readShort();
                this.z = byteBuf.readShort();
            }

            this.onGround = byteBuf.readBoolean();
            this.hasPos = true;
        }

        @Override
        public void write(ProtocolByteBuf byteBuf) {
            super.write(byteBuf);

            if (version.isBelow(ProtocolVersion.V1_16)) {
                byteBuf.writeByte(x);
                byteBuf.writeByte(y);
                byteBuf.writeByte(z);
            } else {
                byteBuf.writeShort(x);
                byteBuf.writeShort(y);
                byteBuf.writeShort(z);
            }

            byteBuf.writeBoolean(onGround);
        }
    }

    @PacketLink(PacketPlayServerEntityRelLook.class)
    public static class GPacketPlayServerEntityLook extends GPacketPlayServerEntity {
        public GPacketPlayServerEntityLook(UUID player, ProtocolVersion version) {
            super("PacketPlayOutEntityLook", player, version);
        }

        @Override
        public void read(ProtocolByteBuf byteBuf) {
            super.read(byteBuf);

            this.yaw = byteBuf.readByte();
            this.pitch = byteBuf.readByte();
            this.onGround = byteBuf.readBoolean();
            this.hasLook = true;
        }

        @Override
        public void write(ProtocolByteBuf byteBuf) {
            super.write(byteBuf);

            byteBuf.writeByte(yaw);
            byteBuf.writeByte(pitch);
            byteBuf.writeBoolean(onGround);
        }
    }

    @PacketLink(PacketPlayServerEntityRelMoveLook.class)
    public static class GPacketPlayServerRelEntityMoveLook extends GPacketPlayServerEntity {
        public GPacketPlayServerRelEntityMoveLook(UUID player, ProtocolVersion version) {
            super("PacketPlayOutRelEntityMoveLook", player, version);
        }

        @Override
        public void read(ProtocolByteBuf byteBuf) {
            super.read(byteBuf);
            if (version.isBelow(ProtocolVersion.V1_16)) {
                this.x = byteBuf.readByte();
                this.y = byteBuf.readByte();
                this.z = byteBuf.readByte();
            } else {
                this.x = byteBuf.readShort();
                this.y = byteBuf.readShort();
                this.z = byteBuf.readShort();
            }

            this.yaw = byteBuf.readByte();
            this.pitch = byteBuf.readByte();
            this.onGround = byteBuf.readBoolean();
            this.hasLook = this.hasPos = true;
        }

        @Override
        public void write(ProtocolByteBuf byteBuf) {
            super.write(byteBuf);

            if (version.isBelow(ProtocolVersion.V1_16)) {
                byteBuf.writeByte(x);
                byteBuf.writeByte(y);
                byteBuf.writeByte(z);
            } else {
                byteBuf.writeShort(x);
                byteBuf.writeShort(y);
                byteBuf.writeShort(z);
            }

            byteBuf.writeByte(yaw);
            byteBuf.writeByte(pitch);
            byteBuf.writeBoolean(onGround);
        }
    }
}
