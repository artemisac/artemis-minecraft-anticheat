package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientSetCreativeSlot;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientSetCreativeSlot.class)
public class GPacketPlayClientSetCreativeSlot extends GPacket implements PacketPlayClientSetCreativeSlot, ReadableBuffer {
    public GPacketPlayClientSetCreativeSlot(UUID player, ProtocolVersion version) {
        super("PacketPlayInSetCreativeSlot", player, version);
    }

    private int slot;
    private ItemStack item;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.slot = byteBuf.readShort();
        this.item = byteBuf.readItem();
    }
}
