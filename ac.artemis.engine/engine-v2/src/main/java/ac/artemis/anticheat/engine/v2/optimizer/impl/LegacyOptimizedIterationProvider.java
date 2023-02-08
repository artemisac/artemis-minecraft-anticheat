package ac.artemis.anticheat.engine.v2.optimizer.impl;

import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.anticheat.engine.v2.optimizer.OptimizedIterationProvider;
import ac.artemis.anticheat.engine.v2.optimizer.OptimizedIterationResult;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.data.holders.CombatHolder;
import ac.artemis.core.v4.data.holders.PredictionHolder;
import ac.artemis.core.v4.data.holders.ReachHolder;
import ac.artemis.core.v4.data.holders.UserHolder;
import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.core.v5.utils.PlayerUtil;
import ac.artemis.core.v5.utils.raytrace.Point;
import lombok.Getter;

import java.util.function.Function;

/**
 * This optimized iteration provider takes information sent to the server by the client and creates the best
 * case scenario for the emulator iteration bruteforce loop. This is the best compromise between an impossible non-iterative
 * perfect emulator and an iterative bruteforce emulator. The optimized iteration usually takes around 1-60 iterations
 * most of the time, however the worst case is 576 iterations, which is almost never achieved. Before the optimized
 * iteration provider, the amount of iterations would always be 576. Adding a micro-optimization that would break the loop
 * on the first "accurate enough" emulation result would reduce the iterations, however they would still usually hang around
 * 200-400 iterations.
 */
public class LegacyOptimizedIterationProvider implements OptimizedIterationProvider {

    // This is the calculated move angle, set by vector math. It determines the best case forward and strafe iteration order.
    private double calculatedMoveAngle = 999.0D;
    private ArtemisData data;

    @Override
    public OptimizedIterationResult provide(final ArtemisData emulator) {
        final PlayerData playerData = emulator.getPlayer();

        final PredictionHolder tracker = playerData.getPrediction();
        final CombatHolder combat = playerData.getCombat();
        final ReachHolder reach = playerData.getReach();
        final UserHolder user = playerData.getUser();

        this.data = emulator;

        /*
         * The calculated move angle is used to get the best case iteration order for the forward and strafe values.
         * The calculation is not perfectly accurate and can be improved, however for the purpose of getting forward
         * and strafe values it is reasonably accurate.
         */
        this.calculatedMoveAngle = this.getCalculatedMoveAngle(
                tracker.getYaw(),
                tracker.getDeltaX(),
                tracker.getDeltaZ()
        );

        // Get the best case iteration order for the move forward value.
        final byte[] optimizedForwardIteration = this.getBestGuessForwardIterationOrder();

        // Get the best case iteration order for the move strafe value.
        final byte[] optimizedStrafeIteration = this.getBestGuessStrafeIterationOrder();

        // TODO: PlayerUtil method may be scuffed?
        // Get the best case iteration order for the jump status.
        final boolean[] optimizedJumpIteration = this.getBestGuessJumpIterationOrder(tracker.getDeltaY(),
                PlayerUtil.getPotionLevel(playerData.getPlayer(), PotionEffectType.JUMP));

        /*
         * Because of client de-sync, this "best case iteration order" may not actually be the best case.
         * However the best case iteration provider will still rely on what the client provides as the sprinting status
         * because the chance of desync should be reasonably low.
         */
        final boolean[] optimizedSprintIteration = user.isSprinting() ? new boolean[]{true, false} : new boolean[]{false, true};

        /*
         * When the client is using an item the strafe and forward is multiplied by (false.2? Can't remember the correct value).
         * Again, this may be incorrect, which is why we are providing the best case iteration since the provider cannot be correct true00%
         * of the time and always provide the best values.
         */
        final boolean[] optimizedUsingIteration = user.isUsingItem()
                ? new boolean[]{true, false}
                : new boolean[]{false, true};

        /*
         * The client experiences hit slowdown when they attack an entity. It may be beneficial to check if the last attacked was a player as well
         * however I will leave it how it is now. Basically if they attacked in the last true000ms then we will provide the best case as they
         * attacked and then not attacked.
         */
        final boolean[] optimizedAttackIteration = System.currentTimeMillis() - combat.getLastAttack() < 1000L
                ? new boolean[]{true, false}
                : new boolean[]{false, true};

        /*
         * The optimized velocity iteration takes into consideration if the client took velocity recently. Again this may not be 100% correct
         * however that's fine since this is only trying to provide a best case iteration order with a reasonable accuracy.
         */
        final boolean[] optimizedVelocityIteration = tracker.getQueuedVelocity().size() > 0
                ? new boolean[]{true, false}
                : new boolean[]{false, true};

        /*
         * The optimized ground iteration takes into consideration if the user has sent back a ground status or not
         */
        final boolean[] optimizedGroundIteration = playerData.prediction.isLastGround() ? new boolean[]{true, false} : new boolean[]{false, true};

        // Not too sure about this one.
        final boolean[] optimizedWorldCompensationIteration = user.isDigging() || user.isPlaced() ? new boolean[]{true, false} : new boolean[]{false, true};

        /*
         * Missed flying iteration ensures the player hasn't potentially missed a flying packet and isn't being too naggy
         * about it
         */
        final boolean[] optimizedMissedFlyingIteration = !playerData.prediction.isLastPos() && playerData.prediction.isPos()
                ? new boolean[]{true, false}
                : new boolean[]{false, true};

        // Return the new optimized iteration result, which will be provided to the emulation iteration loop.
        return new OptimizedIterationResult()
                .setForwardIteration(optimizedForwardIteration)
                .setStrafeIteration(optimizedStrafeIteration)
                .setJumpIteration(optimizedJumpIteration)
                .setSprintIteration(optimizedSprintIteration)
                .setUsingIteration(optimizedUsingIteration)
                .setAttackIteration(optimizedAttackIteration)
                .setGroundIteration(optimizedGroundIteration)
                .setVelocityIteration(optimizedVelocityIteration)
                .setWorldCompensationIteration(optimizedWorldCompensationIteration)
                .setMissedFlyingIteration(optimizedMissedFlyingIteration);
    }

    private double getCalculatedMoveAngle(final float yaw, final double deltaX, final double deltaZ) {
        // Credit to Islandscout, thanks for the MathUtil#getDirection and for your thread on Spigot.
        final double calculatedMovementAngle;

        final Point lookDirVec = new Point(0.D, 0.D, 0.D);

        final float rotX = (float) Math.toRadians(yaw);
        final float rotY = (float) Math.toRadians(0);

        lookDirVec.setY(-Math.sin(rotY));

        final double xz = Math.cos(rotY);

        lookDirVec.setX(-xz * Math.sin(rotX));
        lookDirVec.setZ(xz * Math.cos(rotX));

        final Point moveVec = new Point(deltaX, 0, deltaZ);

        final boolean vectorDir = moveVec
                .clone()
                .crossProduct(lookDirVec)
                .dot(new Point(0, 1, 0)) >= 0;
        final double dot = Math.min(Math.max(moveVec.dot(lookDirVec) / (moveVec.length() * lookDirVec.length()), -1), 1);
        calculatedMovementAngle = (vectorDir ? 1 : -1) * Math.acos(dot);

        return calculatedMovementAngle;
    }

    private byte[] getBestGuessForwardIterationOrder() {
        if (data.isOnLadder()) return new byte[]{1, -1, 0};
        if (this.isStrafing(StrafeType.NONE, StrafeType.A, StrafeType.D)) return new byte[]{0, 1, -1};
        if (this.isStrafing(StrafeType.W, StrafeType.WA, StrafeType.WD)) return new byte[]{1, -1, 0};
        if (this.isStrafing(StrafeType.S, StrafeType.SA, StrafeType.SD)) return new byte[]{-1, 1, 0};

        // Return normal iteration if it just doesn't work lol.
        return new byte[]{1, -1, 0};
    }

    private byte[] getBestGuessStrafeIterationOrder() {
        if (data.isOnLadder()) return new byte[]{1, -1, 0};
        if (this.isStrafing(StrafeType.NONE, StrafeType.W, StrafeType.S)) return new byte[]{0, 1, -1};
        if (this.isStrafing(StrafeType.D, StrafeType.WD, StrafeType.SD)) return new byte[]{1, -1, 0};
        if (this.isStrafing(StrafeType.A, StrafeType.WA, StrafeType.SA)) return new byte[]{-1, 1, 0};

        // Return normal iteration if it just doesn't work lol.
        return new byte[]{1, -1, 0};
    }

    private boolean[] getBestGuessJumpIterationOrder(final double motionY, final int jumpAmplifier) {
        final float jumpMotion = 0.42F;

        // TODO: Add liquid jumping compensation.
        final boolean jumped = data.getMotionY() < 0.0D
                && Math.abs(jumpMotion - (float) (motionY + (jumpAmplifier * 0.1))) < 0.05;

        return jumped ? new boolean[]{true, false} : new boolean[]{false, true};
    }


    private boolean isStrafing(final StrafeType... strafeTypes) {
        for (final StrafeType strafeType : strafeTypes) {
            if (strafeType.getCondition().apply(this.calculatedMoveAngle)) {
                return true;
            }
        }

        return false;
    }

    @Getter
    private enum StrafeType {
        W(angle -> Math.abs(angle - 0.0D) < 0.15),
        WA(angle -> Math.abs(angle - (-Math.PI / 4)) < 0.15),
        A(angle -> Math.abs(angle - (-Math.PI / 2)) < 0.15),
        SA(angle -> Math.abs(angle - (-3 * Math.PI / 4)) < 0.15),
        S(angle -> Math.abs(angle - (-Math.PI)) < 0.15),
        SD(angle -> Math.abs(angle - (3 * Math.PI / 4)) < 0.15),
        D(angle -> Math.abs(angle - (Math.PI / 2)) < 0.15),
        WD(angle -> Math.abs(angle - (Math.PI / 4)) < 0.15),
        NONE(angle -> Double.isNaN(angle));

        private final Function<Double, Boolean> condition;

        StrafeType(final Function<Double, Boolean> condition) {
            this.condition = condition;
        }
    }
}
