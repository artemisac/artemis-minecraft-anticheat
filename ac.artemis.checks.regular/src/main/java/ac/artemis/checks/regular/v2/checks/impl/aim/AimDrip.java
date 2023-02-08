package ac.artemis.checks.regular.v2.checks.impl.aim;

import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Drop;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.settings.CheckSetting;
import ac.artemis.core.v4.check.templates.rotation.SimpleRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.SimpleRotation;

/**
 * @author Ghast
 * @since 17-Mar-20
 */

@Check(type = Type.AIM, var = "Drip", threshold = 3)
@Drop
public class AimDrip extends SimpleRotationCheck {

    public AimDrip(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    private double vl, streak, lastDeltaPitch;

    // SETTINGS
    @Setting(type = CheckSettings.MAX_STREAK)
    private final CheckSetting maxStreak = info.getSetting(CheckSettings.MAX_STREAK);

    @Setting(type = CheckSettings.MAX_STREAK_FOR_VL, defaultValue = "2")
    private final CheckSetting vlForStreak = info.getSetting(CheckSettings.MAX_STREAK_FOR_VL);

    @Override
    public void handleRotation(SimpleRotation from, SimpleRotation to) {
        final float deltaPitch = (float) MathUtil.distanceBetweenAngles(from.getPitch(), to.getPitch());
        final float deltaYaw = (float) MathUtil.distanceBetweenAngles(from.getYaw(), to.getYaw());

        final double pitchAcceleration = Math.abs(lastDeltaPitch - deltaPitch);

        final boolean preemptive = deltaYaw > 1.975f && !data.combat.isCinematic();

        flag: {
            if (!preemptive) break flag;

            final boolean flag = deltaPitch < lastDeltaPitch && deltaPitch < 0.0700001F && deltaPitch > 0.0015f;

            if (flag) {
                if (streak++ > maxStreak.getAsInt()) {
                    vl++;
                } else {
                    vl = Math.max(0, vl - 0.25);
                }
                if (vl > vlForStreak.getAsInt()) {
                    streak = 0;
                    log("%yC=" + deltaYaw + " %pC=" + deltaPitch + " %ascension=" + pitchAcceleration + " %sup=" + (deltaPitch + pitchAcceleration));
                }
            } else {
                streak = Math.max(0, streak - 0.5);
                vl = Math.max(0, vl - 0.25);
            }
        }

        this.lastDeltaPitch = deltaPitch;
    }
}
