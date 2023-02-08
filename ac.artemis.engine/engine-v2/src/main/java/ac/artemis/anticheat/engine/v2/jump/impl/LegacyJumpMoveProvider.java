package ac.artemis.anticheat.engine.v2.jump.impl;

import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.anticheat.engine.v2.jump.JumpMoveProvider;
import ac.artemis.core.v4.emulator.potion.PotionEffect;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.attributes.EntityAttributes;
import ac.artemis.core.v5.emulator.tags.Tags;
import ac.artemis.core.v5.utils.minecraft.MathHelper;
import ac.artemis.packet.minecraft.PotionEffectType;

public class LegacyJumpMoveProvider implements JumpMoveProvider {
    @Override
    public TransitionData provide(final TransitionData run) {
        if (run.isInWater() || run.isInLava()) {
            run.setMotionY(run.getMotionY() + 0.03999999910593033D);
        } else  {
            run.setMotionY((double) (0.42F));
            run.addTag(Tags.JUMP);

            final ArtemisData data = (ArtemisData) run.getData().getEntity();

            if (data.getActivePotionsMap().containsKey(PotionEffectType.JUMP.getId())) {
                final double motionY = run.getMotionY() + ((float) (getJumpBoostAmplifier(data))) * 0.1F;
                run.setMotionY(motionY);
                run.addTag(Tags.POTION);
            }

            if (run.isSprintingAttribute()) {
                run.addTag(Tags.JUMP_SPRINT);
                final float f = (float) run.getAttributeMap().poll(EntityAttributes.YAW) * 0.017453292F;

                run.setMotionX(run.getMotionX() - (double) (MathHelper.sin(f) * 0.2F));
                run.setMotionZ(run.getMotionZ() + (double) (MathHelper.cos(f) * 0.2F));
            }
        }

        return run;
    }

    public int getJumpBoostAmplifier(final ArtemisData data) {
        final PotionEffect effect = data.getActivePotionsMap()
                .getOrDefault(PotionEffectType.JUMP.getId(), null);

        return effect != null ? effect.getAmplifier() + 1 : 0;
    }
}
