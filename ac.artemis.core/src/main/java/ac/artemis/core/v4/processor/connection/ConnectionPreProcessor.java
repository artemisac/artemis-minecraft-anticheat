package ac.artemis.core.v4.processor.connection;

import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.core.v4.lag.LagManager;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.*;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientKeepAlive;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientTransaction;
import ac.artemis.core.v4.utils.maths.MathUtil;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerKeepAlive;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerTransaction;

/**
 * @author Ghast
 * @since 06-Mar-20
 */
public class ConnectionPreProcessor extends AbstractHandler {

    public ConnectionPreProcessor(PlayerData data) {
        super("Connection [0x01]", data);
    }

    private short transactionTick;

    @Override
    public void handle(final GPacket packet) {
        data.connection.handlePacket(packet, false);
        final long now = System.currentTimeMillis();
        final Player player = data.getPlayer();

        if (packet instanceof PacketPlayClientFlying) {
            PacketPlayClientFlying fly = (PacketPlayClientFlying) packet;

            data.user.deltaFly = data.user.lastFlyingPacket - now;
            data.user.offset = 50 - data.user.deltaFly;
            data.user.differencial += data.user.offset;
            data.user.currentTime += data.user.deltaFly;


            // Delta stuff to get ping
            try {
                data.user.setMaxPingTicks(MathUtil.hightestPing(data.user.ping,
                        data.user.keepAlivePing,
                        data.user.transactionPing) / 50);
            } catch (NullPointerException ignored) {
            }

            /*final short upcomingTick = (short) ((transactionTick + 1) % Short.MAX_VALUE);
            data.connection.getSentTransaction(upcomingTick)
                    .flatMap(e -> data.connection.getSentTransactionFunction(upcomingTick, false))
                    .ifPresent(function -> function.forEach(PacketAction::pre));*/

        } else if (packet instanceof GPacketPlayServerTransaction) {
            GPacketPlayServerTransaction trx = (GPacketPlayServerTransaction) packet;


        }

        else if (packet instanceof GPacketPlayClientTransaction) {
            final GPacketPlayClientTransaction tra = (GPacketPlayClientTransaction) packet;




        } else if (packet instanceof GPacketPlayServerKeepAlive) {
            data.user.setLastSentKeepAlive(now);
        } else if (packet instanceof GPacketPlayClientKeepAlive) {
            final GPacketPlayClientKeepAlive ka = (GPacketPlayClientKeepAlive) packet;

            if (data.user.getLastKeepAlive() != 0) {
                data.user.setKeepAlivePing(Math.round(((float) now - (float) data.user.getLastKeepAlive()) / 2));
            }

            data.user.setLastKeepAlive(packet.getTimestamp());
            /*data.connection.getSentKeepAlive((int) ka.getId())
                    .ifPresent(timestamp -> data.connection.getRespondedKeepAliveMap().putIfAbsent((int) ka.getId(),
                            System.currentTimeMillis()));
            data.connection.getSentKeepAliveFunction((int) ka.getId(), false)
                    .ifPresent(function -> function.forEach(PacketAction::pre));*/

            data.user.setPing(LagManager.getPing(data.getPlayer()));
        }
        data.timing.connectionTiming.addTime(now, System.currentTimeMillis());
    }
}
