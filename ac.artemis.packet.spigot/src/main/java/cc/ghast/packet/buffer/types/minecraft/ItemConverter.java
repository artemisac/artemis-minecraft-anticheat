package cc.ghast.packet.buffer.types.minecraft;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.buffer.BufConverter;
import cc.ghast.packet.buffer.types.Converters;
import cc.ghast.packet.wrapper.nbt.WrappedItem;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;

import java.io.IOException;

/**
 * @author Ghast
 * @since 09-May-20
 */
public class ItemConverter extends BufConverter<WrappedItem> {

    public ItemConverter() {
        super("WrappedItem", WrappedItem.class);
    }

    @Override
    public void write(MutableByteBuf buffer, WrappedItem value) throws IOException {
        if (value == null || value.getId() == -1) {
            buffer.writeShort(-1);
        } else {
            buffer.writeShort(value.getId());
            buffer.writeByte(value.getAmount());
            // Todo Fix this
            //Converters.NBT.write(buffer, value.getTag());
        }
    }

    @Override
    public WrappedItem read(MutableByteBuf buffer, ProtocolVersion version, Object... args) throws IOException {
        short id = buffer.readShort();
        byte amount = buffer.readByte();
        CompoundTag tag = Converters.NBT.read(buffer, version);
        return new WrappedItem(id, amount, tag);
    }

}
