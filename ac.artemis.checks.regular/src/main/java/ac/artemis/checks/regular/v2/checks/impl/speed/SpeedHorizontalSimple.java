package ac.artemis.checks.regular.v2.checks.impl.speed;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.TeleportHandler;
import ac.artemis.core.v4.check.VelocityHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Drop;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.potion.Potion;
import ac.artemis.core.v4.emulator.potion.PotionEffect;
import ac.artemis.core.v4.utils.blocks.BlockUtil;
import ac.artemis.core.v4.utils.position.ModifiableFlyingLocation;
import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.utils.MathUtil;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.nms.MathHelper;

import java.util.Arrays;

/**
 * @author Ghast
 * @since 25/03/2021
 * Artemis © 2021
 */

@Check(type = Type.SPEED, var = "Horizontal", threshold = 25)
@Drop(decay = 400)
public class SpeedHorizontalSimple extends ArtemisCheck implements VelocityHandler, PacketHandler, TeleportHandler {

    private double motionX, motionZ;
    private double buffer = 0;

    private Velocity velocity;

    public SpeedHorizontalSimple(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            final PacketPlayClientFlying wrapper = ((PacketPlayClientFlying) packet);

            if (wrapper.isPos()) {
                // Modern versions exist. Unfortunately.
                final boolean fucked = data.getVersion().isOrAbove(ProtocolVersion.V1_13);

                // Check if the player was on ground the time player was moving.
                final boolean onGround = data.prediction.isGround();
                final boolean lastOnGround = data.prediction.isLastGround();

                // Grab our position offsets.
                final double deltaX = data.prediction.getDeltaX();
                final double deltaY = data.prediction.getDeltaY();
                final double deltaZ = data.prediction.getDeltaZ();

                // Grab the current position offset.
                final double deltaXZ = MathUtil.hypot(data.prediction.getDeltaX(), data.prediction.getDeltaZ());

                // These values are directly from NMS, quite useful most the time, these have to be improved nonetheless.
                // Check if the user was on ground before as we're a tick behind since we're predicting the position.
                // Apply the block slipperiness to the friction.
                final double remove = fucked ? 0.5000001D : 1.D;

                // Get the base friction of the game which also happens to be the air friction.
                float friction = 0.91F;
                final float slipperiness = this.getFrictionAtBB(remove);

                if (lastOnGround) {
                    friction *= slipperiness;
                }

                // Create a limit for the maximum offset.
                double movementSpeed = this.getSpeed(true);

                // Apply air and ground math depending on the current position.
                if (lastOnGround) {
                    // Apply ground movement factors.
                    movementSpeed *= fucked
                            ? 0.21600002F / (slipperiness * slipperiness * slipperiness)
                            : 0.16277136F / (friction * friction * friction);

                    // If the player jumped compensate for it.
                    if (!onGround && deltaY >= 0.0D) {
                        // Get the jump boost and add it to our limit.
                        movementSpeed += 0.2D;
                    }
                } else {
                    // If the player is on air get the max sprint speed.
                    movementSpeed = (float) ((double) 0.02F + (double) 0.02F * 0.3D);
                }

                /*
                 * As a velocity sets our motion on NetHandlerPlayClient we are just
                 * going to apply it here as this would be the proper time for it.
                 */
                if (this.velocity != null) {
                    this.motionX = this.velocity.getX();
                    this.motionZ = this.velocity.getZ();

                    this.velocity = null;
                }

                /*
                 * As our movement in the game is (motion * friction) + moveFlying we can simply apply friction and
                 * this would be our motion slowing down. We can actually grab the difference between our current
                 * speed and motion * friction and the offset between these can be maximum movelFlying. As x / x = 1
                 * we can assume this result will never be more than 1, so we can just exactly check for that.
                 */
                final double acceleration = (deltaXZ - (MathUtil.hypot(this.motionX, this.motionZ))) / movementSpeed;

                /*
                 * These are some scenarios our check will not work. They are either exempted because it is not handled
                 * in here and isn't worth handling or is generally inexploitable so that we can just return instead of accounting.
                 */
                final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.SLIME, ExemptType.PISTON, ExemptType.RESPAWN,
                        ExemptType.FLIGHT, ExemptType.GAMEMODE, ExemptType.WORLD, ExemptType.VEHICLE, ExemptType.LIQUID);

                final boolean joined = this.isExempt(ExemptType.JOIN) && Math.abs(data.prediction.getDeltaY() - 0.098F) < 1E-5;

                /*
                 * As explained above we can check if the acceleration is more than 1 here with a bit of leniency.
                 * Thanks to situations like 0.03 and other factors our check might false whilst slow, so we can just
                 * check if the player is actually moving at enough speed to be flagged. This will not cause any bypasses
                 * as worst case scenario is that someone makes a slower than 0.2 speed which is completely pointless.
                 */
                final boolean invalid = acceleration > 1.0 + 1E-6 && deltaXZ > 0.2D;

                if (invalid && !exempt && !joined) {
                    /*
                     * We increment our buffer here. We want to make sure to limit how high can our buffer go as
                     * we do not want it to overflow, and we want to avoid the buffer going too high as if the check
                     * somehow false flagged to a high level this will make sure it can decay down quickly.
                     *
                     * And we want to limit how much can we increment at once as stuff like velocity being
                     * handled on the wrong tick can happen, and we do not want this number going up fast.
                     */
                    this.buffer = Math.min(12, this.buffer + Math.min(3.0D, acceleration));

                    if (this.buffer > 9.0D) {
                        this.log(
                                new Debug<>("acceleration", acceleration),
                                new Debug<>("exempt", Arrays.toString(exemptTypes()))
                        );
                    }
                } else {
                    this.buffer = Math.max(0, this.buffer - 0.05);
                }

                this.motionX = deltaX * friction;
                this.motionZ = deltaZ * friction;

                /*
                 * In post 1.8.9 versions the place where the movement is smaller than a certain
                 * value is rounded this value is 0.003D and for pre 1.9 versions it is 0.005D.
                 */
                final double minimum = data.getVersion().isOrAbove(ProtocolVersion.V1_9) ? 0.003D : 0.005D;

                if (Math.abs(this.motionX) < minimum) this.motionX = 0.0D;
                if (Math.abs(this.motionZ) < minimum) this.motionZ = 0.0D;
            }
        }
    }

    @Override
    public void handle(final Velocity velocity) {
        this.velocity = velocity;

        this.debug("Velocity");
    }

    @Override
    public void handle(final ModifiableFlyingLocation confirmedLocation) {
        this.buffer = 0.0D;
    }

    public float getFrictionAtBB(final double remove) {
        final int x = MathHelper.floor(data.prediction.getLastX());
        final int y = MathHelper.floor((MathHelper.floor(data.prediction.getLastY()) - remove));
        final int z = MathHelper.floor(data.prediction.getLastZ());

        final Block block = data.getEntity().getWorld().getBlockAt(x, y, z);

        return block == null || block.getMaterial() == null
                ? 0.91F
                : BlockUtil.getSlipperiness(block.getMaterial());
    }

    private double getSpeed(final boolean sprinting) {
        // Speed is multiplied by 2 for some reason. Thanks CraftBukkit. Fuck you.
        double speed = data.getPlayer().getWalkSpeed() / 2.F;

        if (sprinting)
            speed *= 1.3F;

        final PotionEffect effect = data.entity.getActivePotionEffect(Potion.moveSpeed);

        if (effect != null) {
            speed *= 1.F + .2F * (effect.getAmplifier() + 1);
        }

        return speed;
    }

    private boolean almost(final double a, final double b) {
        return Math.abs(a - b) < 1E-3;
    }
}
