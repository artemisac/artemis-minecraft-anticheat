package ac.artemis.core.v5.features.teleport;

import ac.artemis.core.v5.features.FeatureFactory;
import ac.artemis.core.v5.features.teleport.impl.BruteforceTeleportHandler;

public class TeleportFeatureFactory implements FeatureFactory<TeleportHandlerFeature> {
    private TeleportHandlerType type;

    public TeleportFeatureFactory setType(TeleportHandlerType type) {
        this.type = type;
        return this;
    }

    @Override
    public TeleportHandlerFeature build() {
        switch (type) {
            default: return new BruteforceTeleportHandler();
        }
    }
}
