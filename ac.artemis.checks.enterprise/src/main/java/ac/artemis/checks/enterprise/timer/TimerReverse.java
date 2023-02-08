package ac.artemis.checks.enterprise.timer;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MovingStats;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;

/**
 * @author Ghast
 * @since 21-Mar-20
 */

@Check(type = Type.TIMER, var = "Reverse", threshold = 10)
@Experimental
public class TimerReverse extends ArtemisCheck implements PacketHandler, PacketExcludable {
    public TimerReverse(final PlayerData data, final CheckInformation info) {
        super(data, info);
        this.setCompatiblePackets(
                PacketPlayClientFlying.class,
                GPacketPlayClientLook.class,
                GPacketPlayClientPosition.class,
                GPacketPlayClientPositionLook.class
        );
    }
    private int buffer;
    private long lastFlying;
    private final MovingStats ejector = new MovingStats(50);
    private int maxBuffer = 8;
    private double maxV, minV;

    @Override
    public void handle(final GPacket packet) {
        if (isNullLocation()) return;

        final boolean invalid = this.isExempt(ExemptType.TELEPORT, ExemptType.JOIN,
                ExemptType.TPS, ExemptType.VOID, ExemptType.NFPGAY);

        if (invalid) {
            buffer = 0;
            debug("null");
            return;
        }

        final long now = packet.getTimestamp();

        this.ejector.add(now - lastFlying);

        final double min = 7.07;
        final double stdDev = ejector.getStdDevReverse(min);

        if (Double.isNaN(stdDev))
            this.maxBuffer = Math.min(60, maxBuffer + 1);
        else {
            // Handle max/min
            if (maxV < stdDev) maxV = stdDev;
            else if (minV > stdDev) minV = stdDev;

            // Buffer
            this.maxBuffer = Math.min(8, maxBuffer - 1);
        }

        final double diff = Math.abs(maxV - minV);

        if (!Double.isNaN(stdDev) && stdDev > min && diff < 0.3) {
            if (++buffer > 8) {
                log("std=" + stdDev + " max=" + min + " buffer=" + buffer);
            }
        } else {
            buffer = 0;
        }
        debug("std=" + stdDev + " max=" + min + " buffer=" + buffer);

        this.lastFlying = now;
    }
}
