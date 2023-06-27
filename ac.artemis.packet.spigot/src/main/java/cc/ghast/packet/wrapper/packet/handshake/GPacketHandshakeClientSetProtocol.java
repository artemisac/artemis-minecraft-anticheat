package cc.ghast.packet.wrapper.packet.handshake;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.handshake.PacketHandshakeClientSetProtocol;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketHandshakeClientSetProtocol.class)
public class GPacketHandshakeClientSetProtocol extends GPacket implements ReadableBuffer {
    public GPacketHandshakeClientSetProtocol(UUID player, ProtocolVersion version) {
        super("PacketHandshakingInSetProtocol", player, version);
    }

    private int protocolVersion;
    private String serverAddress;
    private short serverPort;
    private State nextState;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.protocolVersion = byteBuf.readVarInt();
        this.serverAddress = byteBuf.readString();
        this.serverPort = (short) byteBuf.readUnsignedShort();
        this.nextState = byteBuf.readVarInt() == 1 ? State.STATUS : State.LOGIN;
    }

    @AllArgsConstructor
    @Getter
    public enum State {
        STATUS(1),
        LOGIN(2);
        private final int id;
    }
}
