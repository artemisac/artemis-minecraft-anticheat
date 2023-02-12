package ac.artemis.core.v5.emulator.particle.type;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.datawatcher.serializer.AbstractDataSerializer;
import ac.artemis.core.v5.emulator.particle.Particle;
import ac.artemis.core.v5.emulator.particle.serializer.AbstractParticleConverter;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;

@Getter
public enum Particles {
    AMBIENT_ENTITY_EFFECT(ParticleType.BASIC),
    ANGRY_VILLAGER(ParticleType.BASIC),
    BARRIER(ParticleType.BASIC),
    BLOCK(ParticleType.BLOCK),
    BUBBLE(ParticleType.BASIC),
    CLOUD(ParticleType.BASIC),
    CRIT(ParticleType.BASIC),
    DAMAGE_INDICATOR(ParticleType.BASIC),
    DRAGON_BREATH(ParticleType.BASIC),
    DRIPPING_LAVA(ParticleType.BASIC),
    FALLING_LAVA(ParticleType.BASIC),
    LANDING_LAVA(ParticleType.BASIC),
    DRIPPING_WATER(ParticleType.BASIC),
    FALLING_WATER(ParticleType.BASIC),
    DUST(ParticleType.DUST),
    EFFECT(ParticleType.BASIC),
    ELDER_GUARDIAN(ParticleType.BASIC),
    ENCHANTED_HIT(ParticleType.BASIC),
    ENCHANT(ParticleType.BASIC),
    END_ROD(ParticleType.BASIC),
    ENTITY_EFFECT(ParticleType.BASIC),
    EXPLOSION_EMITTER(ParticleType.BASIC),
    EXPLOSION(ParticleType.BASIC),
    FALLING_DUST(ParticleType.BLOCK),
    FIREWORK(ParticleType.BASIC),
    FISHING(ParticleType.BASIC),
    FLAME(ParticleType.BASIC),
    SOUL_FIRE_FLAME(ParticleType.BASIC),
    SOUL(ParticleType.BASIC),
    FLASH(ParticleType.BASIC),
    HAPPY_VILLAGER(ParticleType.BASIC),
    COMPOSTER(ParticleType.BASIC),
    HEART(ParticleType.BASIC),
    INSTANT_EFFECT(ParticleType.BASIC),
    ITEM(ParticleType.ITEM),
    ITEM_SLIME(ParticleType.BASIC),
    ITEM_SNOWBALL(ParticleType.BASIC),
    LARGE_SMOKE(ParticleType.BASIC),
    LAVA(ParticleType.BASIC),
    MYCELIUM(ParticleType.BASIC),
    NOTE(ParticleType.BASIC),
    POOF(ParticleType.BASIC),
    PORTAL(ParticleType.BASIC),
    RAIN(ParticleType.BASIC),
    SMOKE(ParticleType.BASIC),
    SNEEZE(ParticleType.BASIC),
    SPIT(ParticleType.BASIC),
    SQUID_INK(ParticleType.BASIC),
    SWEEP_ATTACK(ParticleType.BASIC),
    TOTEM_OF_UNDYING(ParticleType.BASIC),
    UNDERWATER(ParticleType.BASIC),
    SPLASH(ParticleType.BASIC),
    WITCH(ParticleType.BASIC),
    BUBBLE_POP(ParticleType.BASIC),
    CURRENT_DOWN(ParticleType.BASIC),
    BUBBLE_COLUMN_UP(ParticleType.BASIC),
    NAUTILUS(ParticleType.BASIC),
    DOLPHIN(ParticleType.BASIC),
    CAMPFIRE_COSY_SMOKE(ParticleType.BASIC),
    CAMPFIRE_SIGNAL_SMOKE(ParticleType.BASIC),
    DRIPPING_HONEY(ParticleType.BASIC),
    FALLING_HONEY(ParticleType.BASIC),
    LANDING_HONEY(ParticleType.BASIC),
    FALLING_NECTAR(ParticleType.BASIC),
    ASH(ParticleType.BASIC),
    CRIMSON_SPORE(ParticleType.BASIC),
    WARPED_SPORE(ParticleType.BASIC),
    DRIPPING_OBSIDIAN_TEAR(ParticleType.BASIC),
    FALLING_OBSIDIAN_TEAR(ParticleType.BASIC),
    LANDING_OBSIDIAN_TEAR(ParticleType.BASIC),
    REVERSE_PORTAL(ParticleType.BASIC),
    WHITE_ASH(ParticleType.BASIC);

    private final ParticleType type;

    Particles(ParticleType type) {
        this.type = type;
    }

    public AbstractParticleConverter getSerializer(PlayerData data) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return type.getSerializer(data);
    }
}
