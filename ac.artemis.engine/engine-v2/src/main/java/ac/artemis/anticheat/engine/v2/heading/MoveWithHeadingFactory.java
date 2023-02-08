package ac.artemis.anticheat.engine.v2.heading;

import ac.artemis.anticheat.engine.v2.heading.impl.LegacyMoveWithHeadingProvider;
import ac.artemis.anticheat.engine.v2.heading.impl.ModernMoveWithHeadingProvider;
import ac.artemis.core.v5.utils.interf.Factory;
import ac.artemis.packet.protocol.ProtocolVersion;

public class MoveWithHeadingFactory implements Factory<MoveWithHeadingProvider> {
    private ProtocolVersion version;

    public MoveWithHeadingFactory setVersion(ProtocolVersion version) {
        this.version = version;
        return this;
    }

    @Override
    public MoveWithHeadingProvider build() {
        assert version != null : "Version must not be null!";
        return version.isOrAbove(ProtocolVersion.V1_14)
                ? new ModernMoveWithHeadingProvider(version)
                : new LegacyMoveWithHeadingProvider(version);
    }
}
