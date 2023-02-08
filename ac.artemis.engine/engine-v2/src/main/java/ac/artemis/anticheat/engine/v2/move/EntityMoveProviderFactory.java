package ac.artemis.anticheat.engine.v2.move;

import ac.artemis.anticheat.engine.v2.move.impl.LegacyEntityMoveProvider;
import ac.artemis.anticheat.engine.v2.move.impl.ModernEntityMoveProvider;
import ac.artemis.core.v5.utils.interf.Factory;
import ac.artemis.packet.protocol.ProtocolVersion;

public class EntityMoveProviderFactory implements Factory<EntityMoveProvider> {
    private ProtocolVersion version;

    public EntityMoveProviderFactory setVersion(ProtocolVersion version) {
        this.version = version;
        return this;
    }

    @Override
    public EntityMoveProvider build() {
        assert version != null : "Version must not be null!";
        return version.isOrAbove(ProtocolVersion.V1_14)
                ? new ModernEntityMoveProvider()
                : new LegacyEntityMoveProvider();
    }
}
