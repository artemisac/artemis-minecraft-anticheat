package ac.artemis.core.v5.emulator.datawatcher;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataWatcher {
    private final DataWatcherReader reader;
    private final DataWatcherRegistry registry;
    private final Map<Integer, WatchableObject> dataWatcherMap = new ConcurrentHashMap<>();

    public DataWatcher(PlayerData data) {
        this.reader = new DataWatcherFactory().setData(data).build();
        this.registry = new DataWatcherRegistryFactory().setData(data).build();
    }

    public <T> void addObject(int id, T object) {
        final DataSerializer<?> dataSerializer = registry.get(id);

        if (dataSerializer == null) {
            throw new IllegalArgumentException("Unknown data type: " + object.getClass());
        }

        else if (id > registry.size()) {
            throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
        }

        else if (this.dataWatcherMap.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate id value for " + id + "!");
        }

        else {
            final WatchableObject watchableObject = new WatchableObject(id, id & 31, object);
            this.dataWatcherMap.put(id, watchableObject);
        }
    }

    public WatchableObject getWatchedObject(int id) {
        return this.dataWatcherMap.get(id);
    }

    public byte getWatchableObjectByte(int id) {
        return (Byte) this.getWatchedObject(id).getWatchedObject();
    }

    public short getWatchableObjectShort(int id) {
        return (Short) this.getWatchedObject(id).getWatchedObject();
    }

    public int getWatchableObjectInt(int id) {
        return (Integer) this.getWatchedObject(id).getWatchedObject();
    }

    public float getWatchableObjectFloat(int id) {
        return (Float) this.getWatchedObject(id).getWatchedObject();
    }

    public String getWatchableObjectString(int id) {
        return (String)this.getWatchedObject(id).getWatchedObject();
    }

    public ItemStack getWatchableObjectItemStack(int id) {
        return (ItemStack) this.getWatchedObject(id).getWatchedObject();
    }

    public <T> void updateObject(int id, T newData) {
        final WatchableObject object = this.getWatchedObject(id);

        if (object == null) {
            return;
        }

        if (ObjectUtils.notEqual(newData, object.getWatchedObject())) {
            object.setWatchedObject(newData);
            object.setWatched(true);
        }
    }
}
