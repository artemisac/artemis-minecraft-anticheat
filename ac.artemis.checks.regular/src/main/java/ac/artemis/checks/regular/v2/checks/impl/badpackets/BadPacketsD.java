package ac.artemis.checks.regular.v2.checks.impl.badpackets;


import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientEntityAction;

@Check(type = Type.BADPACKETS, var = "D", threshold = 1)
@ClientVersion
public class BadPacketsD extends ArtemisCheck implements PacketHandler {

    private boolean sent;

    public BadPacketsD(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        final boolean exempt = this.isExempt(
                ExemptType.GAMEMODE
        );

        if (exempt) return;

        if (packet instanceof PacketPlayClientFlying) {
            this.sent = false;
        } else if (packet instanceof GPacketPlayClientEntityAction) {
            final PlayerEnums.PlayerAction action = ((GPacketPlayClientEntityAction) packet).getAction();
            if (action == PlayerEnums.PlayerAction.START_SNEAKING
                    || action == PlayerEnums.PlayerAction.STOP_SNEAKING) {
                if (this.sent) {
                    log(1);
                } else {
                    this.sent = true;
                }
            }
        }
    }
}
