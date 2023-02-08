package ac.artemis.checks.regular.v2.checks.impl.disabler;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.lag.LagManager;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientTransaction;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerTransaction;

import java.util.*;

/**
 * @author Ghast
 * @since 05/07/2020
 */
@Check(type = Type.DISABLER, var = "TRX", threshold = 3)
public class DisablerTransaction  extends ArtemisCheck implements PacketHandler {

    private boolean sent;
    private long time, timeExpected;
    private final Deque<Short> expectedIds = new LinkedList<>();

    public DisablerTransaction(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayServerTransaction){
            final GPacketPlayServerTransaction wrapper = (GPacketPlayServerTransaction) packet;

            if (wrapper.getWindowId() != 0 || wrapper.isAccepted()) return;

            // First part of this check which is the order of all packets
            this.expectedIds.add(wrapper.getActionNumber());


            // Second part of this check which is the maximum response time
            if (!sent){
                this.sent = true;
                this.time = packet.getTimestamp();

                // First set the time expected to the current timestamp
                this.timeExpected = packet.getTimestamp();

                // Increment it by the Player's estimated ping times 3 for both travels + compensation for lag JUST in-case
                this.timeExpected += (int) Math.min(Math.ceil(LagManager.getPing(data.getPlayer()) * 4), 5000);

                // Increment it by server + client processing time and additional time for compression and decompression
                // This is the usual timeout limit for both ways
                this.timeExpected += 60 * 1000;
            }
        }

        // First part of the check's detection
        else if (packet instanceof GPacketPlayClientTransaction){
            final GPacketPlayClientTransaction trx = (GPacketPlayClientTransaction) packet;

            if (trx.getWindowId() != 0 || !trx.isAccepted()) return;

            // First ensure we've sent one
            if (expectedIds.size() > 0) {

                // Grabbing the next expected ID
                int expected;

                // By basic Netty and TCP structure, the first sent should be the first received
                while (expectedIds.peekFirst() != null && (expected = expectedIds.pollFirst()) != trx.getActionNumber()){
                    if (this.isExempt(ExemptType.JOIN)) continue;
                    this.log("expectedId=" + expected + " got=" + trx.getActionNumber());
                }

                // Set the sent to false as we've received a response which is guaranteed to be the correct one
                this.sent = false;
            }
        }

        // Second part of the check's detection
        else if (packet instanceof PacketPlayClientFlying) {
            this.time += 50;

            handle: {
               if (!sent || expectedIds.size() == 0) break handle;

                // This is quite literally impossible considering how lenient I am with the values. Henceforth, the user
                // Is quite obviously cheating. This can however cause issues on 1.9+, which is why the following has to be added
                final long flagTime = data.getVersion().isOrAbove(ProtocolVersion.V1_9) ? System.currentTimeMillis() : time;

                final boolean flag = data.getVersion().isOrAbove(ProtocolVersion.V1_9)
                        // As long as the user is not lagging and on 1.9+, value can be almost correct
                        // Todo check if the last positions had a move to ensure flying packets are sent, or at least position ones
                        ? flagTime > timeExpected && !data.user.isLagging()

                        // On 1.8 however it's 100% accurate
                        : flagTime > timeExpected;

                if (flag && !data.user.isOnCooldown() || !this.isExempt(ExemptType.JOIN)){
                    //this.log("time=" + time + " max=" + timeExpected);
                }
            }
        }
    }
}
