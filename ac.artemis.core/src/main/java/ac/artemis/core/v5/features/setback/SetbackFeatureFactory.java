package ac.artemis.core.v5.features.setback;

import ac.artemis.core.v5.features.FeatureFactory;
import ac.artemis.core.v5.features.setback.impl.SmartSetbackFeature;

public class SetbackFeatureFactory implements FeatureFactory<SetbackFeature> {
    private SetbackType type;

    public SetbackFeatureFactory setType(final SetbackType type) {
        this.type = type;
        return this;
    }

    @Override
    public SetbackFeature build() {
        switch (type) {
            default: return new SmartSetbackFeature();
        }
    }
}
