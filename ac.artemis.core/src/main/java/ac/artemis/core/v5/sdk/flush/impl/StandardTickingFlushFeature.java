package ac.artemis.core.v5.sdk.flush.impl;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.features.tick.TickHandlerFeature;
import ac.artemis.core.v5.sdk.flush.FlushSdkFeature;

public class StandardTickingFlushFeature implements FlushSdkFeature {
    @Override
    public void onPreFlush(PlayerData data) {
        for (TickHandlerFeature<?> value : data.connection.getFeatureMap().values()) {
            final boolean skip = !value.canReceive()
                    || (value.getActions(value.getTick()) == null
                    && value.getActions(value.getNextTick()) == null);
            if (skip)
                continue;
            value.push(false);
        }
    }

    @Override
    public void onPostFlush(PlayerData data) {
        for (TickHandlerFeature<?> value : data.connection.getFeatureMap().values()) {
            final boolean skip = !value.canReceive()
                    || (value.getActions(value.getTick()) == null
                    && value.getActions(value.getNextTick()) == null);
            if (skip)
                continue;
            value.push(true);
        }
    }
}
