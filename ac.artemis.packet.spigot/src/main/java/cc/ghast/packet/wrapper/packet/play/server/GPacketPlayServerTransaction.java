package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerTransaction;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import cc.ghast.packet.wrapper.packet.WriteableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerTransaction.class)
public class GPacketPlayServerTransaction extends GPacket implements PacketPlayServerTransaction, ReadableBuffer, WriteableBuffer {
    public GPacketPlayServerTransaction(UUID player, ProtocolVersion version) {
        super("PacketPlayOutTransaction", player, version);
    }

    public GPacketPlayServerTransaction(UUID player, ProtocolVersion version, byte windowId, short actionNumber, boolean accepted) {
        super("PacketPlayOutTransaction", player, version);
        this.windowId = windowId;
        this.actionNumber = actionNumber;
        this.accepted = accepted;
    }

    public GPacketPlayServerTransaction(byte windowId, short actionNumber, boolean accepted) {
        super("PacketPlayOutTransaction");
        this.windowId = windowId;
        this.actionNumber = actionNumber;
        this.accepted = accepted;
    }

    private byte windowId;
    private short actionNumber;
    private boolean accepted;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.windowId = byteBuf.readByte();
        this.actionNumber = byteBuf.readShort();
        this.accepted = byteBuf.readBoolean();
    }

    @Override
    public void write(ProtocolByteBuf byteBuf) {
        byteBuf.writeByte(windowId);
        byteBuf.writeShort(actionNumber);
        byteBuf.writeBoolean(accepted);
    }
}
