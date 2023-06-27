package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientArmAnimation;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.mc.PlayerEnums.Hand;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientArmAnimation.class)
public class GPacketPlayClientArmAnimation extends GPacket implements PacketPlayClientArmAnimation, ReadableBuffer {
    public GPacketPlayClientArmAnimation(UUID player, ProtocolVersion version) {
        super("PacketPlayInArmAnimation", player, version);
    }

    private Hand hand;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        // Lolz this is empty
        if (version.isBelow(ProtocolVersion.V1_9)) {
            this.hand = Hand.MAIN_HAND;
        } else {
            this.hand = Hand.values()[byteBuf.readVarInt()];
        }
    }


}
