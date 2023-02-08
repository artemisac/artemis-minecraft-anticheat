package ac.artemis.checks.enterprise.protocol;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;

/**
 * @author Ghast
 * @since 29/11/2020
 * Artemis © 2020
 *
 * Basic vehicle spoofing bad packet. This is extremely dangerous against the prediction system.
 * I've only just added this. It could be prone to false positives; To investigate!
 */

@Check(type = Type.PROTOCOL, var = "D3", threshold = 3)
public class ProtocolD3 extends ArtemisCheck implements PacketHandler, PacketExcludable {

    public ProtocolD3(final PlayerData data, final CheckInformation info) {
        super(data, info);
        this.setCompatiblePackets(
                // Flying
                PacketPlayClientFlying.class,
                GPacketPlayClientLook.class,
                GPacketPlayClientPosition.class,
                GPacketPlayClientPositionLook.class,
                // Vehicle
                GPacketPlayClientSteerVehicle.class
        );
    }

    private int stage;
    private int buffer;

    @Override
    public void handle(final GPacket packet) {
        switch (stage) {
            /*
             * Corresponds to the pre-ticking phase. In this stage, we're expecting if the player is in fact inside
             * a vehicle to send a look packet only and no steer. If such happens, the stage can escalate.
             */
            case 0: {
                if (packet instanceof PacketPlayClientFlying) {
                    final PacketPlayClientFlying fly = (PacketPlayClientFlying) packet;

                    if (fly.isLook() && !fly.isPos()) {
                        this.stage++;
                    }
                }

                /*
                 * User has not sent a flying look before. He is cheating. Fuck them cheaters. I hate the cheaters.
                 * Fuck you.
                 */
                else if (packet instanceof GPacketPlayClientSteerVehicle) {
                    if (buffer++ > 2) {
                        this.log(
                                new Debug<>("stage", stage),
                                new Debug<>("buffer", buffer),
                                new Debug<>("type", packet.getRealName())
                        );
                    }
                }

                break;
            }   

            case 1: {
                /*
                 * You're not in a mf vehicle fam. Retard boy thought he could get away with it. Nice try
                 * dickhead.
                 */
                if (packet instanceof GPacketPlayClientSteerVehicle && !data.prediction.isInVehicle()) {
                    // Ok maybe he is but I cba about desync
                    if (buffer++ > 2) {
                        this.log(
                                new Debug<>("stage", stage),
                                new Debug<>("buffer", buffer),
                                new Debug<>("type", packet.getRealName())
                        );
                    }
                } else {
                    this.buffer = Math.max(buffer - 1, 0);
                }

                this.stage = 0;
            }
        }
    }
}
