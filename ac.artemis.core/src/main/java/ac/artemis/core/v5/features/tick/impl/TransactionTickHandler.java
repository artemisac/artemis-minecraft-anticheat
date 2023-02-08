package ac.artemis.core.v5.features.tick.impl;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.language.Lang;
import ac.artemis.packet.PacketManager;
import cc.ghast.packet.PacketAPI;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientTransaction;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerTransaction;

public class TransactionTickHandler extends AbstractTickHandler<GPacketPlayClientTransaction> {
    public TransactionTickHandler(final PlayerData data) {
        super(data);
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public boolean isPacket(final GPacket packet) {
        return packet instanceof GPacketPlayClientTransaction;
    }

    @Override
    public short getPacketId(final GPacket packet) {
        return ((GPacketPlayClientTransaction) packet).getActionNumber();
    }

    @Override
    public void onConsume(final GPacketPlayClientTransaction packet) {
        data.connection.transactionTick = packet.getActionNumber();
    }

    @Override
    public void onPush(final boolean flush, final short id) {
        /*
         * We're constructing the packet to send it to the player when its time to tick using the information
         * from above. We don't need to do any fancy thread management since everything is already done for us
         * in a parallel stream. We're also only putting load in the netty thread which seems to be able to take it.
         */
        final GPacketPlayServerTransaction transaction = new GPacketPlayServerTransaction((byte) 0, id, false);

        /*
         * Send the packet to the player through the packet feature inside of Artemis. We cannot do it
         * dynamically using entity player since it would collide with our current wrapping system in artemis,
         */
        try {
            PacketManager.getApi().sendPacket(data.getPlayer().getUniqueId(), transaction);
        } catch (final IllegalStateException e) {
            Chat.sendConsoleMessage(Lang.MSG_CONSOLE_TICK_ERROR.replace("%player%", data.getPlayer().getName()));
            e.printStackTrace();
            Server.v().getScheduler().runTask(() -> {
                data.getPlayer().kickPlayer("Invalid connection");
            });
            Chat.sendConsoleMessage(Chat.spacer());
        }
    }
}
