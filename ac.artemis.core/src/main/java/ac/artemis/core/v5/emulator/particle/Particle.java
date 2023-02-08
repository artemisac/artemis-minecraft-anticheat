package ac.artemis.core.v5.emulator.particle;

import ac.artemis.core.v5.emulator.particle.type.Particles;
import lombok.Getter;

@Getter
public class Particle {
    private final Particles type;

    public Particle(Particles type) {
        this.type = type;
    }
}
