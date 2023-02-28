package cc.ghast.packet.buffer.types.minecraft;

import ac.artemis.packet.minecraft.EnchantType;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.buffer.BufConverter;
import cc.ghast.packet.buffer.types.Converters;
import cc.ghast.packet.buffer.types.wrap.BukkitItemStack;
import cc.ghast.packet.wrapper.nbt.WrappedItem;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.reflections.ReflectUtil;

import java.io.IOException;
import java.util.Map;

/**
 * @author Ghast
 * @since 30/08/2020
 * Artemis © 2020
 */
public class ItemStackConverter extends BufConverter<ItemStack> {
    public ItemStackConverter() {
        super("ItemStack", ItemStack.class);
    }

    @Override
    public void write(MutableByteBuf buffer, ItemStack value) {
        if (value == null || value.getType().getId()== -1) {
            buffer.writeShort(-1);
        } else {
            buffer.writeShort(value.getType().getId());
            buffer.writeByte(value.getAmount());
            // Todo Fix this
            Converters.NMS_NBT.write(buffer, ReflectUtil.getCompoundTagFromItem(value.v()));
        }
    }

    @Override
    public ItemStack read(MutableByteBuf buffer, ProtocolVersion version, Object... args) throws IOException {
        short id = buffer.readShort();
        if (id >= 0) {
            byte amount = buffer.readByte();
            short data = buffer.readShort();
            Object tag = Converters.NMS_NBT.read(buffer, version);
            WrappedItem item = new WrappedItem(id, amount, data, tag);
            final Object wrapped = ReflectUtil.getItemFromWrapper(item);
            return wrapped == null ? null : new BukkitItemStack((org.bukkit.inventory.ItemStack) wrapped);
        }
        return null;
    }
}
