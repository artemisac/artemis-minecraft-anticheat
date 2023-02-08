package ac.artemis.core.v5.emulator.datawatcher;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.impl.DataWatcher_1_8;
import ac.artemis.core.v5.emulator.datawatcher.impl.DataWatcher_1_9;
import ac.artemis.core.v5.utils.interf.Factory;

public class DataWatcherFactory implements Factory<DataWatcherReader> {
    private PlayerData data;

    public DataWatcherFactory setData(final PlayerData data) {
        this.data = data;
        return this;
    }

    @Override
    public DataWatcherReader build() {
        switch (data.getVersion()) {
            default: return new DataWatcher_1_8(data);
            case V1_9:
            case V1_9_1:
            case V1_9_2:
            case V1_9_4:
            case V1_10:
            case V1_10_2:
            case V1_11:
            case V1_12:
            case V1_12_1:
            case V1_12_2:
            case V1_13:
            case V1_13_1:
            case V1_13_2:
            case V1_14:
            case V1_14_1:
            case V1_14_2:
            case V1_14_3:
            case V1_14_4:
            case V1_15:
            case V1_15_1:
            case V1_15_2:
            case V1_16_1:
            case V1_16_2:
            case V1_16_3:
            case V1_16_5:
                return new DataWatcher_1_9(data);
        }
    }
}
