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
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockDig;

/**
 * @author Ghast
 * @since 29/11/2020
 * Artemis © 2020
 * <p>
 * This check works on the basis that all dig packets having a face equivalent to 255
 * must be release use packets. Otherwise, they don't follow adequate protocol hence the flag
 */
@Check(type = Type.PROTOCOL, var = "A", threshold = 1)
public class ProtocolA extends ArtemisCheck implements PacketHandler, PacketExcludable {

    private static final PlayerEnums.DigType INVALID_TYPE = PlayerEnums.DigType.RELEASE_USE_ITEM;

    public ProtocolA(final PlayerData data, final CheckInformation info) {
        super(data, info);
        this.setCompatiblePackets(GPacketPlayClientBlockDig.class);
    }

    @Override
    public void handle(final GPacket packet) {
        final GPacketPlayClientBlockDig dig = (GPacketPlayClientBlockDig) packet;

        if (dig.getType() != INVALID_TYPE && dig.getDirection() == 255) {
            this.log(
                    new Debug<>("type", dig.getType().name())
            );
        }
    }
}
