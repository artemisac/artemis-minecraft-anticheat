package ac.artemis.checks.enterprise.spoof;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.FastProcessHandler;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.chat.StringUtil;
import ac.artemis.packet.PacketManager;
import cc.ghast.packet.PacketAPI;
import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientCustomPayload;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientKeepAlive;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientResourcePackStatus;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerKeepAlive;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerResourcePackSend;
import java.util.Deque;
import java.util.LinkedList;

/**
 * @author Ghast
 * @since 25/03/2021
 * Artemis © 2021
 *
 * This is most effectively the best antibot protection I've got on Artemis. By combining the
 * concept of checking a random resource pack link and the usage of keep alives, we can pretty
 * much capture whether or not a player is a bot or has a broken client misplacing keep-alives/
 * resource packs.
 */
@Check(type = Type.PINGSPOOF, var = "Protocol")
public class PingSpoofProtocol extends ArtemisCheck implements PacketHandler, FastProcessHandler {
    public PingSpoofProtocol(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private final Deque<Long> ids = new LinkedList<>();
    private Long next;

    @Override
    public void handle(GPacket packet) {
        final boolean exempt = this.isExempt(ExemptType.JOIN);

        /*
         * They removed the hash :(. We can't use it to identify packet order. How unlucky. This would mean
         * this would false on laggy and latter clients; We can instead from 1.9+ use fake teleports.
         */
        if (data.getVersion().isOrAbove(ProtocolVersion.V1_10))
            return;

        /*
         * Lets handle resource pack status. Since we flush the channel before, theoretically this should be processed
         * beforehand?
         */
        if (packet instanceof GPacketPlayClientResourcePackStatus) {
            final GPacketPlayClientResourcePackStatus sts = (GPacketPlayClientResourcePackStatus) packet;
            /*
             * Lets not interfere with real resource pack requests alright?
             */
            if (!StringUtil.isNumeric(sts.getUrl().get())) {
                this.debug("Invalid id");
                return;
            }

            /*
             * Theoretically speaking, the client can ONLY respond with FAILED_DOWNLOAD. Anything other
             * than that is completely invalid. Hence we can just skip over it.
             */
            if (!sts.getStatus().equals(PlayerEnums.ResourcePackStatus.FAILED_DOWNLOAD) && !exempt) {
                this.log("Successfully downloaded - Invalid");
                this.debug("[FLAG] Successfully downloaded - Invalid");
                return;
            }

            /*
             * Both resource pack and keep alive packets are async to the threading. Meaning they aren't ticked.
             * This means these are synchronous but not correlated to other packets. Perfect for a pingspoof check.
             * If no response is received, it's invalid
             */
            if (ids.isEmpty() && !exempt) {
                this.log("Packet coming from idk where");
                this.debug("[FLAG] Packet coming from idk where");
                return;
            }

            /*
             * Alrighty lets get decoding.
             */
            final long var = Long.decode(sts.getUrl().get());

            /*
             * No clue where tf this keep alive coming from hommie, but it defo ain't ours, so it's a bad
             * packet.
             */
            if (!ids.contains(var) && !exempt) {
                this.log("Keep alive does not exist bruh");
                this.debug("[FLAG] Keep alive does not exist bruh");
                return;
            }

            long id = ids.poll();

            /*
             * Well this is awkward. We're receiving the wrong... packet id?
             * Hence, this is deemed as a bad packet.
             */
            if (id != var && ids.contains(var)) {

                while (id != var && !ids.isEmpty()) {
                    id = ids.poll();

                    if (exempt)
                        continue;

                    this.log("Invalid identifier, polling queue...");
                }
            }

            /*
             * Nice alright, valid keep alive, lets mark the next id to receive.
             */
            else {
                next = var;
                ids.remove(var);

                this.debug("resource=" + var);
            }
        }

        /*
         * Since the keep alive flushed out a resource pack status packet, the next packet received should be the
         * resource pack denial then only the keep alive.
         */
        else if (packet instanceof GPacketPlayClientKeepAlive) {
            final GPacketPlayClientKeepAlive ka = (GPacketPlayClientKeepAlive) packet;

            if (next == null) {
                if (!exempt)
                    this.log("No confirmation");
                return;
            }

            if (next == ka.getId()) {
                this.debug("ka=" + ka.getId());
            } else if (!exempt) {
                this.log("Packet order 0x1");
                this.debug("[FLAG] Packet order 0x1");
            }
        }
    }

    @Override
    public void fastHandle(GPacket packet) {
        /*
         * They removed the hash :(. We can't use it to identify packet order. How unlucky. This would mean
         * this would false on laggy and latter clients; We can instead from 1.9+ use fake teleports.
         */
        if (data.getVersion().isOrAbove(ProtocolVersion.V1_10))
            return;

        /*
         * Only handle server keep alives because uhhh
         */
        if (packet instanceof GPacketPlayServerKeepAlive) {
            final GPacketPlayServerKeepAlive ka = (GPacketPlayServerKeepAlive) packet;

            this.ids.add(ka.getId());
            final String encoded = Long.toString(ka.getId());
            PacketManager.getApi().sendPacket(
                    data.getPlayer().getUniqueId(),
                    new GPacketPlayServerResourcePackSend(
                            "level://" + Math.random() + "/resources.zip", encoded
                    )
            );
        }
    }
}
