package ac.artemis.checks.regular.v2.checks.impl.aim.cinematic;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.rotation.SimpleRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.SimpleRotation;
import ac.artemis.core.v5.utils.MathUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientFlying;

@Check(type = Type.AIM, var = "Cinematic2")
public final class Cinematic2 extends SimpleRotationCheck implements PacketHandler {

    private float lastDeltaYaw, lastDeltaPitch;

    private static final double CINEMATIC_CONSTANT = 0.0078125F;

    private int lastCinematicTicks, cinematicTicks;
    private int tick;

    public Cinematic2(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handleRotation(final SimpleRotation from, final SimpleRotation to) {
        final float deltaYaw = Math.abs(to.getYawRaw() - from.getYawRaw());
        final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

        handle: {
            //Fixes exploits
            if (deltaPitch == 0F || deltaYaw == 0F) break handle;

            final float yawAccel = Math.abs(deltaYaw - this.lastDeltaYaw);
            final float pitchAccel = Math.abs(deltaPitch - this.lastDeltaPitch);

            final boolean invalid = MathUtil.isExponentiallySmall(yawAccel) || yawAccel == 0F
                    || MathUtil.isExponentiallySmall(pitchAccel) || pitchAccel == 0F;

            /*
             * Grab the GCD for the player on both the pitch and yaw. Thanks to our utility only taking
             * in longs we are going to expand the rotations before giving them as a parameter.
             */
            final double constantYaw = ac.artemis.core.v4.utils.maths.MathUtil.getGcd((long) (deltaYaw * ac.artemis.core.v4.utils.maths.MathUtil.EXPANDER), (long) (lastDeltaYaw * ac.artemis.core.v4.utils.maths.MathUtil.EXPANDER));
            final double constantPitch = ac.artemis.core.v4.utils.maths.MathUtil.getGcd((long) (deltaPitch * ac.artemis.core.v4.utils.maths.MathUtil.EXPANDER), (long) (lastDeltaPitch * ac.artemis.core.v4.utils.maths.MathUtil.EXPANDER));

            final boolean cinematic = !invalid && yawAccel < 1F && pitchAccel < 1F;

            if (cinematic) {
                if (constantYaw < CINEMATIC_CONSTANT && constantPitch < CINEMATIC_CONSTANT) this.cinematicTicks++;
            } else this.cinematicTicks -= this.cinematicTicks > 0 ? 1 : 0;

            this.cinematicTicks -= this.cinematicTicks > 5 ? 1 : 0;

            data.combat.setCinematic2(this.cinematicTicks > 2 || this.tick - this.lastCinematicTicks < 80);

            if (data.combat.isCinematic2 && this.cinematicTicks > 3) this.lastCinematicTicks = this.tick;
        }

        this.lastDeltaYaw = deltaYaw;
        this.lastDeltaPitch = deltaPitch;
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientFlying) {
            ++this.tick;
        }
    }
}
