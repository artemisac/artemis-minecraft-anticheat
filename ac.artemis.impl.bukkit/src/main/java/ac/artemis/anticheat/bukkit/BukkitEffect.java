package ac.artemis.anticheat.bukkit;

import ac.artemis.packet.minecraft.AbstractWrapper;
import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.packet.minecraft.potion.Effect;
import org.bukkit.potion.PotionEffect;

public class BukkitEffect extends AbstractWrapper<PotionEffect> implements Effect {
    public BukkitEffect(PotionEffect wrapper) {
        super(wrapper);
    }

    @Override
    public PotionEffectType getType() {
        return PotionEffectType.getFromId(wrapper.getType().getId());
    }

    @Override
    public int getAmplifier() {
        return wrapper.getAmplifier();
    }

    @Override
    public int getDuration() {
        return wrapper.getDuration();
    }
}
