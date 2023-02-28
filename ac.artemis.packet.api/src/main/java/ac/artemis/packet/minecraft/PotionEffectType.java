package ac.artemis.packet.minecraft;

import java.util.HashMap;
import java.util.Map;

/**
 * The wrapped PotionEffectType enum to circumvent bukkit.
 */
public enum PotionEffectType {
    /**
     * Speed potion effect type.
     */
    SPEED(1),
    /**
     * Slow potion effect type.
     */
    SLOW(2),
    /**
     * Fast digging potion effect type.
     */
    FAST_DIGGING(3),
    /**
     * Slow digging potion effect type.
     */
    SLOW_DIGGING(4),
    /**
     * Increase damage potion effect type.
     */
    INCREASE_DAMAGE(5),
    /**
     * Heal potion effect type.
     */
    HEAL(6),
    /**
     * Harm potion effect type.
     */
    HARM(7),
    /**
     * Jump potion effect type.
     */
    JUMP(8),
    /**
     * Confusion potion effect type.
     */
    CONFUSION(9),
    /**
     * Regeneration potion effect type.
     */
    REGENERATION(10),
    /**
     * Damage resistance potion effect type.
     */
    DAMAGE_RESISTANCE(11),
    /**
     * Fire resistance potion effect type.
     */
    FIRE_RESISTANCE(12),
    /**
     * Water breathing potion effect type.
     */
    WATER_BREATHING(13),
    /**
     * Invisibility potion effect type.
     */
    INVISIBILITY(14),
    /**
     * Blindness potion effect type.
     */
    BLINDNESS(15),
    /**
     * Night vision potion effect type.
     */
    NIGHT_VISION(16),
    /**
     * Hunger potion effect type.
     */
    HUNGER(17),
    /**
     * Weakness potion effect type.
     */
    WEAKNESS(18),
    /**
     * Poison potion effect type.
     */
    POISON(19),
    /**
     * Wither potion effect type.
     */
    WITHER(20),
    /**
     * Health boost potion effect type.
     */
    HEALTH_BOOST(21),
    /**
     * Absorption potion effect type.
     */
    ABSORPTION(22),
    /**
     * Saturation potion effect type.
     */
    SATURATION(23),
    /**
     * Glowing potion effect type.
     */
    GLOWING(24),
    /**
     * Levitation potion effect type.
     */
    LEVITATION(25),
    /**
     * Luck potion effect type.
     */
    LUCK(26),
    /**
     * Unluck potion effect type.
     */
    UNLUCK(27),
    /**
     * Slow falling potion effect type.
     */
    SLOW_FALLING(28),
    /**
     * Conduit power potion effect type.
     */
    CONDUIT_POWER(29),
    /**
     * Dolphins grace potion effect type.
     */
    DOLPHINS_GRACE(30);

    private static final Map<Integer, PotionEffectType> idToEffectMap = new HashMap<>();
    private final int id;

    PotionEffectType(int id) {
        this.id = id;
    }

    /**
     * Gets potion effect id.
     *
     * @return the potion effect id
     */
    public int getId() {
        return id;
    }

    public static PotionEffectType getFromId(final int id) {
        return idToEffectMap.get(id);
    }

    static {
        for (PotionEffectType value : values()) {
            idToEffectMap.put(value.getId(), value);
        }
    }
}
