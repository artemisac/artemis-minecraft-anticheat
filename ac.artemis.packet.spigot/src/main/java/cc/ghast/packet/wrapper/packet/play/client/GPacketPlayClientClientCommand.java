package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientCommand;
import ac.artemis.packet.wrapper.mc.ClientCommand;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientCommand.class)
public class GPacketPlayClientClientCommand extends GPacket implements PacketPlayClientCommand, ReadableBuffer {
    public GPacketPlayClientClientCommand(UUID player, ProtocolVersion version) {
        super("PacketPlayInClientCommand", player, version);
    }

    private PlayerEnums.ClientCommand command;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.command = PlayerEnums.ClientCommand.values()[byteBuf.readVarInt()];
    }

    @Override
    public ClientCommand getClientCommand() {
        return ClientCommand.values()[command.ordinal()];
    }
}
