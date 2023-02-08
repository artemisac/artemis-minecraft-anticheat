package ac.artemis.checks.enterprise.timer;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;

@Check(type = Type.TIMER, var = "BalanceX", threshold = 20)
public class TimerBalance extends ArtemisCheck implements PacketHandler {

    private long previousTick, balance, lastBalance;
    private int deceleration, acceleration, maxCatchupTicks;

    public TimerBalance(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            final long currentTick = packet.getTimestamp();

            // Check has issues with processing position packets on join and server lag. (thread lag too)
            if (!TimeUtil.hasExpired(data.user.getJoin(), 20) || this.isExempt(ExemptType.TPS)) return;

            if (this.previousTick != 0L) {
                final long delta = currentTick - previousTick;

                /*
                 * Here I'm calculating how many ticks the balance should be allowed to increase
                 * consecutively based on their last lagout time.
                 *
                 * This will be the third and final measure of abuse prevention I use in this check,
                 * to make sure there is little to no ability to use timer by abusing low timer.
                 *
                 * For example, if the player lags out for 2100ms, their balance will be allowed to rise
                 * consecutively for 57 ticks total (ceil(delta / 50)) + 10 == 57).
                 *
                 * If their balance is still rising constantly even after the max catchup ticks, then we can flag them.
                 *
                 * We will decrease the ticks if they are not lagging, so this will allow it to automatically compensate.
                 */
                if (delta > 90L) {
                    maxCatchupTicks = (int) (Math.ceil(delta / 50.0D) + 10);
                } else if (delta < 75L) {
                    if (maxCatchupTicks > 0) maxCatchupTicks--;
                }

                /*
                 * This is where the balance calculation begins.
                 * We expect the client to tick every 50ms and we are comparing the difference by
                 * adding the expected and subtracting the actual.
                 *
                 * If they are using positive timer the balance will rise because the real delta will be lower than normal.
                 * Negative timer does the opposite, and can cause the check to be abused, so I have implemented some
                 * measures to make sure that this cannot happen.
                 */
                this.balance += 50L;
                this.balance -= delta;

                // No buffer needed, the balance acts as its own buffer.
                if (balance > 50L) {
                    this.log(new Debug<>("balance", balance));

                    // Reset the balance behind a tick to keep the check from spamming.
                    balance = -50L;
                }

                this.debug("balance=" + balance
                        + "\nlastBalance=" + lastBalance
                        + "\ndelta=" + delta
                        + "\nmaxCatchupTicks=" + maxCatchupTicks
                        + "\ndeceleration=" + deceleration
                        + "\nacceleration=" + acceleration);
            }

            // Make sure to take a basic measure to make sure the balance can not be consistently abused.
            if (balance < -20000L) {
                balance = -20000L;
            }

            // Secondary measure to make sure balance is not abused by low timer.
            if (balance < lastBalance && balance < -200L) {
                // Because of lag, we're going to be pretty lenient here.
                if (++deceleration > 5) {
                    /*
                     * Reset the balance if they are abusing the check. You can alert if you want.
                     * I don't alert since I don't want cheaters to know this since this abuse prevention can be bypassed.
                     */
                    balance = -100L;
                    deceleration = 0;
                }
            } else {
                if (deceleration > 0) deceleration--;
            }

            // Third and final measure to make sure balance is not abused by low timer.
            if (balance > lastBalance) {
                ++acceleration;

                if (maxCatchupTicks == 0) {
                    if (acceleration > 15 && balance < -100L) {
                        balance = -100L;
                        acceleration = 0;
                    }
                }
            } else {
                acceleration = 0;
            }

            this.previousTick = currentTick;
            this.lastBalance = balance;
        }
    }
}

