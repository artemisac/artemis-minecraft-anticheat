package ac.artemis.checks.regular.v2.checks.impl.aura;

import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.packet.wrapper.client.PacketPlayClientPosition;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

@Check(type = Type.AURA, var = "Acceleration")
public class AuraAcceleration extends ArtemisCheck implements PacketHandler {
    private Entity attacked = null;
    private double lastPosX, lastPosZ, flyingMotionX, flyingMotionZ, lastDeltaXZ, buffer;

    public AuraAcceleration(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = (GPacketPlayClientUseEntity) packet;

            if (wrapper.getType() == PlayerEnums.UseType.ATTACK && attacked instanceof Player) {
                attacked = NMSManager.getInms()
                        .getEntity(data.getPlayer().getWorld(), wrapper.getEntityId());
            }
        }

        else if (packet instanceof PacketPlayClientPosition) {
            final PacketPlayClientPosition wrapper = (PacketPlayClientPosition) packet;

            final double posX = wrapper.getX();
            final double posZ = wrapper.getZ();

            final double motionX = Math.abs(posX - lastPosX);
            final double motionZ = Math.abs(posZ - lastPosZ);

            if (attacked != null && !attacked.isDead() && data.user.isSprinting()) {
                final double predictedX = flyingMotionX * 0.6;
                final double predictedZ = flyingMotionZ * 0.6;

                final double deltaX = Math.abs(predictedX - motionX);
                final double deltaZ = Math.abs(predictedZ - motionZ);

                final double deltaXZ = deltaX + deltaZ;
                final double acceleration = Math.abs(deltaXZ - lastDeltaXZ);

                if (acceleration < 0.01) {
                    if (deltaX > 0.089 || deltaZ > 0.089) {
                        if (++buffer > 8) {
                            log("a=" + acceleration + " dx=" + deltaX + " dz=" + deltaZ);
                        }
                    } else {
                        buffer = Math.max(buffer - 1, 0);
                    }
                } else {
                    buffer = Math.max(buffer - 0.5, 0);
                }

                this.lastDeltaXZ = deltaXZ;
                this.debug("a=" + acceleration + " dx=" + deltaX + " dz=" + deltaZ);

                this.attacked = null;
            }

            this.lastPosX = posX;
            this.lastPosZ = posZ;
            this.flyingMotionX = motionX;
            this.flyingMotionZ = motionZ;
        }
    }
}
