package ac.artemis.checks.regular.v2.checks.impl.aim;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientLook;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;

/**
 * @author Ghast
 * @since 31/08/2020
 * Artemis © 2020
 */

@Check(type = Type.AIM, var = "Liquid")
@Experimental
public class AimLiquid extends ArtemisCheck implements PacketHandler, PacketExcludable {

    public AimLiquid(final PlayerData data, final CheckInformation info) {
        super(data, info);
        this.setCompatiblePackets(
                GPacketPlayClientPositionLook.class,
                PacketPlayClientLook.class
        );
    }

    private float lastDeltaPitch;
    private float lastDeltaYaw;
    private float lastRotationYaw;
    private int buffer;

    @Override
    public void handle(final GPacket packet) {
        final PacketPlayClientLook wrapper = (PacketPlayClientLook) packet;

        final float sensitivityX = (float) data.sensitivity.sensitivityX;
        final float sensitivityY = (float) data.sensitivity.sensitivityY;

        final float rotationYaw = wrapper.getYaw();
        final float rotationPitch = wrapper.getPitch();

        final float differenceYaw = (float) MathUtil.distanceBetweenAngles(lastRotationYaw, rotationYaw);

        final float gcd = MathUtil.getGcd(
                (long) (rotationYaw * MathUtil.EXPANDER),
                (long) (rotationPitch * MathUtil.EXPANDER)
        );

        final float clampedYaw = modulo(sensitivityX, rotationYaw);
        final float clampedPitch = modulo(sensitivityY, rotationPitch);

        final float deltaYaw = Math.abs(clampedYaw - rotationYaw);
        final float deltaPitch = Math.abs(clampedPitch - rotationPitch);

        final float subtractedYaw = Math.abs(deltaYaw - lastDeltaYaw);
        final float subtractedPitch = Math.abs(deltaPitch - lastDeltaPitch);

        if (deltaYaw < 1e-05 && deltaPitch < 1e-05 && subtractedYaw < 0.01 && subtractedPitch < 0.01) {
            buffer += 4;

            if (buffer > 20) {
                log("subtracted=" + subtractedPitch);
            }
        } else {
            buffer = Math.max(buffer - 1, 0);
        }


        this.debug("subtracted=" + subtractedPitch + "deltaP=" + deltaPitch + " deltaY=" + deltaYaw + " gcd=" + gcd
                + " rotY=" + differenceYaw + " rotP=" + rotationPitch + " buffer=" + buffer);
        this.lastDeltaPitch = deltaPitch;
        this.lastRotationYaw = deltaYaw;
        this.lastRotationYaw = rotationYaw;
    }

    private static float modulo(final float sensitivity, final float angle) {
        final float f = (sensitivity * 0.6f + .2f);
        final float f2 = f * f * f * 1.2f;

        return angle - (angle % f2);
    }
}
