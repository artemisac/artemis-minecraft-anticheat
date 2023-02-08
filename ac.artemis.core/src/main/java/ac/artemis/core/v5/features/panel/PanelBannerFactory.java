package ac.artemis.core.v5.features.panel;

import ac.artemis.core.v5.features.FeatureFactory;
import ac.artemis.core.v5.features.panel.impl.PanelBanner_V1;

import java.io.File;

public class PanelBannerFactory implements FeatureFactory<PanelBanner> {
    private File dump;

    public PanelBannerFactory setDumpDirectory(File dump) {
        this.dump = dump;
        return this;
    }

    public PanelBanner build() {
        assert dump != null : "You must specify the dump directory for the banner!";

        return new PanelBanner_V1(dump);
    }
}
