package ac.artemis.core.v4.emulator.potion;

import ac.artemis.core.v4.emulator.attribute.AttributeModifier;

public class PotionAttackDamage extends Potion
{
    protected PotionAttackDamage(int potionID, boolean badEffect, int potionColor)
    {
        super(potionID, badEffect, potionColor);
    }

    public double getAttributeModifierAmount(int p_111183_1_, AttributeModifier modifier)
    {
        return this.id == Potion.weakness.id ? (double)(-0.5F * (float)(p_111183_1_ + 1)) : 1.3D * (double)(p_111183_1_ + 1);
    }
}
