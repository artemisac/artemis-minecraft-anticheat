package ac.artemis.checks.regular.v2.checks.impl.autoclicker.badpacket;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockDig;

/**
 * @author Elevated
 * @since 18-Apr-20
 */

@Check(type = Type.AUTOCLICKER, var = "X1")
public class AutoClicker1 extends ArtemisCheck implements PacketHandler {
    public AutoClicker1(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private boolean dug = false;
    private long lastTime;
    private int buffer;

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientBlockDig) {
            GPacketPlayClientBlockDig dig = (GPacketPlayClientBlockDig) packet;
            if (dig.getType() == null) return;

            // If we start digging
            if (dig.getType().equals(PlayerEnums.DigType.STOP_DESTROY_BLOCK)) {
                this.dug = true;
            }

            else if (dig.getType().equals(PlayerEnums.DigType.ABORT_DESTROY_BLOCK)) {
                if (dug) {
                    if (++buffer > 9) {
                        log("buffer=" + buffer);
                    }
                } else {
                    buffer = Math.max(buffer - 3, 0);
                }
            }
        } else if (packet instanceof GPacketPlayClientArmAnimation) {
            final long now = System.currentTimeMillis();

            // Probably a double click
            if (now - this.lastTime < 10L) {
                this.buffer = 0;
            }

            this.lastTime = now;
            this.dug = false;
        }
    }
}
