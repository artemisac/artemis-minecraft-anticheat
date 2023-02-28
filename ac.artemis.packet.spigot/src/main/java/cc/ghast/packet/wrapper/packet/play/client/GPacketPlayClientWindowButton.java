package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.PacketPlayClientWindowButton;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientWindowButton.class)
public class GPacketPlayClientWindowButton extends GPacket implements PacketPlayClientWindowButton, ReadableBuffer {
    public GPacketPlayClientWindowButton(UUID player, ProtocolVersion version) {
        super("PacketPlayInWindowButton", player, version);
    }

    @Override
    public void read(ProtocolByteBuf byteBuf) {

    }


}
