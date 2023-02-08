package ac.artemis.core.v5.sdk.flush.component.injected;

import ac.artemis.anticheat.sdk.FlushListener;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.sdk.flush.AbstractFlushComponent;
import ac.artemis.core.v5.sdk.flush.FlushSdkFeature;
import ac.artemis.core.v5.sdk.flush.FlushSdkHandler;

import java.util.UUID;

public class FlushSdkListener extends AbstractFlushComponent implements FlushListener {
    public FlushSdkListener(FlushSdkHandler handler) {
        super(handler);
    }

    /**
     * Minecraft servers work on a queued flushing basis in order to limit the quantity of synchronized and thread
     * locked packets from overloading the netty pipeline due to the constant flushing. Flushing can quickly become a
     * performance impacting process, hence it is favorable to keep it limited; Furthermore, flushing benefits better
     * server-client synchronization and ensures that *most the time* packets fall under the same tick. This of course
     * can differ from time to time.
     * <p>
     * This specific method should be called *before* the flushing occurs
     *
     * @param uuid UUID of the player who's pipeline was flushed
     */
    @Override
    public void onPreFlush(UUID uuid) {
        final PlayerData data = this.getDataManager().getData(uuid);

        if (data == null)
            return;

        for (FlushSdkFeature feature : handler.getFeatures()) {
            feature.onPreFlush(data);
        }
    }

    /**
     * Minecraft servers work on a queued flushing basis in order to limit the quantity of synchronized and thread
     * locked packets from overloading the netty pipeline due to the constant flushing. Flushing can quickly become a
     * performance impacting process, hence it is favorable to keep it limited; Furthermore, flushing benefits better
     * server-client synchronization and ensures that *most the time* packets fall under the same tick. This of course
     * can differ from time to time.
     * <p>
     * This specific method should be called *after* the flushing occurs
     *
     * @param uuid UUID of the player who's pipeline was flushed
     */
    @Override
    public void onPostFlush(UUID uuid) {
        final PlayerData data = this.getDataManager().getData(uuid);

        if (data == null)
            return;

        for (FlushSdkFeature feature : handler.getFeatures()) {
            feature.onPostFlush(data);
        }
    }
}
