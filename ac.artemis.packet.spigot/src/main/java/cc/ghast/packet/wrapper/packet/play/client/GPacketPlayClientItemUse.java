package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.PacketPlayClientItemUse;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
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
@PacketLink(PacketPlayClientItemUse.class)
public class GPacketPlayClientItemUse extends GPacket implements PacketPlayClientItemUse, ReadableBuffer{
    public GPacketPlayClientItemUse(UUID player, ProtocolVersion version) {
        super("PacketPlayInBlockPlace", player, version, e -> e.isOrAbove(ProtocolVersion.V1_9));
    }

    private PlayerEnums.Hand hand;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        // Lolz this is empty
        if (version.isBelow(ProtocolVersion.V1_9)) {
            this.hand = PlayerEnums.Hand.MAIN_HAND;
        } else {
            this.hand = PlayerEnums.Hand.values()[byteBuf.readVarInt()];
        }
    }

}

