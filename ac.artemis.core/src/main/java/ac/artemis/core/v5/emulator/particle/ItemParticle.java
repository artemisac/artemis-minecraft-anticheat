package ac.artemis.core.v5.emulator.particle;

import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.core.v5.emulator.particle.type.Particles;
import lombok.Getter;

@Getter
public class ItemParticle extends Particle {
    private final ItemStack itemStack;

    public ItemParticle(Particles particle, ItemStack itemStack) {
        super(particle);
        this.itemStack = itemStack;
    }
}
