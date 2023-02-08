package ac.artemis.core.v5.emulator.datawatcher.impl;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.WatchableObject;
import cc.ghast.packet.buffer.ProtocolByteBuf;

import java.util.ArrayList;
import java.util.List;

public class DataWatcher_1_8 extends AbstractDataWatcher {
    public DataWatcher_1_8(PlayerData data) {
        super(data);
    }

    @Override
    public List<WatchableObject> readWatchedListFromPacketBuffer(ProtocolByteBuf buffer) {
        final List<WatchableObject> list = new ArrayList<>();

        for (int i = buffer.readByte(); i != 127; i = buffer.readByte()) {
            int j = (i & 224) >> 5;
            int k = i & 31;

            list.add(new WatchableObject(j, k, this.getSerializer(j).read(buffer)));
        }

        return list;
    }


}
