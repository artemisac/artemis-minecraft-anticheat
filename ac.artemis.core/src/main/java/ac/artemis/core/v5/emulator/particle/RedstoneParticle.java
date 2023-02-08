package ac.artemis.core.v5.emulator.particle;

import ac.artemis.core.v4.emulator.moderna.ModernaMathHelper;
import ac.artemis.core.v5.emulator.particle.type.Particles;
import lombok.Getter;

@Getter
public class RedstoneParticle extends Particle {
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public RedstoneParticle(Particles type, float red, float green, float blue, float alpha) {
        super(type);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = ModernaMathHelper.clamp(alpha, 0.01F, 4.0F);
    }
}
