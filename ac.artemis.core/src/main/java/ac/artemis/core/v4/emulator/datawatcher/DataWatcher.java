package ac.artemis.core.v4.emulator.datawatcher;

import ac.artemis.core.v5.emulator.Emulator;
import ac.artemis.core.v5.utils.bounding.BlockPos;
import ac.artemis.core.v4.emulator.datawatcher.moderna.DataSerializer;
import ac.artemis.core.v4.emulator.datawatcher.moderna.DataSerializers;
import ac.artemis.core.v5.emulator.datawatcher.WatchableObject;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.protocol.ProtocolVersion;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ObjectUtils;

public class DataWatcher
{
    private final Emulator owner;

    /** When isBlank is true the DataWatcher is not watching any objects */
    private boolean isBlank = true;
    private static final Map<Class<?>, Integer> dataTypes = Maps.newHashMap();
    private final Map<Integer, WatchableObject> watchedObjects = new ConcurrentHashMap<>();

    /** true if one or more object was changed */
    private boolean objectChanged;

    public DataWatcher(Emulator owner)
    {
        this.owner = owner;
    }

    public <T> void addObject(int id, T object)
    {
        Integer integer = (Integer)dataTypes.get(object.getClass());

        if (integer == null)
        {
            throw new IllegalArgumentException("Unknown data type: " + object.getClass());
        }
        else if (id > 31)
        {
            throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
        }
        else if (this.watchedObjects.containsKey(id))
        {
            throw new IllegalArgumentException("Duplicate id value for " + id + "!");
        }
        else
        {
            WatchableObject datawatcher$watchableobject = new WatchableObject(integer.intValue(), id, object);
            this.watchedObjects.put(id, datawatcher$watchableobject);
            this.isBlank = false;
        }
    }

    /**
     * Add a new object for the DataWatcher to watch, using the specified data type.
     */
    public void addObjectByDataType(int id, int type)
    {
        WatchableObject datawatcher$watchableobject = new WatchableObject(type, id, (Object)null);
        this.watchedObjects.put(id, datawatcher$watchableobject);
        this.isBlank = false;
    }

    /**
     * gets the bytevalue of a watchable object
     */
    public byte getWatchableObjectByte(int id)
    {
        return (Byte) this.getWatchedObject(id).getWatchedObject();
    }

    public short getWatchableObjectShort(int id)
    {
        return (Short) this.getWatchedObject(id).getWatchedObject();
    }

    /**
     * gets a watchable object and returns it as a Integer
     */
    public int getWatchableObjectInt(int id)
    {
        return (Integer) this.getWatchedObject(id).getWatchedObject();
    }

    public float getWatchableObjectFloat(int id)
    {
        return (Float) this.getWatchedObject(id).getWatchedObject();
    }

    /**
     * gets a watchable object and returns it as a String
     */
    public String getWatchableObjectString(int id)
    {
        return (String)this.getWatchedObject(id).getWatchedObject();
    }

    /**
     * Get a watchable object as an ItemStack.
     */
    public ItemStack getWatchableObjectItemStack(int id)
    {
        return (ItemStack) this.getWatchedObject(id).getWatchedObject();
    }

    /**
     * is threadsafe, unless it throws an exception, then
     */
    private WatchableObject getWatchedObject(int id)
    {
        WatchableObject datawatcher$watchableobject;

        datawatcher$watchableobject = this.watchedObjects.get(id);

        return datawatcher$watchableobject;
    }

    public <T> void updateObject(int id, T newData)
    {
        WatchableObject datawatcher$watchableobject = this.getWatchedObject(id);

        if (datawatcher$watchableobject == null) {
            return;
        }

        if (ObjectUtils.notEqual(newData, datawatcher$watchableobject.getWatchedObject()))
        {
            datawatcher$watchableobject.setWatchedObject(newData);
            //this.owner.onDataWatcherUpdate(id);
            datawatcher$watchableobject.setWatched(true);
            this.objectChanged = true;
        }
    }

    public void setObjectWatched(int id)
    {
        this.getWatchedObject(id).setWatched(true);
        this.objectChanged = true;
    }

    /**
     * true if one or more object was changed
     */
    public boolean hasObjectChanged()
    {
        return this.objectChanged;
    }

    /**
     * Writes the list of watched objects (entity attribute of type {byte, short, int, float, string, ItemStack,
     * ChunkCoordinates}) to the specified PacketBuffer
     */
    public static void writeWatchedListToPacketBuffer(List<WatchableObject> objectsList, ProtocolByteBuf buffer) throws IOException
    {
        if (objectsList != null)
        {
            for (WatchableObject datawatcher$watchableobject : objectsList)
            {
                writeWatchableObjectToPacketBuffer(buffer, datawatcher$watchableobject);
            }
        }

        buffer.writeByte(127);
    }

    public List<WatchableObject> getChanged()
    {
        List<WatchableObject> list = null;

        if (this.objectChanged)
        {

            for (WatchableObject datawatcher$watchableobject : this.watchedObjects.values())
            {
                if (datawatcher$watchableobject.isWatched())
                {
                    datawatcher$watchableobject.setWatched(false);

                    if (list == null)
                    {
                        list = Lists.<WatchableObject>newArrayList();
                    }

                    list.add(datawatcher$watchableobject);
                }
            }

        }

        this.objectChanged = false;
        return list;
    }

    public void writeTo(ProtocolByteBuf buffer) throws IOException
    {
        for (WatchableObject datawatcher$watchableobject : this.watchedObjects.values())
        {
            writeWatchableObjectToPacketBuffer(buffer, datawatcher$watchableobject);
        }

        buffer.writeByte(127);
    }

    public List<WatchableObject> getAllWatched()
    {
        List<WatchableObject> list = null;

        for (WatchableObject datawatcher$watchableobject : this.watchedObjects.values())
        {
            if (list == null)
            {
                list = Lists.<WatchableObject>newArrayList();
            }

            list.add(datawatcher$watchableobject);
        }

        return list;
    }

    /**
     * Writes a watchable object (entity attribute of type {byte, short, int, float, string, ItemStack,
     * ChunkCoordinates}) to the specified PacketBuffer
     */
    private static void writeWatchableObjectToPacketBuffer(ProtocolByteBuf buffer, WatchableObject object) throws IOException
    {
        int i = (object.getObjectType() << 5 | object.getDataValueId() & 31) & 255;
        buffer.writeByte(i);

        switch (object.getObjectType())
        {
            case 0:
                buffer.writeByte((Byte) object.getWatchedObject());
                break;

            case 1:
                buffer.writeShort((Short) object.getWatchedObject());
                break;

            case 2:
                buffer.writeInt((Integer) object.getWatchedObject());
                break;

            case 3:
                buffer.writeFloat((Float) object.getWatchedObject());
                break;

            case 4:
                buffer.writeString((String)object.getWatchedObject());
                break;

            case 5:
                ItemStack itemstack = (ItemStack)object.getWatchedObject();
                buffer.writeItem(itemstack);
                break;

            case 6:
                BlockPos blockpos = (BlockPos)object.getWatchedObject();
                buffer.writeInt(blockpos.getX());
                buffer.writeInt(blockpos.getY());
                buffer.writeInt(blockpos.getZ());
                break;

            /*case 7:
                Rotations rotations = (Rotations)object.getObject();
                buffer.writeFloat(rotations.getX());
                buffer.writeFloat(rotations.getY());
                buffer.writeFloat(rotations.getZ());*/
        }
    }

    public static List<WatchableObject> readWatchedListFromPacketBuffer(ProtocolByteBuf buffer) {
        if (buffer.getVersion().isOrAbove(ProtocolVersion.V1_9)) {
            List < WatchableObject> list = null;
            int id;

            while ((id = buffer.readUnsignedByte()) != 255)
            {
                if (list == null)
                {
                    list = new ArrayList<>();
                }

                int type = buffer.readVarInt();
                DataSerializer<?> dataserializer = DataSerializers.getSerializer(type);

                if (dataserializer == null) {

                    continue;
                    //throw new DecoderException("Unknown serializer type " + type);
                }

                try {
                    //final DataParameter<?> parameter = dataserializer.createKey(i);
                    list.add(new WatchableObject(type, id, dataserializer.read(buffer)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return list;
        }

        List<WatchableObject> list = new ArrayList<>();

        for (int i = buffer.readByte(); i != 127; i = buffer.readByte()) {

            int j = (i & 224) >> 5;
            int k = i & 31;
            WatchableObject datawatcher$watchableobject = null;

            switch (j)
            {
                case 0:
                    datawatcher$watchableobject = new WatchableObject(j, k, buffer.readByte());
                    break;

                case 1:
                    datawatcher$watchableobject = new WatchableObject(j, k, buffer.readShort());
                    break;

                case 2:
                    datawatcher$watchableobject = new WatchableObject(j, k, buffer.readInt());
                    break;

                case 3:
                    datawatcher$watchableobject = new WatchableObject(j, k, buffer.readFloat());
                    break;

                case 4:
                    datawatcher$watchableobject = new WatchableObject(j, k, buffer.readStringBuf(32767));
                    break;

                case 5:
                    datawatcher$watchableobject = new WatchableObject(j, k, buffer.readItem());
                    break;

                case 6:
                    int l = buffer.readInt();
                    int i1 = buffer.readInt();
                    int j1 = buffer.readInt();
                    datawatcher$watchableobject = new WatchableObject(j, k, new BlockPos(l, i1, j1));
                    break;

                /*case 7:
                    float f = buffer.readFloat();
                    float f1 = buffer.readFloat();
                    float f2 = buffer.readFloat();
                    datawatcher$watchableobject = new WatchableObject(j, k, new Rotations(f, f1, f2));*/
            }

            list.add(datawatcher$watchableobject);
        }

        return list;
    }

    public void updateWatchedObjectsFromList(List<WatchableObject> p_75687_1_)
    {

        for (WatchableObject datawatcher$watchableobject : p_75687_1_)
        {
            WatchableObject datawatcher$watchableobject1 = this.watchedObjects.get(datawatcher$watchableobject.getDataValueId());

            if (datawatcher$watchableobject1 != null)
            {
                datawatcher$watchableobject1.setWatchedObject(datawatcher$watchableobject.getWatchedObject());
                //this.owner.onDataWatcherUpdate(datawatcher$watchableobject.getDataValueId());
            }
        }

        this.objectChanged = true;
    }

    public boolean getIsBlank()
    {
        return this.isBlank;
    }

    public void func_111144_e()
    {
        this.objectChanged = false;
    }

    static
    {
        dataTypes.put(Byte.class, 0);
        dataTypes.put(Short.class, 1);
        dataTypes.put(Integer.class, 2);
        dataTypes.put(Float.class, 3);
        dataTypes.put(String.class, 4);
        dataTypes.put(ItemStack.class, 5);
        dataTypes.put(BlockPos.class, 6);
        dataTypes.put(Object.class, 7);
    }

    
}
