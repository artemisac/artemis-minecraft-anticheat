package ac.artemis.anticheat.engine.v1;

import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.attribute.AttributeModifier;
import ac.artemis.core.v4.emulator.attribute.IAttributeInstance;
import ac.artemis.core.v4.emulator.attribute.RangedAttribute;
import ac.artemis.core.v4.emulator.attribute.map.BaseAttributeMap;
import ac.artemis.core.v4.emulator.moderna.*;
import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v5.utils.raytrace.Point;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityTeleport;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerUpdateAttributes;

import java.util.stream.Collectors;

/**
 * @author Ghast
 * @since 13/08/2020
 * Artemis © 2020
 */
public class BntityPlayerXYZ_1_15 extends BntityPlayerXYZ {
    public BntityPlayerXYZ_1_15(PlayerData data) {
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
    @Override
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

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    @Override
    public void jump() {
        float f = 0.42F; // Todo add support for honey blocks
        if (data.getPlayer().hasEffect(PotionEffectType.JUMP)) {
            // TODO Do the potion thingy instead of relying on this old hag's shit
            this.motionY += ((float) (getJumpBoostAmplifier(data.getPlayer()))) * 0.1F;
        }

        Velocity vector3d = this.getMotion();
        this.setVelocity(vector3d.getX(), f, vector3d.getY());
        if (this.playerControls.isSprint()) { // Artemis desync fix
            float f1 = this.rotationYaw * ((float) Math.PI / 180F);
            this.setVelocity(this.getMotion().add(-ModernaMathHelper.sin(f1) * 0.2F, 0.0D, ModernaMathHelper.cos(f1) * 0.2F));
        }

        this.isAirBorne = true;
    }



    @Override
    protected void moveEntity(double x, double y, double z) {
        if (true) {
            super.moveEntity(x, y, z);
            return;
        }
    }

    /**
     * Taken directly from NMS. This is what updates properly the MotionXZ of a data
     *
     * @param strafe   Horizontal (to the data) movement based on the Euler Axis
     * @param forward  Vertical (to the data) movement based on the Euler Axis
     * @param friction Friction of the current data
     */
    @Override
    protected void moveFlying(float strafe, float forward, float friction) {
        Point point = new Point(strafe, 0.D, forward);
        final double d0 = point.lengthSquared();
        if (d0 < 1.0E-7D) {
            return;
        } else {
            Point vector3d = (d0 > 1.0D ? point.normalize() : point).scale(friction);
            float f = ModernaMathHelper.sin(rotationYaw * ((float)Math.PI / 180F));
            float f1 = ModernaMathHelper.cos(rotationYaw * ((float)Math.PI / 180F));

            this.motionX += vector3d.getX() * (double)f1 - vector3d.getZ() * (double)f;
            this.motionZ += vector3d.getZ() * (double)f1 + vector3d.getX() * (double)f;
        }
    }
}
