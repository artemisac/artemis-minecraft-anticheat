package ac.artemis.core.v5.features.panel;

import ac.artemis.core.v5.features.FeatureFactory;
import ac.artemis.core.v5.features.panel.impl.PanelLogger_V1;

import java.io.File;

public class PanelLoggerFactory implements FeatureFactory<PanelLogger> {
    private File dump;

    public PanelLoggerFactory setDumpDirectory(File dump) {
        this.dump = dump;
        return this;
    }

    public PanelLogger build() {
        assert dump != null : "You must specify the dump directory for the logger!";

        return new PanelLogger_V1(dump);
    }
}
