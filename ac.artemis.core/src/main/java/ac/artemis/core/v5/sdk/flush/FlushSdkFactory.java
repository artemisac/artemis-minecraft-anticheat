package ac.artemis.core.v5.sdk.flush;

import ac.artemis.core.v5.sdk.flush.handler.FlushSdkHandlerInjected;
import ac.artemis.core.v5.sdk.flush.handler.FlushSdkHandlerLegacy;
import ac.artemis.core.v5.sdk.flush.handler.FlushSdkHandlerStandard;
import ac.artemis.core.v5.utils.ClassUtil;
import ac.artemis.core.v5.utils.interf.Factory;

public class FlushSdkFactory implements Factory<FlushSdkHandler> {
    @Override
    public FlushSdkHandler build() {
        return ClassUtil.isClassExist("ac.artemis.anticheat.sdk.FlushAPI")
                ? new FlushSdkHandlerInjected()
                : new FlushSdkHandlerStandard();
    }
}
