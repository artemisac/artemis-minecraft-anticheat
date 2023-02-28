package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.PacketPlayClientWindowHorse;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;

import java.util.UUID;

/**
 * @author Ghast
 * @since 31/10/2020
 * ArtemisPacket © 2020
 */
@Getter
@PacketLink(PacketPlayClientWindowHorse.class)
public class GPacketPlayClientWindowHorse extends GPacket implements PacketPlayClientWindowHorse, ReadableBuffer {
    public GPacketPlayClientWindowHorse(UUID player, ProtocolVersion version) {
        super("PacketPlayInOpenHorseWindow", player, version, e -> e.isOrAbove(ProtocolVersion.V1_13));
    }


    @Override
    public void read(ProtocolByteBuf byteBuf) {

    }

}

