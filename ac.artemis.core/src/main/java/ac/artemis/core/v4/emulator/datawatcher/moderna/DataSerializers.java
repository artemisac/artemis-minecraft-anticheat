package ac.artemis.core.v4.emulator.datawatcher.moderna;

import ac.artemis.packet.minecraft.Unsafe;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.core.v4.emulator.moderna.ITextComponent;
import ac.artemis.core.v4.emulator.moderna.IntIdentityHashBiMap;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.FPoint;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.google.common.base.Optional;

import java.io.IOException;
import java.util.UUID;

public class DataSerializers
{
    private static final IntIdentityHashBiMap< DataSerializer<? >> REGISTRY = new IntIdentityHashBiMap<>(16);
    public static final DataSerializer<Byte> BYTE = new DataSerializer<Byte>()
    {
        public void write(ProtocolByteBuf buf, Byte value)
        {
            buf.writeByte(value);
        }
        public Byte read(ProtocolByteBuf buf) throws IOException
        {
            return buf.readByte();
        }
        public DataParameter<Byte> createKey(int id)
        {
            return new DataParameter<Byte>(id, this);
        }
        public Byte copyValue(Byte value)
        {
            return value;
        }
    };
    public static final DataSerializer<Integer> VARINT = new DataSerializer<Integer>()
    {
        public void write(ProtocolByteBuf buf, Integer value)
        {
            buf.writeVarInt(value);
        }
        public Integer read(ProtocolByteBuf buf) throws IOException
        {
            return buf.readVarInt();
        }
        public DataParameter<Integer> createKey(int id)
        {
            return new DataParameter<Integer>(id, this);
        }
        public Integer copyValue(Integer value)
        {
            return value;
        }
    };
    public static final DataSerializer<Float> FLOAT = new DataSerializer<Float>()
    {
        public void write(ProtocolByteBuf buf, Float value)
        {
            buf.writeFloat(value);
        }
        public Float read(ProtocolByteBuf buf) throws IOException
        {
            return buf.readFloat();
        }
        public DataParameter<Float> createKey(int id)
        {
            return new DataParameter<Float>(id, this);
        }
        public Float copyValue(Float value)
        {
            return value;
        }
    };
    public static final DataSerializer<String> STRING = new DataSerializer<String>()
    {
        public void write(ProtocolByteBuf buf, String value)
        {
            buf.writeString(value);
        }
        public String read(ProtocolByteBuf buf) throws IOException
        {
            return buf.readStringBuf(32767);
        }
        public DataParameter<String> createKey(int id)
        {
            return new DataParameter<String>(id, this);
        }
        public String copyValue(String value)
        {
            return value;
        }
    };
    public static final DataSerializer<ITextComponent> TEXT_COMPONENT = new DataSerializer<ITextComponent>()
    {
        public void write(ProtocolByteBuf buf, ITextComponent value)
        {
            buf.writeString(ITextComponent.Serializer.componentToJson(value));
        }
        public ITextComponent read(ProtocolByteBuf buf) throws IOException
        {
            return ITextComponent.Serializer.jsonToComponent(buf.readStringBuf(32767));
        }
        public DataParameter<ITextComponent> createKey(int id)
        {
            return new DataParameter<ITextComponent>(id, this);
        }
        public ITextComponent copyValue(ITextComponent value)
        {
            return value.createCopy();
        }
    };
    public static final DataSerializer<ItemStack> ITEM_STACK = new DataSerializer<ItemStack>()
    {
        public void write(ProtocolByteBuf buf, ItemStack value)
        {
            buf.writeItem(value);
        }
        public ItemStack read(ProtocolByteBuf buf) throws IOException
        {
            return buf.readItem();
        }
        public DataParameter<ItemStack> createKey(int id)
        {
            return new DataParameter<ItemStack>(id, this);
        }
        public ItemStack copyValue(ItemStack value)
        {
            return value;
        }
    };
    public static final DataSerializer<Optional<Object>> OPTIONAL_BLOCK_STATE = new DataSerializer<Optional<Object>>()
    {
        public void write(ProtocolByteBuf buf, Optional<Object> value)
        {
            /*if (value.isPresent())
            {
                buf.writeVarInt(Block.getStateId(value.get()));
            }
            else
            {*/
                buf.writeVarInt(0);
            /*}*/
        }
        public Optional<Object> read(ProtocolByteBuf buf) throws IOException
        {
            int i = buf.readVarInt();
            return /*i == 0 ? Optional.absent() : Optional.of(Block.getStateById(i))*/ Optional.absent();
        }
        public DataParameter<Optional<Object>> createKey(int id)
        {
            return new DataParameter<Optional<Object>>(id, this);
        }
        public Optional<Object> copyValue(Optional<Object> value)
        {
            return value;
        }
    };
    public static final DataSerializer<Boolean> BOOLEAN = new DataSerializer<Boolean>()
    {
        public void write(ProtocolByteBuf buf, Boolean value)
        {
            buf.writeBoolean(value.booleanValue());
        }
        public Boolean read(ProtocolByteBuf buf) throws IOException
        {
            return buf.readBoolean();
        }
        public DataParameter<Boolean> createKey(int id)
        {
            return new DataParameter<Boolean>(id, this);
        }
        public Boolean copyValue(Boolean value)
        {
            return value;
        }
    };
    public static final DataSerializer<FPoint> ROTATIONS = new DataSerializer<FPoint>()
    {
        public void write(ProtocolByteBuf buf, FPoint value)
        {
            buf.writeFloat(value.getX());
            buf.writeFloat(value.getY());
            buf.writeFloat(value.getZ());
        }
        public FPoint read(ProtocolByteBuf buf) throws IOException
        {
            return new FPoint(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
        public DataParameter<FPoint> createKey(int id)
        {
            return new DataParameter<FPoint>(id, this);
        }
        public FPoint copyValue(FPoint value)
        {
            return value;
        }
    };
    public static final DataSerializer<BlockPosition> BLOCK_POS = new DataSerializer<BlockPosition>()
    {
        public void write(ProtocolByteBuf buf, BlockPosition value)
        {
            buf.writeBlockPositionIntoLong(value);
        }
        public BlockPosition read(ProtocolByteBuf buf) throws IOException
        {
            return buf.readBlockPositionFromLong();
        }
        public DataParameter<BlockPosition> createKey(int id)
        {
            return new DataParameter<BlockPosition>(id, this);
        }
        public BlockPosition copyValue(BlockPosition value)
        {
            return value;
        }
    };
    public static final DataSerializer<Optional<BlockPosition>> OPTIONAL_BLOCK_POS = new DataSerializer<Optional<BlockPosition>>()
    {
        public void write(ProtocolByteBuf buf, Optional<BlockPosition> value)
        {
            buf.writeBoolean(value.isPresent());

            if (value.isPresent())
            {
                buf.writeBlockPositionIntoLong(value.get());
            }
        }
        public Optional<BlockPosition> read(ProtocolByteBuf buf) throws IOException
        {
            return !buf.readBoolean() ? Optional.absent() : Optional.of(buf.readBlockPositionFromLong());
        }
        public DataParameter<Optional<BlockPosition>> createKey(int id)
        {
            return new DataParameter<Optional<BlockPosition>>(id, this);
        }
        public Optional<BlockPosition> copyValue(Optional<BlockPosition> value)
        {
            return value;
        }
    };
    public static final DataSerializer<EnumFacing> FACING = new DataSerializer<EnumFacing>()
    {
        public void write(ProtocolByteBuf buf, EnumFacing value)
        {
            buf.writeVarInt(value.ordinal());
        }
        public EnumFacing read(ProtocolByteBuf buf) throws IOException
        {
            return EnumFacing.values()[buf.readVarInt()];
        }
        public DataParameter<EnumFacing> createKey(int id)
        {
            return new DataParameter<EnumFacing>(id, this);
        }
        public EnumFacing copyValue(EnumFacing value)
        {
            return value;
        }
    };
    public static final DataSerializer<Optional<UUID>> OPTIONAL_UNIQUE_ID = new DataSerializer<Optional<UUID>>()
    {
        public void write(ProtocolByteBuf buf, Optional<UUID> value)
        {
            buf.writeBoolean(value.isPresent());

            if (value.isPresent())
            {
                buf.writeUUID(value.get());
            }
        }
        public Optional<UUID> read(ProtocolByteBuf buf) throws IOException
        {
            return !buf.readBoolean() ? Optional.absent() : Optional.of(buf.readUUID());
        }
        public DataParameter<Optional<UUID>> createKey(int id)
        {
            return new DataParameter<Optional<UUID>>(id, this);
        }
        public Optional<UUID> copyValue(Optional<UUID> value)
        {
            return value;
        }
    };
    public static final DataSerializer<CompoundTag> COMPOUND_TAG = new DataSerializer<CompoundTag>()
    {
        public void write(ProtocolByteBuf buf, CompoundTag value)
        {
            buf.writeTag(value);
        }
        public CompoundTag read(ProtocolByteBuf buf) throws IOException
        {
            return buf.readTag();
        }
        public DataParameter<CompoundTag> createKey(int id)
        {
            return new DataParameter<CompoundTag>(id, this);
        }
        public CompoundTag copyValue(CompoundTag value)
        {
            return value.clone();
        }
    };

    public static void registerSerializer(DataSerializer<?> serializer)
    {
        REGISTRY.add(serializer);
    }

    public static DataSerializer<?> getSerializer(int id)
    {
        return (DataSerializer)REGISTRY.get(id);
    }

    public static int getSerializerId(DataSerializer<?> serializer)
    {
        return REGISTRY.getId(serializer);
    }

    static {
        registerSerializer(BYTE);
        registerSerializer(VARINT);
        registerSerializer(FLOAT);
        registerSerializer(STRING);
        registerSerializer(TEXT_COMPONENT);
        registerSerializer(ITEM_STACK);
        registerSerializer(BOOLEAN);
        registerSerializer(ROTATIONS);
        registerSerializer(BLOCK_POS);
        registerSerializer(OPTIONAL_BLOCK_POS);
        registerSerializer(FACING);
        registerSerializer(OPTIONAL_UNIQUE_ID);
        registerSerializer(OPTIONAL_BLOCK_STATE);
        registerSerializer(COMPOUND_TAG);
    }
}
