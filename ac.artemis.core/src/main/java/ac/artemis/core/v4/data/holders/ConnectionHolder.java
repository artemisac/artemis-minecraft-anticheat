package ac.artemis.core.v4.data.holders;


import ac.artemis.core.Artemis;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.function.PacketAction;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.core.v5.features.tick.TickHandlerFeature;
import ac.artemis.core.v5.features.tick.impl.TransactionTickHandler;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityTeleport;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerPosition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class ConnectionHolder extends AbstractHolder {
    public ConnectionHolder(PlayerData data) {
        super(data);

        this.featureMap.put(ConfirmationType.TRANSACTION, new TransactionTickHandler(data));
        //this.featureMap.put(ConfirmationType.RESOURCE_PACK, new ResourcePackTickHandler(data));
    }

    /*
     * Actual entity stuff here
     */
    public int transactionTick;
    public int resourcePackTick;

    private Map<ConfirmationType, TickHandlerFeature<?>> featureMap = new EnumMap<>(ConfirmationType.class);

    public void confirmFunction(final ConfirmationType confirmationType, final PacketAction action) {
        final TickHandlerFeature<?> feature = featureMap.get(confirmationType);
        if (!feature.canReceive())
            throw new IllegalStateException("Cannot use type " + confirmationType.name() + " on player " + data.getPlayer().getName());

        feature.addToConfirm(action, feature.getTick());
    }

    public void confirmFunctionLast(final ConfirmationType confirmationType, final PacketAction action) {
        final TickHandlerFeature<?> feature = featureMap.get(confirmationType);
        if (!feature.canReceive())
            throw new IllegalStateException("Cannot use type " + confirmationType.name() + " on player " + data.getPlayer().getName());

        final boolean push = feature.getActions(feature.getLastTick()) == null
                || feature.hasReceived(feature.getLastTick())
                || TimeUtil.elapsed(feature.whenSent(feature.getLastTick()), 5L);

        if (push) {
            feature.addToConfirm(action, feature.getTick());
            feature.push(true);
        } else {
            feature.addToConfirm(action, feature.getLastTick());
        }
    }

    public void confirmFunctionAndTick(final ConfirmationType confirmationType, final PacketAction action) {
        final TickHandlerFeature<?> feature = featureMap.get(confirmationType);
        if (!feature.canReceive())
            throw new IllegalStateException("Cannot use type " + confirmationType.name() + " on player " + data.getPlayer().getName());

        feature.addToConfirm(action, feature.getTick());
        feature.push(true);
    }

    public void confirmFunctionAndTick(final ConfirmationType confirmationType, final boolean flush, final PacketAction action) {
        final TickHandlerFeature<?> feature = featureMap.get(confirmationType);
        if (!feature.canReceive())
            throw new IllegalStateException("Cannot use type " + confirmationType.name() + " on player " + data.getPlayer().getName());

        feature.addToConfirm(action, feature.getTick());
        feature.push(flush);
    }

    public void handlePacket(final GPacket packet, final boolean remove) {
        for (ConfirmationType value : ConfirmationType.values()) {
            final TickHandlerFeature<?> feature = featureMap.get(value);
            if (!feature.canReceive()) continue;
            feature.checkReceive(packet, remove);
        }
    }

    public void pushTick() {
        for (ConfirmationType value : ConfirmationType.values()) {
            final TickHandlerFeature<?> feature = featureMap.get(value);
            if (!feature.canReceive()) continue;
            feature.push(true);
        }
    }

    public TickHandlerFeature<?> getTransactionFeature() {
        return featureMap.get(ConfirmationType.TRANSACTION);
    }

    private GPacketPlayServerPosition lastTeleportPacket;
    private GPacketPlayServerEntityTeleport lastEntityTeleport;

    @RequiredArgsConstructor
    public enum ConfirmationType {
        // Only > 1.10.2
        //RESOURCE_PACK,
        TRANSACTION;
    }
}
