package ac.artemis.checks.regular.v2.checks.impl.autoclicker;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.settings.CheckSetting;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;

/**
 * @author Ghast
 * @since 21-Mar-20
 */

@Check(type = Type.AUTOCLICKER, var = "A", threshold = 20)
public class AutoClickerA extends ArtemisCheck implements PacketHandler {

    @Setting(type = CheckSettings.MAX_CLICK_EXPERIMENTAL, defaultValue = "200")
    private final CheckSetting maxExperimental = info.getSetting(CheckSettings.MAX_CLICK_EXPERIMENTAL);

    @Setting(type = CheckSettings.MAX_CLICK_ABNORMAL, defaultValue = "220")
    private final CheckSetting maxAbnormal = info.getSetting(CheckSettings.MAX_CLICK_ABNORMAL);

    @Setting(type = CheckSettings.MAX_CLICK_IMPOSSIBLE, defaultValue = "400")
    private final CheckSetting maxImpossible = info.getSetting(CheckSettings.MAX_CLICK_ABNORMAL);

    @Setting(type = CheckSettings.MILLIS_BETWEEN_CLICK_FOR_RESET_FLYING, defaultValue = "150")
    private final CheckSetting maxSeconds = info.getSetting(CheckSettings.MILLIS_BETWEEN_CLICK_FOR_RESET_FLYING);

    @Setting(type = CheckSettings.MILLIS_BETWEEN_CLICK_FOR_RESET_ARM, defaultValue = "250")
    private final CheckSetting maxSecondsArm = info.getSetting(CheckSettings.MILLIS_BETWEEN_CLICK_FOR_RESET_ARM);

    private int ticks;
    private int clicks;
    private int oldClicks;
    private int flyTicks, armTicks;

    private long lastClick;

    private double lastSwingRate;

    public AutoClickerA(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (isNull(CheckType.POSITION)) return;

        if (packet instanceof GPacketPlayClientArmAnimation) {
            if (invalid()) return;

            final boolean timeCondition = (System.currentTimeMillis() - lastClick) > maxSecondsArm.getAsInt();

            // Update values
            if (timeCondition) this.clicks = oldClicks;
            this.ticks = 0;
            this.lastClick = System.currentTimeMillis();
            this.armTicks++;

            if (flyTicks > 10) {
                this.lastSwingRate = (float) flyTicks / (float) armTicks;
            }

            if (armTicks > 100) {
                this.armTicks = this.flyTicks = 0;
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            if (invalid()) return;

            final boolean timeCondition = System.currentTimeMillis() - lastClick > maxSeconds.getAsInt();

            if (timeCondition) return;
            final boolean hasMoved = (data.movement.getLocation().distanceXZ(data.movement.getLastLocation()) > 0);

            this.flyTicks++;

            if (++ticks <= 2) {
                final boolean flag = ++clicks > 100 && lastSwingRate > 1.0;

                if (!flag) return;

                if (clicks > maxExperimental.getAsInt()) {
                    log(1, "Experimental | swingRate=" + lastSwingRate);
                }

                if (clicks > maxAbnormal.getAsInt()) {
                    log(1, "Abnormal");
                }

                if (clicks > maxImpossible.getAsInt()) {
                    log(15, "Impossible");
                }

            } else if (ticks < 5) {
                this.oldClicks = clicks;
                this.clicks = 0;
            }

            debug("ticks=" + ticks + " clicks=" + clicks + " hasMoved=" + hasMoved);
        }
    }

    private boolean invalid() {
        return data.user.isDigging() || data.user.isPlaced() || !TimeUtil.elapsed(data.user.getLastDig(), maxSeconds.getAsInt());
    }

    private void reset() {
        this.oldClicks = clicks;
        this.clicks = 0;
    }
}
