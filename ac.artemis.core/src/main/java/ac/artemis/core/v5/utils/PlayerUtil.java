package ac.artemis.core.v5.utils;

import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.potion.Effect;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlayerUtil {

    public int getPotionLevel(final Player player, final PotionEffectType effect) {
        final int effectId = effect.getId();

        if (!player.hasEffect(effect)) return 0;

        return player.getActivePotionEffects()
                .stream()
                .filter(potionEffect -> potionEffect.getType().getId() == effectId)
                .map(Effect::getAmplifier)
                .findAny()
                .orElse(0) + 1;
    }
}
