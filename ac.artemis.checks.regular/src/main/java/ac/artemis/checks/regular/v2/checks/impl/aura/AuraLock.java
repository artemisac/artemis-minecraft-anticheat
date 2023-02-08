package ac.artemis.checks.regular.v2.checks.impl.aura;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.core.v5.utils.MathUtil;
import ac.artemis.core.v5.utils.OldMathUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientPositionLook;

@Check(type = Type.AURA, var = "Lock")
public class AuraLock extends ArtemisCheck implements PacketHandler {

    private float lastYaw, lastPitch;
    private double lastPosX, lastPosZ, lastHorizontalDistance;

    @Setting(type = CheckSettings.MIN_DELTA_ACCELERATION_YAW, defaultValue = "25.d")
    private final double minimumDeltaYaw = info.getSetting(CheckSettings.MIN_DELTA_ACCELERATION_YAW).getAsDouble();

    @Setting(type = CheckSettings.MIN_DELTA_ACCELERATION_PITCH, defaultValue = "10.d")
    private final double minimumDeltaPitch = info.getSetting(CheckSettings.MIN_DELTA_ACCELERATION_PITCH).getAsDouble();

    public AuraLock(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientPositionLook) {
            final PacketPlayClientPositionLook wrapper = (PacketPlayClientPositionLook) packet;

            final float yaw = wrapper.getYaw();
            final float pitch = wrapper.getPitch();

            final double posX = wrapper.getX();
            final double posZ = wrapper.getZ();

            final boolean valid = yaw != lastYaw && pitch != lastPitch && posX != lastPosX && posZ != lastPosZ
                    && !TimeUtil.elapsed(data.combat.getLastAttack(), 250L);

            if (valid) {
                final float deltaYaw = Math.abs(yaw - lastYaw);
                final float deltaPitch = Math.abs(pitch - lastPitch);

                final double horizontalDistance = MathUtil.hypot(posX - lastPosX, posZ - lastPosZ);
                final double acceleration = Math.abs(horizontalDistance - lastHorizontalDistance);

                if (deltaYaw > minimumDeltaYaw && deltaPitch > minimumDeltaPitch && acceleration < 1e-04) {
                    this.log("dy=" + deltaYaw + " dp=" + deltaPitch + " a=" + acceleration);
                }

                this.lastHorizontalDistance = horizontalDistance;
            }

            this.lastPosX = posX;
            this.lastPosZ = posZ;
            this.lastYaw = yaw;
            this.lastPitch = pitch;
        }
    }
}
