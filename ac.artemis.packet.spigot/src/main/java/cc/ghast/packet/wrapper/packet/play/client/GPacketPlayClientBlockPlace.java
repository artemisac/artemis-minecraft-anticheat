package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.spigot.utils.ServerUtil;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientBlockPlace;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.buffer.types.Converters;
import cc.ghast.packet.nms.EnumDirection;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import cc.ghast.packet.wrapper.bukkit.Vector3D;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientBlockPlace.class)
public class GPacketPlayClientBlockPlace extends GPacket implements PacketPlayClientBlockPlace, ReadableBuffer {
    public GPacketPlayClientBlockPlace(UUID player, ProtocolVersion version) {
        super(ServerUtil.getGameVersion().isBelow(ProtocolVersion.V1_11) ? "PacketPlayInBlockPlace"
                : "PacketPlayInUseItem", player, version);
    }
    private Optional<EnumDirection> direction;
    @Deprecated
    private int directionId;
    private Optional<ItemStack> item;
    private BlockPosition position;
    private Vector3D vector;
    private PlayerEnums.Hand hand;

    @Override
    public void read(ProtocolByteBuf byteBuf) {

        final boolean legacy = gameVersion.isOrBelow(ProtocolVersion.V1_8_9);
        final int direction;
        if (legacy) {

            // 1.6.4 - 1.7.10 version range
            if (version.isLegacy()) {
                // Position
                int x = byteBuf.readInt();
                int y = byteBuf.readByte();
                int z = byteBuf.readInt();
                this.position = new BlockPosition(x, y, z);
            }

            // 1.8 - 1.8.8 version range
            else if (version.isBelow(ProtocolVersion.V1_9)) {
                // Position
                this.position = Converters.LOCATION_LONG.read(byteBuf.getByteBuf(), version);
            }

            // Direction
            direction = byteBuf.readUnsignedByte();

            this.directionId = direction;

            // Item
            try {
                this.item = Optional.ofNullable(Converters.ITEM_STACK.read(byteBuf.getByteBuf(), version));
            } catch (IOException e){
                e.printStackTrace();
            }
            this.hand = PlayerEnums.Hand.MAIN_HAND;
        } else {
            this.position = byteBuf.readBlockPositionFromLong();
            // Direction
            direction = byteBuf.readVarInt();

            this.directionId = direction;

            this.hand = PlayerEnums.Hand.values()[byteBuf.readVarInt()];

            this.item = Optional.empty();
        }

        if (direction == 255) {
            this.direction = Optional.empty();
        } else {
            this.direction = Optional.of(EnumDirection.values()[direction]);
        }

        float x = (float) byteBuf.readUnsignedByte() / 16.0F;
        float y = (float) byteBuf.readUnsignedByte() / 16.0F;
        float z = (float) byteBuf.readUnsignedByte() / 16.0F;
        this.vector = new Vector3D(x, y, z);
    }
}
