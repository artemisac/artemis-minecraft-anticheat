package ac.artemis.core.v4.emulator.damage;

import ac.artemis.packet.minecraft.entity.Entity;
import lombok.Data;

/**
 * @author Ghast
 * @since 15/08/2020
 * Artemis © 2020
 */

@Data
public class Damage {
    private final Entity entity;
    private final DamageSource damageSource;

    public Damage(Entity entity, DamageSource damageSource) {
        this.entity = entity;
        this.damageSource = damageSource;
    }
}
