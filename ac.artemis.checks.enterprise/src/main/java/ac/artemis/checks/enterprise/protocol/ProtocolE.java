package ac.artemis.checks.enterprise.protocol;

import ac.artemis.packet.minecraft.GameMode;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.debug.DebugHandler;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientAbilities;

/**
 * @author Ghast
 * @since 04/12/2020
 * Artemis © 2020
 *
 * This is as simple and concise of an ability check I could write. The client should not
 * ever be sending any of these privilege escalation requests without it being authorised
 * prior on the server. I would definitely deem this check as unfalsable to some degree.
 */

@Check(type = Type.PROTOCOL, var = "E", threshold = 1)
public class ProtocolE extends ArtemisCheck implements PacketHandler, PacketExcludable, DebugHandler {

    public ProtocolE(final PlayerData data, final CheckInformation info) {
        super(data, info);
        this.setCompatiblePackets(GPacketPlayClientAbilities.class);
    }

    @Override
    public void handle(final GPacket packet) {
        final GPacketPlayClientAbilities abilities = (GPacketPlayClientAbilities) packet;

        /*
         * The client should always receive granted fly permission from the server. In the
         * case it doesn't, we can flag this. This should not cause any sort of issues on any version.
         * @see NetPlayerClientHandler
         * @see NetPlayerServerHandler
         */
        final boolean allow = abilities.getAllowedFlight().isPresent()
                && abilities.getAllowedFlight().get()
                && !data.getPlayer().isAllowedFlight();

        /*
         * This specific flag is slightly more difficult to comprehend. Flying represents whether
         * the individual is authorized by the server is vanilla fly. This is used by some disablers.
         * It is important to take count of it.
         * @see NetPlayerClientHandler
         * @see NetPlayerServerHandler
         */
        final boolean fly = abilities.isFlying() && !data.getPlayer().isFlying();


        final boolean god = abilities.getInvulnerable().isPresent()
                && abilities.getInvulnerable().get()
                && !data.getPlayer().isInvulnerable();
        final boolean creative = abilities.getCreativeMode().isPresent()
                && abilities.getCreativeMode().get()
                && !data.getPlayer().getGameMode().equals(GameMode.CREATIVE);
        final boolean flySpeed = abilities.getFlySpeed().isPresent()
                && abilities.getFlySpeed().get() > (data.getPlayer().getFlySpeed() / 2.F);
        final boolean groundSpeed = abilities.getWalkSpeed().isPresent()
                && abilities.getWalkSpeed().get() > (data.getPlayer().getWalkSpeed() / 2.F);

        flag: {
            final boolean flag = allow || fly || god || creative || flySpeed || groundSpeed;

            if (!flag) break flag;

            this.log(
                    new Debug<>("allowFly", allow),
                    new Debug<>("fly", fly),
                    new Debug<>("god", god),
                    new Debug<>("creative", creative),
                    new Debug<>("flySpeed", flySpeed),
                    new Debug<>("walkSpeed", groundSpeed)
            );
        }

        this.debug("allowFly=%s fly=%s god=%s creative=%s flySpeed=%s walkSpeed=%s", allow, fly, god, creative, flySpeed, groundSpeed);
    }
}
