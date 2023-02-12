package ac.artemis.core.v5.sdk;

import ac.artemis.core.v5.sdk.flush.FlushSdkFeature;

import java.util.Set;

public interface SDKHandler {
    void init() throws IllegalAccessException;

    void disinit() throws IllegalAccessException;

}
