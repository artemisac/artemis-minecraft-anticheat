package ac.artemis.core.v5.sdk.flush;

import ac.artemis.core.v4.data.PlayerData;

import java.util.UUID;

public interface FlushSdkFeature {
    void onPreFlush(final PlayerData data);

    void onPostFlush(final PlayerData data);
}
