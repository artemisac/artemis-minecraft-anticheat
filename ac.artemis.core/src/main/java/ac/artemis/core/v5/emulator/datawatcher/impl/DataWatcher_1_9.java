package ac.artemis.core.v5.emulator.datawatcher.impl;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.WatchableObject;
import ac.artemis.core.v5.emulator.datawatcher.serializer.*;
import cc.ghast.packet.buffer.ProtocolByteBuf;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class DataWatcher_1_9 extends AbstractDataWatcher {
    public DataWatcher_1_9(PlayerData data) {
        super(data);
    }

    @Override
    public List<WatchableObject> readWatchedListFromPacketBuffer(ProtocolByteBuf buffer) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final List<WatchableObject> list = new ArrayList<>();

        int id;

        while ((id = buffer.readUnsignedByte()) != 255) {
            final int type = buffer.readVarInt();
            final AbstractDataSerializer<?> dataserializer = this.getSerializer(type);
            if (dataserializer == null) {
                continue;
            }

            list.add(new WatchableObject(type, id, dataserializer.read(buffer)));
        }

        return list;
    }

}
