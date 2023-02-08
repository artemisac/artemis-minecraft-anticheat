package ac.artemis.checks.regular.v2.checks.impl.autoclicker.badpacket;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.TickHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;
import ac.artemis.core.v4.utils.position.PlayerPosition;

/**
 * @author Ghast
 * @since 18-Apr-20
 */

@Check(type = Type.AUTOCLICKER, var = "X4")
@ClientVersion
public class AutoClicker4 extends ArtemisCheck implements PacketHandler, TickHandler {
    public AutoClicker4(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private double vl;
    private long lastSwing;
    private boolean swung;

    private int tickCount, flyCount;

    @Override
    public void handle(final GPacket packet) {
        final boolean exempt = this.isExempt(
                ExemptType.GAMEMODE
        );

        if (exempt)
            return;

        if (packet instanceof GPacketPlayClientArmAnimation) {
            PlayerPosition lastPosition = data.movement.getLocation();
            if (lastPosition == null) {
                return;
            }
            final long delay = System.currentTimeMillis() - lastPosition.getTimestamp();
            if (delay <= 25.0) {
                this.lastSwing = System.currentTimeMillis();
                this.swung = true;
                return;
            }

            vl = vl > 0 ? vl - 0.25 : 0;
        } else if (packet instanceof PacketPlayClientFlying && swung) {
            final long time = System.currentTimeMillis() - this.lastSwing;

            if (flyCount++ >= 100) {
                flyCount = 0;
            }

            if (time >= 25L && !data.user.isLagging() && Math.abs(tickCount - flyCount) < 4) {
                if (++vl > 6) {
                    log(3, "vl=" + vl + " time=" + time);
                }
                return;
            }

            vl = vl > 0 ? vl - 0.25 : 0;
            this.swung = false;
        }
    }

    @Override
    public void tick() {
        if (tickCount++ >= 100) {
            tickCount = 0;
        }
    }
}
