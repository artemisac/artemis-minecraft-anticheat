package ac.artemis.anticheat.engine.v2.liquid;

import ac.artemis.anticheat.engine.v2.liquid.impl.LegacyLiquidCollisionProvider;
import ac.artemis.core.v5.utils.interf.Factory;

public class LiquidCollisionFactory implements Factory<LiquidCollisionProvider> {
    @Override
    public LiquidCollisionProvider build() {
        return new LegacyLiquidCollisionProvider();
    }
}
