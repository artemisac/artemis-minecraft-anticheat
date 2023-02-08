package ac.artemis.checks.regular.v2.checks.impl.autoclicker;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

@Check(type = Type.AUTOCLICKER, var = "J")
@Experimental
public class AutoClickerJ  extends ArtemisCheck implements PacketHandler {
    private int ticks = 0, lastTicks = 0, totalTicks = 0, buffer = 0, streak = 0;

    public AutoClickerJ(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = (GPacketPlayClientUseEntity) packet;

            if (wrapper.getType() == PlayerEnums.UseType.ATTACK) {
                final boolean proper = ticks < 4 && lastTicks < 4;

                if (proper) {
                    final boolean invalid = ticks == lastTicks;

                    if (invalid) {
                        ++buffer;
                    }

                    if (++totalTicks == 25) {
                        if (buffer > 22) {
                            this.log("b=" + buffer);
                        }

                        if (++buffer > 15) {
                            if (++streak > 2) {
                                this.log("s=" + streak);
                            }
                        } else {
                            streak = 0;
                        }

                        totalTicks = 0;
                    }
                }
                this.lastTicks = ticks;
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            ++ticks;
        }
    }
}
