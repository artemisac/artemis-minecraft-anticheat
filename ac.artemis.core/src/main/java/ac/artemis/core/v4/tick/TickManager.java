package ac.artemis.core.v4.tick;

import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v5.sdk.flush.FlushSdkFactory;
import ac.artemis.core.v5.sdk.flush.FlushSdkHandler;
import ac.artemis.core.v5.sdk.flush.handler.FlushSdkHandlerInjected;
import lombok.Getter;

@Getter
public class TickManager extends Manager  {
    private FlushSdkHandler flushSdkHandler;
    private boolean sdk;

    public TickManager(final Artemis plugin) {
        super(plugin, "TickManager");
    }

    @Override
    public void init(InitializeAction initializeAction) {
        assert flushSdkHandler == null : "TickManager has already started";

        flushSdkHandler = new FlushSdkFactory().build();
        flushSdkHandler.init();

        this.sdk = flushSdkHandler instanceof FlushSdkHandlerInjected;
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {
        assert flushSdkHandler != null : "TickManger has already stopped running";

        flushSdkHandler.disinit();
        flushSdkHandler = null;
    }
}
