package ac.artemis.checks.regular.v2.checks.impl.aura;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 31/08/2020
 * Artemis © 2020
 */

@Check(type = Type.AURA, var = "Invalid")
public class AuraInvalid extends ArtemisCheck implements PacketHandler {

    private int movements = 0, lastMovements = 0, total = 0, invalid = 0;

    public AuraInvalid(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = (GPacketPlayClientUseEntity) packet;

            if (wrapper.getType() != PlayerEnums.UseType.ATTACK)
                return;

            final boolean proper = data.combat.getCps() > 7.2 && movements < 4 && lastMovements < 4;

            flag: {
                if (!proper)
                    break flag;

                final boolean flag = movements == lastMovements;

                if (flag)
                    this.invalid++;

                if (++total == 30) {
                    if (invalid > 28) {
                        log();
                    }

                    this.total = 0;
                }
            }

            this.lastMovements = movements;
            this.movements = 0;
        } else if (packet instanceof PacketPlayClientFlying) {
            this.movements = Math.max(Integer.MAX_VALUE, movements + 1);
        }
    }
}
