package ac.artemis.checks.regular.v2.checks.impl.badpackets;


import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientEntityAction;

@Check(type = Type.BADPACKETS, var = "C", threshold = 1)
public class BadPacketsC extends ArtemisCheck implements PacketHandler {

    private boolean sent;

    public BadPacketsC(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            this.sent = false;
        } else if (packet instanceof GPacketPlayClientEntityAction) {
            final PlayerEnums.PlayerAction action = ((GPacketPlayClientEntityAction) packet).getAction();
            if (action == PlayerEnums.PlayerAction.START_SPRINTING
                    || action == PlayerEnums.PlayerAction.STOP_SPRINTING) {
                if (this.sent) {
                    log(1);
                } else {
                    this.sent = true;
                }
            }
        }
    }
}
