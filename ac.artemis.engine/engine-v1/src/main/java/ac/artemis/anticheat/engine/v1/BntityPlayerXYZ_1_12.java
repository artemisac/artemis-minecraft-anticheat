package ac.artemis.anticheat.engine.v1;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.attribute.AttributeModifier;
import ac.artemis.core.v4.emulator.attribute.IAttributeInstance;
import ac.artemis.core.v4.emulator.attribute.RangedAttribute;
import ac.artemis.core.v4.emulator.attribute.map.BaseAttributeMap;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityTeleport;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerUpdateAttributes;

import java.util.stream.Collectors;

/**
 * @author Ghast
 * @since 13/08/2020
 * Artemis © 2020
 */
public class BntityPlayerXYZ_1_12 extends BntityPlayerXYZ {
    public BntityPlayerXYZ_1_12(PlayerData data) {
        super(data);
    }


    /**
     * Updates an entity's position and rotation as specified by the packet
     */
    @Override
    public void handlePacket(GPacketPlayServerEntityTeleport packet) {
        this.serverPosX = packet.getX();
        this.serverPosY = packet.getY();
        this.serverPosZ = packet.getZ();
        double d0 = (double) this.serverPosX / 4096.0D;
        double d1 = (double) this.serverPosY / 4096.0D;
        double d2 = (double) this.serverPosZ / 4096.0D;
        float f = (packet.getYaw() * 360) / 256.0F;
        float f1 = (packet.getPitch() * 360) / 256.0F;

        if (Math.abs(this.posX - d0) < 0.03125D && Math.abs(this.posY - d1) < 0.015625D && Math.abs(this.posZ - d2) < 0.03125D) {
            this.setPositionAndRotationDirect(this.posX, this.posY, this.posZ, f, f1, 0, true);
        } else {
            this.setPositionAndRotation2(d0, d1, d2, f, f1, 3, true);
        }

        this.onGround = packet.isOnGround();
    }

    /**
     * Updates en entity's attributes and their respective modifiers, which are used for speed bonusses (player
     * sprinting, animals fleeing, baby speed), weapon/tool attackDamage, hostiles followRange randomization, zombie
     * maxHealth and knockback resistance as well as reinforcement spawning chance.
     */
    public void handlePacket(GPacketPlayServerUpdateAttributes packet) {
        BaseAttributeMap baseattributemap = this.getAttributeMap();

        for (GPacketPlayServerUpdateAttributes.Snapshot snapshot : packet.getAttributes()) {
            IAttributeInstance iattributeinstance = baseattributemap.getAttributeInstanceByName(snapshot.getLocalName());

            if (iattributeinstance == null) {
                iattributeinstance = baseattributemap.registerAttribute(new RangedAttribute(null,
                        snapshot.getLocalName(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
            }



            iattributeinstance.setBaseValue(snapshot.getBaseValue());
            iattributeinstance.removeAllModifiers();

            for (AttributeModifier attributemodifier : snapshot.getModifiers().stream()
                    .map(e -> new AttributeModifier(e.getId(), e.getName(), e.getAmount(), e.getOperation()))
                    .collect(Collectors.toList())) {
                iattributeinstance.applyModifier(attributemodifier);
            }
        }
    }
}
