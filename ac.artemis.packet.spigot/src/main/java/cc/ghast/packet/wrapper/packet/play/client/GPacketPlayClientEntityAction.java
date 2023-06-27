package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientEntityAction;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientEntityAction.class)
public class GPacketPlayClientEntityAction extends GPacket implements PacketPlayClientEntityAction, ReadableBuffer {
    public GPacketPlayClientEntityAction(UUID player, ProtocolVersion version) {
        super("PacketPlayInEntityAction", player, version);
    }

    private int entityId;
    private PlayerEnums.PlayerAction action;
    private int parameter;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.entityId = byteBuf.readVarInt();
        this.action = PlayerEnums.PlayerAction.values()[byteBuf.readVarInt()];
        this.parameter = byteBuf.readVarInt();
    }


}
