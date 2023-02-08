package ac.artemis.core.v5.sdk.flush;

import ac.artemis.core.v5.sdk.SDKHandler;

import java.util.Set;

public interface FlushSdkHandler extends SDKHandler {
    Set<FlushSdkFeature> getFeatures();
}
