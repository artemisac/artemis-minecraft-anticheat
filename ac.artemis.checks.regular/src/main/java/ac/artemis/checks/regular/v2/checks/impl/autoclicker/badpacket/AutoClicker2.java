package ac.artemis.checks.regular.v2.checks.impl.autoclicker.badpacket;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;

/**
 * @author Ghast
 * @since 18-Apr-20
 */

@Check(type = Type.AUTOCLICKER, var = "X2")
public class AutoClicker2 extends ArtemisCheck implements PacketHandler {
    public AutoClicker2(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private int swings, movements, buffer;
    private long lastSwing;

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            ++this.movements;
            if (this.movements == 20) {
                if (this.swings > 20) {
                    if (buffer++ > 1) {
                        log(2, "swings=" + swings + " movs=" + movements);
                    }
                } else {
                    buffer = 0;
                }
            }
            if (System.currentTimeMillis() - this.lastSwing <= 350L) {
                this.movements = 0;
                this.swings = 0;
            }
        } else if (packet instanceof GPacketPlayClientArmAnimation) {
            if (System.currentTimeMillis() - data.movement.getLastDelayedMovePacket() > 110L
                    && System.currentTimeMillis() - data.movement.getLastMovePacket() < 110L
                    && !data.user.isDigging()
                    && !data.user.isPlaced()) {
                ++this.swings;
                this.lastSwing = System.currentTimeMillis();
            }
        }
    }
}
