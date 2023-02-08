package ac.artemis.core.v5.sdk.flush.handler;

import ac.artemis.anticheat.sdk.FlushAPI;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.language.Lang;
import ac.artemis.core.v5.sdk.flush.FlushSdkFeature;
import ac.artemis.core.v5.sdk.flush.FlushSdkHandler;
import ac.artemis.core.v5.sdk.flush.component.injected.FlushSdkListener;
import ac.artemis.core.v5.sdk.flush.component.injected.FlushSdkManager;
import ac.artemis.core.v5.sdk.flush.impl.StandardTickingFlushFeature;
import lombok.Getter;

import java.util.*;

public class FlushSdkHandlerInjected implements FlushSdkHandler {
    private FlushSdkListener listener;
    private FlushSdkManager manager;

    @Getter
    private Set<FlushSdkFeature> features;

    @Override
    public void init() {
        Chat.sendConsoleMessage("&r[&a✓&r] " + Lang.MSG_CONSOLE_SDK_BOOST);
        listener = new FlushSdkListener(this);
        manager = new FlushSdkManager(this);

        FlushAPI.setApi(manager);
        FlushAPI.getApi().addListener(listener);

        features = new HashSet<>(Collections.singleton(
                new StandardTickingFlushFeature()
        ));
    }

    @Override
    public void disinit() {
        FlushAPI.getApi().clearListeners();
        FlushAPI.setApi(null);

        listener = null;
        manager = null;
    }
}
