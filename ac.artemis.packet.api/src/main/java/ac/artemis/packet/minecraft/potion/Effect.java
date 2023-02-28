package ac.artemis.packet.minecraft.potion;

import ac.artemis.packet.minecraft.PotionEffectType;

/**
 * The wrapped Effect class from bukkit.
 */
public interface Effect {
    /**
     * Gets Potion effect type.
     *
     * @return the type enum
     */
    PotionEffectType getType();

    /**
     * @return Gets amplifier amount.
     */
    int getAmplifier();

    /**
     * @return Gets the integer duration in ticks
     */
    int getDuration();

}
