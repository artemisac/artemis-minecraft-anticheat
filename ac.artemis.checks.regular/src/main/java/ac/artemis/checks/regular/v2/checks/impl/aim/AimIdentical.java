package ac.artemis.checks.regular.v2.checks.impl.aim;

import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.rotation.SimpleRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.SimpleRotation;

/**
 * @author Ghast
 * @since 17-Mar-20
 */

@Check(type = Type.AIM, var = "Identical", threshold = 5)
public class AimIdentical extends SimpleRotationCheck {
    public AimIdentical(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    private float lasYawDelta;
    private float lasPitchDelta;
    private double vl, streak;

    @Override
    public void handleRotation(final SimpleRotation from, final SimpleRotation to) {
        final float yawDelta = Math.abs(to.getYaw() - from.getYaw());
        final float pitchDelta = Math.abs(to.getPitch() - from.getPitch());

        if ((System.currentTimeMillis() - data.combat.getLastAttack() >= 2000)) {
            vl = 0;
            streak = 0;
            debug("[FAILED] LastAttack=" + data.combat.getLastAttack());
            return;
        }

        if (data.movement.getTeleportTicks() > 0 || data.movement.getRespawnTicks() > 0 || data.movement.getStandTicks() > 0) {
            vl = 0;
            debug("[FAILED] tpTicks=" + data.movement.getTeleportTicks() + " rpTicks=" + data.movement.getRespawnTicks() + " stdTicks=" + data.movement.getStandTicks());
            return;
        }
        final float magicVal = pitchDelta * 100 / lasPitchDelta;
        if (magicVal > 60) {
            vl = Math.max(0, vl - 1);
            streak = Math.max(0, streak - 0.125);
        }

        if (yawDelta > 0.0 && pitchDelta > 0.0) {
            final int roundedYaw = Math.round(yawDelta);
            final int previousRoundedYaw = Math.round(lasYawDelta);

            final float yawDeltaChange = Math.abs(yawDelta - lasYawDelta);

            if (roundedYaw == previousRoundedYaw
                    && yawDelta > 0.01
                    && yawDelta > 1.5F
                    && yawDeltaChange > 0.001
                    && pitchDelta > 0.5
                    && pitchDelta <= 20) {
                if (++vl > 1) {
                    ++streak;
                }
                if (streak > 6) {
                    log("YDelta -> " + roundedYaw + " Change -> " + yawDeltaChange);
                }
            } else {
                vl = Math.max(0, vl - 1);
            }
        }
        debug("[COMPLETE] pC=" + pitchDelta + " yD=" + yawDelta + " streak=" + streak);
        lasYawDelta = yawDelta;
        lasPitchDelta = pitchDelta;
    }


}
