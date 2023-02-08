package ac.artemis.checks.regular.v2.checks.impl.badpackets;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MovingStats;
import ac.artemis.packet.minecraft.GameMode;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;

@Check(type = Type.BADPACKETS, var = "N", threshold = 1)
public class BadPacketsN extends ArtemisCheck implements PacketHandler {

    private final MovingStats movingStats = new MovingStats(20);
    private double vl, streak, balance;
    private long lastFlying, lastTime;

    public BadPacketsN(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        final Player player = data.getPlayer();
        if (packet instanceof PacketPlayClientFlying) {
            if (!data.user.isLagging() && data.movement.getRespawnTicks() == 0 && data.movement.getDeathTicks() == 0
                    && (System.currentTimeMillis() - data.user.getLongInTimePassed()) > 5000
                    && data.movement.getLocation() != null
                    && data.user.getPing() > 0
                    && !data.movement.isInLiquid()
                    && !player.isFlying()
                    && player.getGameMode() == GameMode.SURVIVAL) {
                final long now = System.currentTimeMillis();

                final long time = System.currentTimeMillis();
                final long lastTime = this.lastTime != 0 ? this.lastTime : time - 50;
                this.lastTime = time;

                if (data.movement.isStuck()) {
                    debug("Data is stuck");
                    return;
                }

                final long rate = time - lastTime;

                balance += 50.0;
                balance -= rate;

                movingStats.add(now - lastFlying);

                final double max = 7.07;
                final double stdDev = movingStats.getStdDev(max);

                // To fix some exploits to bypass this check.
                if (balance <= -500) {
                    balance = -500;
                } else if (balance >= 200) {
                    balance = 200;
                }

                if (!Double.isNaN(stdDev) && stdDev < max && balance > 10) {
                    if (++vl > 1) {
                        ++streak;
                    }
                    if (streak > 15) {
                        log("streak=" + streak + " stdDev=" + stdDev + " balance=" + balance);
                        streak = 0;
                    }
                } else {
                    vl = Math.max(0, vl - 1);
                    streak = Math.max(0, streak - 0.125);
                }
                lastFlying = now;
            }
        }
    }
}
