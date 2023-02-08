package ac.artemis.core.v4.processor.potion;

import ac.artemis.core.v4.check.FastProcessHandler;
import ac.artemis.core.v4.data.holders.ConnectionHolder;
import ac.artemis.core.v4.utils.function.PacketAction;
import cc.ghast.packet.PacketAPI;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientTransaction;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.potion.PotionEffect;
import ac.artemis.core.v4.processor.AbstractHandler;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityEffect;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityEffectRemove;

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

public class PotionPreProcessor extends AbstractHandler implements FastProcessHandler {
    public PotionPreProcessor(PlayerData data) {
        super("Potion [0x01]", data);
    }

    @Override
    public void handle(final GPacket packet) {

    }

    @Override
    public void fastHandle(GPacket packet) {
        if (packet instanceof GPacketPlayServerEntityEffect) {
            final GPacketPlayServerEntityEffect eff = (GPacketPlayServerEntityEffect) packet;
            if (eff.getEntityId() == data.getPlayer().getEntityId()) {

                final PotionEffect effect = new PotionEffect(eff.getEffectId(), eff.getDuration(), eff.getAmplifier());

                data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        data.user.getEffects().add(effect);
                        data.user.setLastAddEffect(effect);
                        data.user.setEffectAddProcessed(true);

                        if (data.user.isUsingItem() && data.user.isUsingPotion()) {
                            data.user.setUsingItem(false);
                            data.user.setUsingPotion(false);
                        }
                    }

                    @Override
                    public void post() {
                        data.user.setEffectAddProcessed(false);
                    }
                });
            }
        }

        else if (packet instanceof GPacketPlayServerEntityEffectRemove) {
            final GPacketPlayServerEntityEffectRemove eff = (GPacketPlayServerEntityEffectRemove) packet;
            if (eff.getEntityId() == data.getPlayer().getEntityId()) {

                final PotionEffect effect = data.user.getEffects().parallelStream().filter(e -> e.getPotionID() == eff.getEffectId()).findAny().orElse(null);

                if (effect == null) return;

                data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        data.user.getEffects().remove(effect);
                        data.user.setLastRemoveEffect(effect);
                        data.user.setEffectRemoveProcessed(true);
                    }

                    @Override
                    public void post() {
                        data.user.setEffectRemoveProcessed(false);
                    }
                });
            }
        }
    }
}
