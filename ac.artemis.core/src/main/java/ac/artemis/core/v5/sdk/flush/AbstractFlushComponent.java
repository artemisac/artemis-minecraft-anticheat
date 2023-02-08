package ac.artemis.core.v5.sdk.flush;

import ac.artemis.core.v5.utils.interf.Access;

public abstract class AbstractFlushComponent implements Access {
    protected final FlushSdkHandler handler;

    public AbstractFlushComponent(FlushSdkHandler handler) {
        this.handler = handler;
    }
}
