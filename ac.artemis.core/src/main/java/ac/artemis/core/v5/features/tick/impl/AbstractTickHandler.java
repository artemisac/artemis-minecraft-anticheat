package ac.artemis.core.v5.features.tick.impl;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.function.PacketAction;
import ac.artemis.core.v4.utils.hashing.EvictingMap;
import ac.artemis.core.v5.features.tick.TickHandlerFeature;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.Packet;
import lombok.Getter;

import java.util.*;

public abstract class AbstractTickHandler<T extends Packet> implements TickHandlerFeature<T> {
    protected final PlayerData data;

    public AbstractTickHandler(final PlayerData data) {
        this.data = data;
    }

    private final Map<Short, Long> dataMap = new HashMap<>();
    private final Map<Short, List<PacketAction>> actionMap = new HashMap<>();
    private final Map<Short, Long> receiveMap = new EvictingMap<>(50);

    private short ticks;
    private short lastTicks;

    @Getter
    private long ping;

    protected abstract boolean isPacket(final GPacket packet);
    protected abstract short getPacketId(final GPacket packet);
    protected abstract void onConsume(final T packet);
    protected abstract void onPush(final boolean flush, final short id);

    @Override
    public void checkReceive(final GPacket packet, final boolean remove) {
        if (!isPacket(packet)) return;

        final short id = this.getPacketId(packet);

        if (!remove) {
            //Bukkit.broadcastMessage("Received " + id);
            this.getSent(id).ifPresent(e -> ping = (packet.getTimestamp() - e) / 2);
            //this.getFunction(id, false).ifPresent(function -> function.forEach(PacketAction::pre));
            this.onConsume((T) packet);
        } else {
            this.getFunction(id, true).ifPresent(function -> function.forEach(PacketAction::post));
            this.dataMap.remove(id);
            this.actionMap.remove(id);
        }

        this.receiveMap.put(id, packet.getTimestamp());
    }

    @Override
    public void addToConfirm(final PacketAction packetAction, final short ticks) {
        //Bukkit.broadcastMessage("Adding confirmation for action in tick " + ticks);
        final List<PacketAction> actions = this.actionMap.get(ticks);
        if (actions != null) {
            actions.add(packetAction);
        } else {
            this.actionMap.put(ticks, new ArrayList<>(Collections.singleton(packetAction)));
        }
    }

    @Override
    public List<PacketAction> getActions(final short tick) {
        return this.actionMap.get(ticks);
    }


    @Override
    public boolean hasReceived(final short tick) {
        return receiveMap.containsKey(tick);
    }

    @Override
    public long whenSent(final short tick) {
        return dataMap.containsKey(tick) ? dataMap.get(tick) : -1;
    }

    @Override
    public void push(final boolean flush) {
        this.dataMap.put(ticks, System.currentTimeMillis());

        /*
         * This is not really randomization but it is still going to be a little unpredictable for the client
         * since it cannot really know the server ticks to be able to accurately assume the action number that is
         * about to be sent to them. If we wanted we could use a secure random just to make things a little more difficult.
         */
        this.onPush(flush, ticks);

        this.lastTicks = ticks;
        this.ticks = this.getNextTick();
    }

    private Optional<Long> getSent(final short actionNumber) {
        final Long timestamp = dataMap.get(actionNumber);

        if (timestamp == null) return Optional.empty();

        return Optional.of(timestamp);
    }

    private Optional<List<PacketAction>> getFunction(final short identification, final boolean remove) {
        final List<PacketAction> runnable = actionMap.get(identification);

        if (runnable == null) return Optional.empty();
        else if (remove) actionMap.remove(identification);

        return Optional.of(runnable);
    }


    private Optional<Long> getResponded(final short actionNumber) {
        final Long timestamp = receiveMap.get(actionNumber);

        if (timestamp == null) return Optional.empty();

        return Optional.of(timestamp);
    }

    @Override
    public short getTick() {
        return ticks;
    }

    @Override
    public short getNextTick() {
        short tick = ticks;
        if (tick == Short.MIN_VALUE) {
            tick = 0;
        }

        tick--;

        return tick;
    }

    @Override
    public short getLastTick() {
        return lastTicks;
    }
}
