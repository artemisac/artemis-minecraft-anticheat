package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientTransaction;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientTransaction.class)
public class GPacketPlayClientTransaction extends GPacket implements PacketPlayClientTransaction, ReadableBuffer {
    public GPacketPlayClientTransaction(UUID player, ProtocolVersion version) {
        super("PacketPlayInTransaction", player, version);
    }

    private byte windowId;
    private short actionNumber;
    private boolean accepted;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.windowId = byteBuf.readByte();
        this.actionNumber = byteBuf.readShort();
        this.accepted = byteBuf.readByte() != 0;
    }
}
