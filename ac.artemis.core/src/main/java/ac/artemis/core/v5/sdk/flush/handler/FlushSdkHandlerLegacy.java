package ac.artemis.core.v5.sdk.flush.handler;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.core.Artemis;
import ac.artemis.core.v5.sdk.flush.FlushSdkFeature;
import ac.artemis.core.v5.sdk.flush.FlushSdkHandler;
import ac.artemis.core.v5.sdk.flush.component.legacy.FlushSdkTicker;
import ac.artemis.core.v5.sdk.flush.impl.StandardTickingFlushFeature;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Deprecated
public class FlushSdkHandlerLegacy implements FlushSdkHandler {
    @Getter
    private Set<FlushSdkFeature> features;
    private FlushSdkTicker ticker;
    @Override
    public void init() {
        this.ticker = new FlushSdkTicker(this);
        this.features = new HashSet<>(Collections.singleton(
                new StandardTickingFlushFeature()
        ));
        Server.v().getScheduler().runTaskTimerAsynchronously(ticker, 0L, 1L);
    }

    @Override
    public void disinit() {
        this.features.clear();
        this.features = null;
        this.ticker = null;
    }
}
