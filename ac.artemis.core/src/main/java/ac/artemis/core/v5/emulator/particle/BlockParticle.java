package ac.artemis.core.v5.emulator.particle;

import ac.artemis.core.v5.emulator.particle.type.Particles;
import lombok.Getter;

@Getter
public class BlockParticle extends Particle {
    private final int blockId;

    public BlockParticle(Particles type, int blockId) {
        super(type);
        this.blockId = blockId;
    }
}
