package ac.artemis.core.v4.emulator.potion;

import ac.artemis.core.v4.emulator.attribute.map.BaseAttributeMap;
import ac.artemis.core.v5.emulator.Emulator;

public class PotionHealthBoost extends Potion
{
    public PotionHealthBoost(int potionID, boolean badEffect, int potionColor)
    {
        super(potionID, badEffect, potionColor);
    }

    public void removeAttributesModifiersFromEntity(Emulator entityLivingBaseIn, BaseAttributeMap p_111187_2_, int amplifier)
    {
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, p_111187_2_, amplifier);

        /*if (entityLivingBaseIn.getHealth() > entityLivingBaseIn.getMaxHealth())
        {
            entityLivingBaseIn.setHealth(entityLivingBaseIn.getMaxHealth());
        }*/
    }
}
