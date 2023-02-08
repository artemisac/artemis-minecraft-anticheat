package ac.artemis.checks.enterprise.protocol;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.*;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientClientCommand;

/**
 * @author Ghast
 * @since 04/12/2020
 * Artemis © 2020
 *
 * This is a relatively simple window check. Now remain aware as to what you have to be
 * extremely sensitive with this one: using the twitch window you can exit the GUI without
 * sending the packet, effectively causing a desync. What's important to add here is a proper
 * 'protection' for this kind of situation.
 *
 * Todo: Add setback for inv open movement with prediction handling.
 */
@Check(type = Type.PROTOCOL, var = "H", threshold = 5)
public class ProtocolH extends ArtemisCheck implements PacketHandler {

    public ProtocolH(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    private boolean sentUse;
    private boolean sentClick;

    @Override
    public void handle(GPacket packet) {
        if (packet instanceof PacketPlayClientUseEntity) {
            if (sentClick) {
                this.log(
                        new Debug<>("state", "USE")
                );
            } else {
                this.sentUse = true;
            }
        }

        else if (packet instanceof PacketPlayClientWindowClick) {
            if (sentUse) {
                this.log(
                        new Debug<>("state", "USE")
                );
            } else {
                this.sentClick = true;
            }
        }

        else if (packet instanceof PacketPlayClientWindowClose) {
            if (data.getVersion().isOrBelow(ProtocolVersion.V1_8_9))
                return;

            this.sentUse = this.sentClick = false;
        }

        else if (packet instanceof PacketPlayClientCommand) {
            if (data.getVersion().isOrBelow(ProtocolVersion.V1_8_9))
                return;

            final GPacketPlayClientClientCommand wrapper = (GPacketPlayClientClientCommand) packet;

            if (!wrapper.getCommand().equals(PlayerEnums.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT)) {
               return;
            }

            this.sentUse = this.sentClick = false;
        }

        else if (packet instanceof PacketPlayClientFlying) {
            this.sentUse = false;
        }
    }
}
