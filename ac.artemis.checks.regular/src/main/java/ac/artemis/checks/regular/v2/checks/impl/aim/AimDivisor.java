package ac.artemis.checks.regular.v2.checks.impl.aim;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientLook;

@Check(type = Type.AIM, var = "Divisor")
public class AimDivisor extends ArtemisCheck implements PacketHandler {

    private double buffer;
    private float lastYaw, lastPitch;

    public AimDivisor(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientLook) {
            final PacketPlayClientLook wrapper = (PacketPlayClientLook) packet;

            final float yaw = wrapper.getYaw();
            final float pitch = wrapper.getPitch();

            final float deltaYaw = Math.abs(yaw - lastYaw);
            final float deltaPitch = Math.abs(pitch - lastPitch);

            final double mcpSensitivity = data.sensitivity.sensitivityTableValue;

            final float f = (float) mcpSensitivity * 0.6F + 0.2F;
            final float gcd = f * f * f * 1.2F;

            final long now = System.currentTimeMillis();
            final boolean action = now - data.combat.lastAttack < 200L || now - data.user.lastPlace < 200L;

            final double deltaX = deltaYaw / gcd;
            final double deltaY = deltaPitch / gcd;

            final double roundedX = Math.abs(Math.round(deltaX) - deltaX);
            final double roundedY = Math.abs(Math.round(deltaY) - deltaY);

            final boolean invalidX = roundedX > .03 && roundedY < 1e-4;
            final boolean invalidY = roundedY > .03 && roundedX < 1e-4;

            if (action && (invalidX || invalidY)) {
                if (++buffer > 20) {
                    log("divisorX=" + roundedX + " divisorY=" + roundedY);
                    this.buffer *= .5;
                }
            } else {
                this.buffer = Math.max(0, buffer - .175);
            }

            this.lastYaw = yaw;
            this.lastPitch = pitch;
        }
    }
}

