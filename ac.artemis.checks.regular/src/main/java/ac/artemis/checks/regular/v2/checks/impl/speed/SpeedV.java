package ac.artemis.checks.regular.v2.checks.impl.speed;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.VelocityHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.move.Motion;
import ac.artemis.core.v4.emulator.potion.Potion;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.*;

/**
 * @author Ghast
 * @since 25/03/2021
 * Artemis © 2021
 */

@Check(type = Type.SPEED, var = "Vertical", threshold = 25)
@Experimental
public class SpeedV extends ArtemisCheck implements VelocityHandler, PacketHandler {
    public SpeedV(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private double motionY;

    private float buffer = 0;

    @Override
    public void handle(GPacket packet) {
        /*
         * In here we handle all of the movement and so and forth. This is considered to be the heart of this
         * system. More particularly, it represents the onTick formula
         */
        if (packet instanceof PacketPlayClientFlying) {

            /*
             * We set our basic values for this as I needed to use many different packets to account for
             * velocity and teleports
             */
            final PlayerPosition from = data.prediction.getLastPosition();
            final PlayerPosition to = data.prediction.getPosition();

            final Point next = to.toPoint();

            final double deltaY = to.getY() - from.getY();

            /*
             * Since we're looping through a bunch of stuff we need to have a smallest motion defined
             */
            Motion smallest = new Motion(999,999,999, 0.0F, 0.0F, false, false);
            double shortestDistance = 999;


            /*
             * LOOP #1 - Jumping
             * You may either desire to make a gravity check or whatnot, but this is essentially super important
             * for proper jumping. There's too many factors that can screw with a normal jump accounting, so it's
             * best to actually have it looped
             */
            for (int jump = 0; jump < 2; jump++) {
                final boolean jumping = jump == 1;

                /*
                 * Here we get the estimated maximum flying motion and compare it to the shortest.
                 * Works roughly decently. When it doesn't it still doesn't go above 1.0. Note to mention
                 * this is exclusively on an XZ basis
                 */
                final Motion motion = this.getMoveOffset(jumping);

                final Point distance = motion.toPoint();
                final double motionDistance = Math.abs((from.getY() + distance.getY()) - next.getY());

                if (motionDistance < shortestDistance) {
                    smallest = motion;
                    shortestDistance = motionDistance;
                }
            }

            /*
             * Essentially this isn't really needed since we're doing XZ but can come in practical if in
             * need to patch a low hop or something. This checks from ground too. Pretty kewl.
             */
            final Point point = to.toPoint().addVector(0, -0.5, 0);
            final Block block = data.entity.getWorld().getBlockAt(point.getBlockX(), point.getBlockY(), point.getBlockZ());
            if (Math.abs(deltaY) < 1E-9D || (deltaY > 0.5999D && deltaY < 0.601D) && block != null && block.getMaterial().getMaterial().isSolid()) {
                smallest.setY(0.0D);
                this.debug("Bad y, deltaY=" + deltaY + " smallestY=" + smallest.getY());
            }

            /*
             * Here we make two vectors of the distance travelled and debug it.
             */
            final Point delta = new Point(0.0F, deltaY, 0.0F);
            final Point predict = new Point(smallest.getX(), smallest.getY(), smallest.getZ());

            this.debug(smallest.toString());

            log: {
                /*
                 * Here we do a casual comparison. We add 0.0005 to prevent an invalid numerical operation.
                 * That's about it really. Not much to comment on.
                 */
                final double movementSpeed = Math.abs(delta.getY() - predict.getY());

                this.debug("speed=" + movementSpeed + " buffer=" + buffer + " y=" + delta.getY() + " motionY=" + predict.getY());

                if (movementSpeed <= 1E-4) {
                    this.buffer = Math.max(buffer - 1, 0);
                    break log;
                }

                this.buffer = Math.min(500, buffer + 10); //We do this to prevent integer overflow.

                if (buffer > 60) {
                    this.log(
                            new Debug<>("speed", movementSpeed),
                            new Debug<>("buffer", buffer),
                            new Debug<>("gotY", delta.getY()),
                            new Debug<>("expectedY", predict.getY())
                    );

                    this.buffer /= 2;
                }
            }

            /*
             * We update the motion with out smallest
             */
            this.motionY = smallest.getY();

            /*
             * Hi. This is gravity. It likes to pull shit down. Say hi to gravity people.
             */
            this.motionY -= 0.08D;

            /*
             * And just the casual frictional forces.
             */
            this.motionY *= 0.98D;
        }
    }

    @Override
    public void handle(Velocity velocity) {
        /*
         * Here we handle confirmed velocity. This is a cool system I implemented. I need to improve it
         * slightly nonetheless.
         */
        this.motionY = velocity.getY();

        this.debug("Velocity");
    }

    private Motion getMoveOffset(boolean jumping) {
        final boolean water = false;
        final boolean lava = false;
        final boolean flying = false;
        final boolean fucked = data.getVersion().isOrAbove(ProtocolVersion.V1_13);

        /*
         * We create a new motion with all the factors, including the ones we bruteforce
         * this will allow for us to properly handle movement.
         */
        final Motion motion = new Motion(0.0F, motionY, 0.0F, 0.0F, 0.0F, jumping, false);

        if (jumping) {
            motion.setY(0.42f);

            if (data.entity.isPotionActive(Potion.jump)) {
                motion.addY((float)(data.entity.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
            }
        }

        return motion;
    }

    private boolean almost(double a, double b) {
        return Math.abs(a - b) < 1E-3;
    }
}
