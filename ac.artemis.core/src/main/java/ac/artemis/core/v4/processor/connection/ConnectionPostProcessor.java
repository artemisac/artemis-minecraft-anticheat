package ac.artemis.core.v4.processor.connection;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v4.utils.function.PacketAction;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientKeepAlive;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientTransaction;

public class ConnectionPostProcessor extends AbstractHandler {
    public ConnectionPostProcessor(PlayerData data) {
        super("Connection [0x02]", data);
    }

    @Override
    public void handle(final GPacket packet) {
        data.connection.handlePacket(packet, true);
        if (packet instanceof GPacketPlayClientTransaction){
            GPacketPlayClientTransaction trx = (GPacketPlayClientTransaction) packet;
            /*data.connection.getSentTransactionFunction(trx.getActionNumber(), true)
                    .ifPresent(function -> function.forEach(PacketAction::post));
            data.connection.getTransactionMap().remove(trx.getActionNumber());*/
        }

        else if (packet instanceof GPacketPlayClientKeepAlive) {
            final GPacketPlayClientKeepAlive ka = (GPacketPlayClientKeepAlive) packet;
            /*data.connection.getSentKeepAliveFunction((int) ka.getId(), true)
                    .ifPresent(function -> function.forEach(PacketAction::post));*/
        }
    }
}
