package ac.artemis.core.v4.emulator.potion;

import ac.artemis.core.v4.emulator.attribute.map.BaseAttributeMap;
import ac.artemis.core.v5.emulator.Emulator;

public class PotionAbsorption extends Potion
{
    protected PotionAbsorption(int potionID, boolean badEffect, int potionColor)
    {
        super(potionID, badEffect, potionColor);
    }

    public void removeAttributesModifiersFromEntity(Emulator entityLivingBaseIn, BaseAttributeMap p_111187_2_, int amplifier)
    {
        //entityLivingBaseIn.setAbsorptionAmount(entityLivingBaseIn.getAbsorptionAmount() - (float)(4 * (amplifier + 1)));
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, p_111187_2_, amplifier);
    }

    public void applyAttributesModifiersToEntity(Emulator entityLivingBaseIn, BaseAttributeMap p_111185_2_, int amplifier)
    {
        //entityLivingBaseIn.setAbsorptionAmount(entityLivingBaseIn.getAbsorptionAmount() + (float)(4 * (amplifier + 1)));
        super.applyAttributesModifiersToEntity(entityLivingBaseIn, p_111185_2_, amplifier);
    }
}
