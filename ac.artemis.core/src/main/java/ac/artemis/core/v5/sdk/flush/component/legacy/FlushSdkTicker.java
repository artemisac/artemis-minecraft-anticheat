package ac.artemis.core.v5.sdk.flush.component.legacy;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.packet.PacketExecutor;
import ac.artemis.core.v4.utils.graphing.Pair;
import ac.artemis.core.v5.sdk.flush.AbstractFlushComponent;
import ac.artemis.core.v5.sdk.flush.FlushSdkFeature;
import ac.artemis.core.v5.sdk.flush.FlushSdkHandler;
import ac.artemis.core.v5.threading.Threading;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Deprecated
public class FlushSdkTicker extends AbstractFlushComponent implements Runnable {
    private final ExecutorService executorService = Threading.getOrStartService("artemis-flush-ticker");

    public FlushSdkTicker(FlushSdkHandler handler) {
        super(handler);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        for (Pair<PlayerData, PacketExecutor> value : this.getDataManager().getPlayerDataMap().values()) {
            for (FlushSdkFeature feature : handler.getFeatures()) {
                feature.onPreFlush(value.getX());
                CompletableFuture.runAsync(() -> {
                    executorService.execute(() -> {
                        feature.onPostFlush(value.getX());
                    });
                });
            }
        }
    }
}
