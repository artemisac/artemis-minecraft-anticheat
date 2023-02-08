package ac.artemis.core.v4.emulator.damage;

import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.LivingEntity;
import ac.artemis.packet.minecraft.entity.impl.Player;

public class EntityDamageSource extends DamageSource
{
    protected Entity damageSourceEntity;

    /**
     * Whether this EntityDamageSource is from an entity wearing Thorns-enchanted armor.
     */
    private boolean isThornsDamage = false;

    public EntityDamageSource(String p_i1567_1_, Entity damageSourceEntityIn)
    {
        super(p_i1567_1_);
        this.damageSourceEntity = damageSourceEntityIn;
    }

    /**
     * Sets this EntityDamageSource as originating from Thorns armor
     */
    public EntityDamageSource setIsThornsDamage()
    {
        this.isThornsDamage = true;
        return this;
    }

    public boolean getIsThornsDamage()
    {
        return this.isThornsDamage;
    }

    public Entity getEntity()
    {
        return this.damageSourceEntity;
    }


    /**
     * Return whether this damage source will have its damage amount scaled based on the current difficulty.
     */
    public boolean isDifficultyScaled()
    {
        return this.damageSourceEntity != null && this.damageSourceEntity instanceof LivingEntity && !(this.damageSourceEntity instanceof Player);
    }
}
