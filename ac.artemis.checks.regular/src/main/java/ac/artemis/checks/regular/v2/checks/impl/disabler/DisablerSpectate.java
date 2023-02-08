package ac.artemis.checks.regular.v2.checks.impl.disabler;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientSpectate;

/**
 * @author Ghast
 * @since 05/07/2020
 */
@Check(type = Type.DISABLER, var = "SPT", threshold = 3)
public class DisablerSpectate extends ArtemisCheck implements PacketHandler, PacketExcludable {

    public DisablerSpectate(PlayerData data, CheckInformation info) {
        super(data, info);
        this.setCompatiblePackets(GPacketPlayClientSpectate.class);
    }

    @Override
    public void handle(final GPacket packet) {
        final boolean invalid = this.isExempt(ExemptType.GAMEMODE);

        if (invalid) return;

        this.log("gamemode=" + data.getPlayer().getGameMode());
    }
}
