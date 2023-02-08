package ac.artemis.core.v5.features.logs;

import ac.artemis.core.v5.features.FeatureFactory;
import ac.artemis.core.v5.features.logs.impl.PanelLogsFetcher_V1;

public class PanelLogsFetcherFactory implements FeatureFactory<PanelLogsFetcher> {
    @Override
    public PanelLogsFetcher build() {
        return new PanelLogsFetcher_V1();
    }
}
