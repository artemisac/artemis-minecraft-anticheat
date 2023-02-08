package ac.artemis.anticheat.engine.v2.jump;

import ac.artemis.anticheat.engine.v2.jump.impl.LegacyJumpMoveProvider;
import ac.artemis.core.v5.utils.interf.Factory;

public class JumpMoveFactory implements Factory<JumpMoveProvider> {
    @Override
    public JumpMoveProvider build() {
        return new LegacyJumpMoveProvider();
    }
}
