package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.core.v4.data.PlayerData;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class ItemStackSerializer extends AbstractDataSerializer<ItemStack> {
    public ItemStackSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, ItemStack value) {
        buf.writeItem(value);
    }

    @Override
    public ItemStack read(ProtocolByteBuf buf) {
        return buf.readItem();
    }

    @Override
    public ItemStack copyValue(ItemStack value) {
        return value;
    }
}
