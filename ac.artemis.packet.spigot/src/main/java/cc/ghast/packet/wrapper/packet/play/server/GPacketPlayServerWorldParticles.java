package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.server.PacketPlayServerBed;
import ac.artemis.packet.wrapper.server.PacketPlayServerWorldParticles;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.bukkit.Particle;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import cc.ghast.packet.wrapper.packet.WriteableBuffer;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@PacketLink(PacketPlayServerWorldParticles.class)
public class GPacketPlayServerWorldParticles extends GPacket implements PacketPlayServerBed, ReadableBuffer, WriteableBuffer {
    public GPacketPlayServerWorldParticles(UUID player, ProtocolVersion version) {
        super("PacketPlayOutWorldParticles", player, version);
    }

    public GPacketPlayServerWorldParticles(String realName, UUID player, ProtocolVersion version, Particle type,
                                           float x, float y, float z, float offsetX, float offsetY, float offsetZ,
                                           float particleSpeed, int particleCount, boolean longDistance) {
        super(realName, player, version);
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.particleSpeed = particleSpeed;
        this.particleCount = particleCount;
        this.longDistance = longDistance;
    }

    private Particle type;
    private float x;
    private float y;
    private float z;
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private float particleSpeed;
    private int particleCount;
    private boolean longDistance;
    /**
     * These are the block/item ids and possibly metaData ids that are used to color or texture the particle.
     */
    private int[] particleArguments;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        try {
            this.type = Particle.values()[byteBuf.readInt()];
        } catch (Exception e) { }


        if (this.type == null) {
            this.type = Particle.BARRIER;
        }

        this.longDistance = byteBuf.readBoolean();
        this.x = byteBuf.readFloat();
        this.y = byteBuf.readFloat();
        this.z = byteBuf.readFloat();
        this.offsetX = byteBuf.readFloat();
        this.offsetY = byteBuf.readFloat();
        this.offsetZ = byteBuf.readFloat();
        this.particleSpeed = byteBuf.readFloat();
        this.particleCount = byteBuf.readInt();

        final int size = this.type.getArgumentCount();
        this.particleArguments = new int[size];

        for (int j = 0; j < size; ++j) {
            this.particleArguments[j] = byteBuf.readVarInt();
        }
    }

    @Override
    public void write(ProtocolByteBuf byteBuf) {
        byteBuf.writeInt(this.type.getParticleID());
        byteBuf.writeBoolean(this.longDistance);
        byteBuf.writeFloat(this.x);
        byteBuf.writeFloat(this.y);
        byteBuf.writeFloat(this.z);
        byteBuf.writeFloat(this.offsetX);
        byteBuf.writeFloat(this.offsetY);
        byteBuf.writeFloat(this.offsetZ);
        byteBuf.writeFloat(this.particleSpeed);
        byteBuf.writeInt(this.particleCount);
        final int size = this.type.getArgumentCount();

        for (int j = 0; j < size; ++j) {
            byteBuf.writeVarInt(-this.particleArguments[j]);
        }
    }
}
