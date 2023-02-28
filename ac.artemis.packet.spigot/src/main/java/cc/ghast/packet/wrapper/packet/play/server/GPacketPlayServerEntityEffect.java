package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerEntityEffect;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerEntityEffect.class)
public class GPacketPlayServerEntityEffect extends GPacket implements PacketPlayServerEntityEffect, ReadableBuffer {
    public GPacketPlayServerEntityEffect(UUID player, ProtocolVersion version) {
        super("PacketPlayOutEntityEffect", player, version);
    }

    private int entityId;
    private byte effectId;
    private byte amplifier;
    private int duration;
    private boolean showParticles;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.entityId = byteBuf.readVarInt();
        this.effectId = byteBuf.readByte();
        this.amplifier = byteBuf.readByte();
        this.duration = byteBuf.readVarInt();
        this.showParticles = byteBuf.readByte() == (byte) 1;
    }
}
