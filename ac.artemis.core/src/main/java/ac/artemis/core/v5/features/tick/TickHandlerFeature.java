package ac.artemis.core.v5.features.tick;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.function.PacketAction;
import ac.artemis.packet.wrapper.Packet;
import cc.ghast.packet.wrapper.packet.ClientPacket;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ServerPacket;

import java.util.List;
import java.util.function.Function;

public interface TickHandlerFeature<T extends Packet> {
    boolean canReceive();

    void checkReceive(final GPacket packet, final boolean remove);

    void addToConfirm(final PacketAction packetAction, final short ticks);

    List<PacketAction> getActions(final short tick);

    void push(final boolean flush);

    boolean hasReceived(final short tick);

    long whenSent(final short tick);

    short getTick();

    short getNextTick();

    short getLastTick();

    long getPing();
}
