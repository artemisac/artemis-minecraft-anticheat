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

@Check(type = Type.AIM, var = "Kitten")
public class AimKitten extends ArtemisCheck implements PacketHandler {

    private double buffer;

    public AimKitten(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientLook) {
            final PacketPlayClientLook wrapper = (PacketPlayClientLook) packet;

            final long now = System.currentTimeMillis();

            final boolean action = now - data.combat.lastAttack < 250L
                    || now - data.user.lastPlace < 250L;
            final boolean teleporting = this.isExempt(ExemptType.TELEPORT);

            final double mcpSensitivity = data.sensitivity.sensitivityTableValue;

            if (mcpSensitivity < .001) return;

            final float f = (float) mcpSensitivity * 0.6F + 0.2F;
            final float gcd = f * f * f * 1.2F;

            final float yaw = wrapper.getYaw() % 180;
            final float pitch = wrapper.getPitch();

            final float adjustedYaw = (yaw - (yaw % gcd));
            final float adjustedPitch = (pitch - (pitch % gcd));

            final float yawDifference = Math.abs(yaw - adjustedYaw);
            final float pitchDifference = Math.abs(pitch - adjustedPitch);

            final float gcdYawDifference = Math.abs(gcd - yawDifference);
            final float gcdPitchDifference = Math.abs(gcd - pitchDifference);

            final double moduloX = gcdYawDifference % .001;
            final double moduloY = gcdPitchDifference % .001;

            if (moduloX < 1e-6 && moduloY < 1e-6) {
                if (action && !teleporting && ++buffer > 8) {
                    this.log("moduloX=" + moduloX + " moduloY=" + moduloY);
                    this.buffer *= .5;
                }
            } else {
                this.buffer = Math.max(0, buffer - .175);
            }

            this.debug(
                    String.format("moduloX=%f moduloY=%f buffer=%f diffX=%f diffY=%f gcdX=%f gcdY=%f",
                            moduloX,
                            moduloY,
                            buffer,
                            yawDifference,
                            pitchDifference,
                            gcdYawDifference,
                            gcdPitchDifference)
            );
        }
    }
}
