package ac.artemis.core.v4.emulator.potion;


public class PotionHealth extends Potion
{
    public PotionHealth(int potionID, boolean badEffect, int potionColor)
    {
        super(potionID, badEffect, potionColor);
    }

    /**
     * Returns true if the potion has an instant effect instead of a continuous one (eg Harming)
     */
    public boolean isInstant()
    {
        return true;
    }

    /**
     * checks if Potion effect is ready to be applied this tick.
     */
    public boolean isReady(int p_76397_1_, int p_76397_2_)
    {
        return p_76397_1_ >= 1;
    }
}
