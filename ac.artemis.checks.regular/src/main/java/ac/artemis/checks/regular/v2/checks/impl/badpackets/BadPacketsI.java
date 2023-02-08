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
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

@Check(type = Type.BADPACKETS, var = "I", threshold = 1)
public class BadPacketsI extends ArtemisCheck implements PacketHandler {

    private boolean sent;

    public BadPacketsI(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = (GPacketPlayClientUseEntity) packet;

            if (sent && wrapper.getType() == PlayerEnums.UseType.ATTACK) {
                log();
            }

        } else if (packet instanceof GPacketPlayClientEntityAction) {
            final GPacketPlayClientEntityAction wrapper = (GPacketPlayClientEntityAction) packet;

            switch (wrapper.getAction()) {
                case START_SNEAKING:
                case STOP_SNEAKING:
                case START_SPRINTING:
                case STOP_SPRINTING:
                    sent = true;
                    break;
            }

        } else if (packet instanceof PacketPlayClientFlying) {
            sent = false;
        }
    }
}
