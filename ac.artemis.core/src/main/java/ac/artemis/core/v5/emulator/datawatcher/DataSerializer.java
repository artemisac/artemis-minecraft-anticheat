package ac.artemis.core.v5.emulator.datawatcher;

import ac.artemis.core.v4.data.PlayerData;
import cc.ghast.packet.buffer.ProtocolByteBuf;

import java.lang.reflect.InvocationTargetException;

public interface DataSerializer<T> {
    void write(ProtocolByteBuf buf, T value) throws InvocationTargetException, InstantiationException, IllegalAccessException;

    default DataKey<T> createKey(int id) {
        return new DataKey<>(id, this);
    }

    T copyValue(T value);
}
