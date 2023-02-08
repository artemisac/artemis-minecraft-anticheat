package ac.artemis.core.v4.processor.interact;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.impl.Arrow;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v4.data.PlayerData;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;

/**
 * @author Ghast
 * @since 06-Mar-20
 */
public class InteractPreProcessor extends AbstractHandler {

    public InteractPreProcessor(PlayerData data) {
        super("Interact [0x02]", data);
    }

    @Override
    public void handle(final GPacket packet) {
        final long now = System.currentTimeMillis();
        final Player player = data.getPlayer();

        if (packet instanceof GPacketPlayClientEntityAction) {
            final GPacketPlayClientEntityAction p = (GPacketPlayClientEntityAction) packet;

            if (p.getAction() != null && p.getEntityId() == player.getEntityId()) {
                switch (p.getAction()) {
                    case START_SPRINTING:
                        data.user.setSprinting(true);
                        break;
                    case STOP_SPRINTING:
                        data.user.setSprinting(false);
                        break;
                    case START_SNEAKING:
                        data.user.setSneaking(true);
                        break;
                    case STOP_SNEAKING:
                        data.user.setSneaking(false);
                        break;
                }

                /*if (p.getAction().equals(PlayerEnums.PlayerAction)) {
                    data.movement.setFalling(true);
                } else if (this.data.user.onFakeGround) {
                    data.movement.setFalling(false);
                }*/
            }

        }

        // ENTITY ACTION

        // ENTITY BLOCK DIG

        else if (packet instanceof GPacketPlayClientBlockDig) {
            final GPacketPlayClientBlockDig p = (GPacketPlayClientBlockDig) packet;
            final PlayerEnums.DigType type = p.getType();

            // Push

            if (type != null) {
                switch (type) {
                    case START_DESTROY_BLOCK:
                        data.user.setDigging(true);
                        break;
                    case STOP_DESTROY_BLOCK:
                    case ABORT_DESTROY_BLOCK:
                        data.user.setDigging(false);
                        data.user.setLastDig(now);
                    case DROP_ITEM:
                    case DROP_ALL_ITEMS:
                    case SWAP_HELD_ITEMS:
                    case RELEASE_USE_ITEM:
                        data.user.setUsingItem(false);
                        break;
                }
            }

            //System.out.println("[blockdig] type=" + p.getType() + " p=" + p.toString());


        }

        // ENTITY BLOCK PLACE

        else if (packet instanceof GPacketPlayClientBlockPlace) {
            final GPacketPlayClientBlockPlace place = (GPacketPlayClientBlockPlace) packet;
            if (place.getDirection().isPresent()) {
                if (place.getItem().isPresent() && place.getItem().get().getType() != null && place.getItem().get()
                        .getType().isBlock()) {
                    data.user.setPlaced(true);
                    data.user.setLastPlace(System.currentTimeMillis());
                }
            } else {
                data.user.setUsingItem(true);

                if (place.getItem().isPresent() && place.getItem().get().getType().equals(NMSMaterial.POTION.getMaterial())) {
                    data.user.setUsingPotion(true);
                }
            }

        }


        else if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity use = (GPacketPlayClientUseEntity) packet;
            data.combat.setLastAttack(packet.getTimestamp());

            data.combat.setProcessAttack(true);
            final Entity entity = NMSManager.getInms().getEntity(player.getWorld(), use.getEntityId());
            if (entity != null) {
                if (entity instanceof Arrow) {
                    data.combat.setLastBowDamage(packet.getTimestamp());
                } else if (entity instanceof Player) {
                    final Player target = (Player) entity;
                    final PlayerData targetData = Artemis.v().getApi().getPlayerDataManager().getData(target);
                    if (targetData != null) {
                        targetData.combat.setLastDamage(packet.getTimestamp());
                    }
                }
            }
        }

        data.timing.interactTiming.addTime(now, System.currentTimeMillis());
    }
}
