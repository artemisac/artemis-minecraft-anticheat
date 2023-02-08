package ac.artemis.checks.regular.v2.checks.impl.fastladder;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.settings.CheckSetting;
import ac.artemis.core.v4.check.templates.position.ComplexPositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PlayerPosition;

/**
 * @author Ghast
 * @since 07-May-20
 */

@Check(type = Type.FASTLADDER, var = "Simple")
public class FastLadderSimple extends ComplexPositionCheck {


    @Setting(type = CheckSettings.MAX_DELTA, defaultValue = "0.1177")
    private final CheckSetting maxDelta = info.getSetting(CheckSettings.MAX_DELTA);

    @Setting(type = CheckSettings.MAX_STREAK_FOR_VL, defaultValue = "1")
    private final CheckSetting streaksNeeded = info.getSetting(CheckSettings.MAX_STREAK_FOR_VL);

    private int streaks;

    private double lastDeltaY;

    public FastLadderSimple(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(final PlayerPosition from, final PlayerPosition to) {
        final double deltaY = to.getY() - from.getY();

        final boolean flag = deltaY > maxDelta.getAsDouble() && deltaY == this.lastDeltaY && data.entity.isOnLadder();

        final boolean exempt = this.isExempt(
                ExemptType.VOID,
                ExemptType.VELOCITY,
                ExemptType.TELEPORT,
                ExemptType.FLIGHT,
                ExemptType.LIQUID
        );

        if (flag && !exempt) {
            if (++this.streaks > this.streaksNeeded.getAsInt()) {
                this.log();
            }
        } else {
            this.streaks = 0;
        }

        this.lastDeltaY = deltaY;
    }
}
