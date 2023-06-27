package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientWindowClick;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientWindowClick.class)
public class GPacketPlayClientWindowClick extends GPacket implements PacketPlayClientWindowClick, ReadableBuffer {
    public GPacketPlayClientWindowClick(UUID player, ProtocolVersion version) {
        super("PacketPlayInWindowClick", player, version);
    }

    public GPacketPlayClientWindowClick(String realName, UUID player, ProtocolVersion version) {
        super(realName, player, version);
    }

    private byte windowId;
    private short slot;
    private byte button;
    private short actionNumber;
    private int shiftedMode;

    @Deprecated
    private SlotType mode;

    private SimpleSlotType simpleMode;

    @Deprecated
    private ItemStack clickedItem;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.windowId = byteBuf.readByte();
        this.slot = byteBuf.readShort();
        this.button = byteBuf.readByte();
        this.actionNumber = byteBuf.readShort();


        if (version.isOrBelow(ProtocolVersion.V1_8_9)) {
            this.shiftedMode = byteBuf.readByte();
        } else if (version.isOrBelow(ProtocolVersion.V1_9_4)) {
            this.shiftedMode = byteBuf.readVarInt();
            this.clickedItem = byteBuf.readItem();
        }

        try {
            this.mode = types[shiftedMode][button];
            this.simpleMode = SimpleSlotType.values()[shiftedMode];
        } catch (Exception e){
            // Ignore
        }
    }



    private static final SlotType[][] types = {
            {SlotType.LEFT_MOUSE_CLICK, SlotType.RIGHT_MOUSE_CLICK},
            {SlotType.SHIFT_LEFT_MOUSE_CLICK, SlotType.SHIFT_RIGHT_MOUSE_CLICK},
            {SlotType.NUMBER_KEY_1, SlotType.NUMBER_KEY_2, SlotType.NUMBER_KEY_3, SlotType.NUMBER_KEY_4, SlotType.NUMBER_KEY_5, SlotType.NUMBER_KEY_6, SlotType.NUMBER_KEY_7, SlotType.NUMBER_KEY_8, SlotType.NUMBER_KEY_9},
            {SlotType.MIDDLE_CLICK},
            {SlotType.DROP_KEY, SlotType.DROP_STACK, SlotType.LEFT_CLICK_OUTSIDE_INV, SlotType.RIGHT_CLICK_OUTSIDE_INV},
            {SlotType.START_LEFT_MOUSE_DRAG, SlotType.START_RIGHT_MOUSE_DRAG, SlotType.ADD_SLOT_LEFT_MOUSE_DRAG, SlotType.ADD_SLOT_RIGHT_MOUSE_DRAG, SlotType.END_LEFT_MOUSE_DRAG, SlotType.END_RIGHT_MOUSE_DRAG},
            {SlotType.DOUBLE_CLICK}
    };

    enum SimpleSlotType {
        PICKUP,
        QUICK_MOVE,
        SWAP,
        CLONE,
        THROW,
        QUICK_CRAFT,
        PICKUP_ALL;
    }

    enum SlotType {
        LEFT_MOUSE_CLICK,
        RIGHT_MOUSE_CLICK,
        SHIFT_LEFT_MOUSE_CLICK,
        SHIFT_RIGHT_MOUSE_CLICK,
        NUMBER_KEY_1,
        NUMBER_KEY_2,
        NUMBER_KEY_3,
        NUMBER_KEY_4,
        NUMBER_KEY_5,
        NUMBER_KEY_6,
        NUMBER_KEY_7,
        NUMBER_KEY_8,
        NUMBER_KEY_9,
        MIDDLE_CLICK,
        DROP_KEY,
        DROP_STACK,
        LEFT_CLICK_OUTSIDE_INV,
        RIGHT_CLICK_OUTSIDE_INV,
        START_LEFT_MOUSE_DRAG,
        START_RIGHT_MOUSE_DRAG,
        ADD_SLOT_LEFT_MOUSE_DRAG,
        ADD_SLOT_RIGHT_MOUSE_DRAG,
        END_LEFT_MOUSE_DRAG,
        END_RIGHT_MOUSE_DRAG,
        DOUBLE_CLICK
        ;
    }
}
