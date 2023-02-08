package ac.artemis.checks.regular.v2.checks.impl.disabler;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientClientCommand;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;

/**
 * @author Ghast
 * @since 05/07/2020
 */
@Check(type = Type.DISABLER, var = "RSP", threshold = 3)
public class DisablerRespawn extends ArtemisCheck implements PacketHandler {

    public DisablerRespawn(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private float buffer;

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientClientCommand) {
            final GPacketPlayClientClientCommand cmd = (GPacketPlayClientClientCommand) packet;

            if (cmd.getCommand() != PlayerEnums.ClientCommand.PERFORM_RESPAWN) return;

            flag: {
                final boolean dead = data.getPlayer().isDead();

                if (dead) {
                    break flag;
                }

                if (buffer++ > 2) {
                    this.log("");
                }
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            buffer -= 0.0125;
        }
    }
}
