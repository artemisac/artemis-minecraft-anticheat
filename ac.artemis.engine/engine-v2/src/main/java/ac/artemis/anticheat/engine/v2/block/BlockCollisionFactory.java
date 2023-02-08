package ac.artemis.anticheat.engine.v2.block;

import ac.artemis.anticheat.engine.v2.block.impl.LegacyBlockCollisionProvider;
import ac.artemis.core.v5.utils.interf.Factory;

public class BlockCollisionFactory implements Factory<BlockCollisionProvider> {
    @Override
    public BlockCollisionProvider build() {
        return new LegacyBlockCollisionProvider();
    }
}
