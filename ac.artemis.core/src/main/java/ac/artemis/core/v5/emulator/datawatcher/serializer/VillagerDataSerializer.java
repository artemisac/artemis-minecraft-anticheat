package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.DataKey;
import ac.artemis.core.v5.emulator.villager.VillagerData;
import ac.artemis.core.v5.emulator.villager.profession.VillagerProfession;
import ac.artemis.core.v5.emulator.villager.profession.VillagerProfessionRegistry;
import ac.artemis.core.v5.emulator.villager.profession.VillagerProfessionRegistryFactory;
import ac.artemis.core.v5.emulator.villager.type.VillagerType;
import ac.artemis.core.v5.emulator.villager.type.VillagerTypeRegistry;
import ac.artemis.core.v5.emulator.villager.type.VillagerTypeRegistryFactory;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;

public class VillagerDataSerializer extends AbstractDataSerializer<VillagerData> {
    public VillagerDataSerializer(PlayerData data) {
        super(data);
    }

    private final VillagerTypeRegistry typeRegistry = new VillagerTypeRegistryFactory()
            .setVersion(data.getVersion())
            .build();

    private final VillagerProfessionRegistry professionRegistry = new VillagerProfessionRegistryFactory()
            .setVersion(data.getVersion())
            .build();

    @Override
    public void write(ProtocolByteBuf buf, VillagerData value) {
        buf.writeVarInt(typeRegistry.index(value.getType()));
        buf.writeVarInt(professionRegistry.index(value.getProfession()));
        buf.writeVarInt(value.getLevel());
    }

    @Override
    public VillagerData read(ProtocolByteBuf buf) {
        final VillagerType type = typeRegistry.get(buf.readVarInt());
        final VillagerProfession profession = professionRegistry.get(buf.readVarInt());
        final int level = buf.readVarInt();
        return new VillagerData(type, profession, level);
    }

    @Override
    public DataKey<VillagerData> createKey(int id) {
        return new DataKey<>(id, this);
    }

    @Override
    public VillagerData copyValue(VillagerData value)
    {
        return value;
    }
}
