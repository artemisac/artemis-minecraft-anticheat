package ac.artemis.checks.regular.v2.checks.impl.aim;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientLook;

@Check(type = Type.AIM, var = "Water")
public class AimWater extends ArtemisCheck implements PacketHandler {

    private double buffer;

    public AimWater(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientLook) {
            final PacketPlayClientLook wrapper = (PacketPlayClientLook) packet;

            final long now = System.currentTimeMillis();

            final boolean action = now - data.combat.lastAttack < 250L || now - data.user.lastPlace < 250L;
            final boolean teleporting = this.isExempt(ExemptType.TELEPORT);

            final double mcpSensitivity = data.sensitivity.sensitivityTableValue;

            if (mcpSensitivity < .001) return;

            final float f = (float) mcpSensitivity * 0.6F + 0.2F;
            final float gcd = f * f * f * 1.2F;

            final float yaw = wrapper.getYaw();
            final float pitch = wrapper.getPitch();

            final float adjustedYaw = (yaw - (yaw % gcd));
            final float adjustedPitch = (pitch - (pitch % gcd));

            final float yawDifference = Math.abs(yaw - adjustedYaw);
            final float pitchDifference = Math.abs(pitch - adjustedPitch);

            if (yawDifference == 0.0 || pitchDifference == 0.0) {
                if (action && !teleporting && ++buffer > 8) {
                    this.log("yawDifference=" + yawDifference + " pitchDifference=" + pitchDifference);
                    this.buffer *= .5;
                }
            } else {
                this.buffer = Math.max(0, buffer - .175);
            }

            this.debug(
                    String.format("adjX=%f adjY=%f buffer=%f diffX=%f diffY=%f",
                            adjustedYaw,
                            adjustedPitch,
                            buffer,
                            yawDifference,
                            pitchDifference)
            );
        }
    }
}
