package ac.artemis.core.v5.sdk.flush.component.injected;

import ac.artemis.anticheat.sdk.FlushListener;
import ac.artemis.anticheat.sdk.FlushManager;
import ac.artemis.core.v5.sdk.flush.AbstractFlushComponent;
import ac.artemis.core.v5.sdk.flush.FlushSdkHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlushSdkManager extends AbstractFlushComponent implements FlushManager {
    private final List<FlushListener> listeners = new ArrayList<>();

    public FlushSdkManager(FlushSdkHandler handler) {
        super(handler);
    }

    @Override
    public void addListener(FlushListener flushListener) {
        listeners.add(flushListener);
    }

    @Override
    public void removeListener(FlushListener flushListener) {
        listeners.remove(flushListener);
    }

    @Override
    public void clearListeners() {
        listeners.clear();
    }

    @Override
    public void callPre(UUID uuid) {
        for (FlushListener listener : listeners) {
            listener.onPreFlush(uuid);
        }
    }

    @Override
    public void callPost(UUID uuid) {
        for (FlushListener listener : listeners) {
            listener.onPostFlush(uuid);
        }
    }
}
