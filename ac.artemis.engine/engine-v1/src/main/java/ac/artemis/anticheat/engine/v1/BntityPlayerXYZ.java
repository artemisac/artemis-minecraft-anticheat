package ac.artemis.anticheat.engine.v1;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.attribute.AttributeModifier;
import ac.artemis.core.v4.emulator.attribute.IAttributeInstance;
import ac.artemis.core.v4.emulator.attribute.RangedAttribute;
import ac.artemis.core.v4.emulator.attribute.map.BaseAttributeMap;
import ac.artemis.core.v4.emulator.entity.utils.PlayerControls;
import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v5.emulator.Emulator;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityTeleport;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerExplosion;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerPosition;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerUpdateAttributes;

import java.util.stream.Collectors;

/**
 * @author Ghast
 * @since 13/08/2020
 * Artemis © 2020
 */
public class BntityPlayerXYZ extends BntityPlayerSP implements Emulator {
    public BntityPlayerXYZ(PlayerData data) {
        super(data);
    }

    /**
     * Handles changes in player positioning and rotation such as when travelling to a new dimension, (re)spawning,
     * mounting horses etc. Seems to immediately reply to the server with the clients post-processing perspective on the
     * player positioning
     */
    public void handlePacket(GPacketPlayServerPosition packetIn) {
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        float f = packetIn.getYaw();
        float f1 = packetIn.getPitch();

        if (packetIn.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.X)) {
            d0 += this.posX;
            //System.out.println("MotionX!");
        }
        else {
            this.motionX = 0.0D;
        }

        if (packetIn.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.Y)) {
            d1 += this.posY;
            //System.out.println("MotionY!");
        }
        else {
            this.motionY = 0.0D;
        }

        if (packetIn.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.Z)) {
            //System.out.println("MotionZ!");
            d2 += this.posZ;
        }
        else {
            this.motionZ = 0.0D;
        }

        if (packetIn.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.X_ROT)) {
            f1 += this.rotationPitch;
        }

        if (packetIn.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.Y_ROT)) {
            f += this.rotationYaw;
        }

        this.setPositionAndRotation(d0, d1, d2, f, f1);
    }

    @Override
    public boolean isLastSprinting() {
        return lastSprinting;
    }

    @Override
    public void setWasPos(boolean value) {
        this.wasPos = value;
    }

    @Override
    public void setMaxDistance(double value) {
        return;
    }

    @Override
    public void setLastSprinting(boolean sprinting) {
        this.lastSprinting = sprinting;
    }

    @Override
    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

    /**
     * Updates an entity's position and rotation as specified by the packet
     */
    public void handlePacket(GPacketPlayServerEntityTeleport packet) {
        this.serverPosX = packet.getX();
        this.serverPosY = packet.getY();
        this.serverPosZ = packet.getZ();
        double d0 = (double) this.serverPosX / 32.0D;
        double d1 = (double) this.serverPosY / 32.0D;
        double d2 = (double) this.serverPosZ / 32.0D;
        float f = (packet.getYaw() * 360) / 256.0F;
        float f1 = (packet.getPitch() * 360) / 256.0F;

        if (Math.abs(this.posX - d0) < 0.03125D && Math.abs(this.posY - d1) < 0.015625D && Math.abs(this.posZ - d2) < 0.03125D) {
            this.setPositionAndRotation2(this.posX, this.posY, this.posZ, f, f1, 3, true);
        } else {
            this.setPositionAndRotation2(d0, d1, d2, f, f1, 3, true);
        }

        this.onGround = packet.isOnGround();
    }

    /**
     * Updates en entity's attributes and their respective modifiers, which are used for speed bonuses (player
     * sprinting, animals fleeing, baby speed), weapon/tool attackDamage, hostiles followRange randomization, zombie
     * maxHealth and velocity resistance as well as reinforcement spawning chance.
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

    @Override
    public void setChunkLoaded(boolean value) {

    }

    @Override
    public void setGround(boolean value) {
        this.onGround = value;
    }

    @Override
    public void setServerPosition(double x, double y, double z) {

    }

    @Override
    public boolean isAttributeSprinting() {
        return this.isSprinting();
    }

    /**
     * Initiates a new explosion (sound, particles, drop spawn) for the affected blocks indicated by the packet.
     */
    public void handleExplosion(GPacketPlayServerExplosion packetIn) {
        this.motionX += packetIn.getMotionX();
        this.motionY += packetIn.getMotionY();
        this.motionZ += packetIn.getMotionZ();
    }

    @Override
    public void setLastPositionPrevious(PlayerMovement playerMovementIn) {
        this.lastPositionPrevious = playerMovementIn;
    }

    @Override
    public void setLastRotationPrevious(PlayerMovement playerMovementIn) {
        this.lastRotationPrevious = playerMovementIn;
    }

    @Override
    public void setPreviousGround(boolean previousGroundIn) {
        this.previousGround = previousGroundIn;
    }

    @Override
    public PlayerControls getPlayerControls() {
        return playerControls;
    }

    @Override
    public float getMoveStrafing() {
        return moveStrafing;
    }

    @Override
    public float getMoveForward() {
        return moveForward;
    }

    @Override
    public int getJumpTicks() {
        return jumpTicks;
    }

    @Override
    public void setNoClip(boolean noClipIn) {
        this.noClip = noClipIn;
    }

    @Override
    public void setJumpTicks(int ticks) {
        this.jumpTicks = ticks;
    }
}
