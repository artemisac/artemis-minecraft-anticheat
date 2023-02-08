package ac.artemis.anticheat.engine.v2.flying;

import ac.artemis.anticheat.engine.v2.flying.impl.LegacyEntityFlyingProvider;
import ac.artemis.core.v5.utils.interf.Factory;

public class EntityFlyingFactory implements Factory<EntityFlyingProvider> {
    @Override
    public EntityFlyingProvider build() {
        return new LegacyEntityFlyingProvider();
    }
}
