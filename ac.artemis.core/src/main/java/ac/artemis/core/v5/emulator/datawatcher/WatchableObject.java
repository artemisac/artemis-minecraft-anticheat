package ac.artemis.core.v5.emulator.datawatcher;

import lombok.Data;

@Data
public class WatchableObject {
    private final int objectType;
    private final int dataValueId;
    private Object watchedObject;
    private boolean watched;

    public WatchableObject(int type, int id, Object object) {
        this.dataValueId = id;
        this.watchedObject = object;
        this.objectType = type;
        this.watched = true;
    }
}