package ac.artemis.core.v5.features.safety;

import ac.artemis.core.v5.features.FeatureFactory;
import ac.artemis.core.v5.features.safety.impl.SmartSafetyFeature;

public class SafetyFeatureFactory implements FeatureFactory<SafetyFeature> {
    @Override
    public SafetyFeature build() {
        return new SmartSafetyFeature();
    }
}
