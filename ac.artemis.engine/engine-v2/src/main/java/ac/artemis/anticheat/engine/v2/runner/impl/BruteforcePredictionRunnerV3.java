package ac.artemis.anticheat.engine.v2.runner.impl;

import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.anticheat.engine.v2.heading.MoveWithHeadingFactory;
import ac.artemis.anticheat.engine.v2.heading.MoveWithHeadingProvider;
import ac.artemis.anticheat.engine.v2.jump.JumpMoveFactory;
import ac.artemis.anticheat.engine.v2.jump.JumpMoveProvider;
import ac.artemis.anticheat.engine.v2.liquid.LiquidCollisionFactory;
import ac.artemis.anticheat.engine.v2.liquid.LiquidCollisionProvider;
import ac.artemis.anticheat.engine.v2.optimizer.OptimizedIterationFactory;
import ac.artemis.anticheat.engine.v2.optimizer.OptimizedIterationProvider;
import ac.artemis.anticheat.engine.v2.optimizer.OptimizedIterationResult;
import ac.artemis.anticheat.engine.v2.runner.BruteforceIteration;
import ac.artemis.anticheat.engine.v2.runner.BruteforceKey;
import ac.artemis.anticheat.engine.v2.runner.PredictionRunner;
import ac.artemis.anticheat.engine.v2.runner.loop.LoopData;
import ac.artemis.anticheat.engine.v2.runner.loop.LoopReturn;
import ac.artemis.anticheat.engine.v2.runner.loop.Looper;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.attributes.AttributeMap;
import ac.artemis.core.v5.emulator.attributes.EntityAttributes;
import ac.artemis.core.v5.emulator.modal.Motion;
import ac.artemis.core.v5.emulator.tags.Tags;
import ac.artemis.core.v5.utils.ServerUtil;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.packet.protocol.ProtocolVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BruteforcePredictionRunnerV3 implements PredictionRunner {

    private final ArtemisData data;

    private final MoveWithHeadingProvider moveWithHeadingProvider;
    private final OptimizedIterationProvider optimizedIterationProvider;
    private final JumpMoveProvider jumpMoveProvider;
    private final LiquidCollisionProvider liquidCollisionProvider;

    public BruteforcePredictionRunnerV3(final ArtemisData data) {
        this.data = data;

        final ProtocolVersion version = data.getData().getVersion();

        this.moveWithHeadingProvider = new MoveWithHeadingFactory()
                .setVersion(version)
                .build();

        this.jumpMoveProvider = new JumpMoveFactory().build();
        this.optimizedIterationProvider = new OptimizedIterationFactory().build();
        this.liquidCollisionProvider = new LiquidCollisionFactory().build();

        this.loopData = new LoopData(data, this::runIteration);
    }

    private final LoopData loopData;

    @Override
    public void run() {
        final OptimizedIterationResult bestIteration = optimizedIterationProvider.provide(data);

        //bestIteration.setMissedFlyingIteration(transitionDataMap.isEmpty() ? new boolean[]{false,true} : new boolean[]{true, false});
        bestIteration.setMissedFlyingIteration(new boolean[]{false, true});

        final LoopReturn loopReturn;

        if (loopData.isNext() && ConfigManager.getPrediction().isSkipTick()) {
            loopData.sort(data.getData().prediction.getLocation());

            loopReturn = Looper.DUMB.run(loopData, bestIteration);
        } else if (data.getData().prediction.isPos()) {
            loopData.clean();
            loopReturn = Looper.FULL.run(loopData, bestIteration);
        } else if (data.getData().prediction.isLastPos() && ConfigManager.getPrediction().isSkipTick()) {
            loopData.clean();
            loopReturn = Looper.SKIP.run(loopData, bestIteration);
        } else {
            loopData.clean();
            loopReturn = Looper.EMPTY.run(loopData, bestIteration);
        }

        loopReturn.getRunnable().run();
    }


    private BruteforceIteration runIteration(final BruteforceKey key) {
        if (key.isVelocity() && data.getPlayer().prediction.getQueuedVelocities().isEmpty()) return null;

        if (key.isSprint() && key.isUsing()) return null;

        final AttributeMap attributeMap = data.getAttributeMap().copy();

        attributeMap.get(EntityAttributes.GROUND).set(key.isGround());
        attributeMap.get(EntityAttributes.COMPENSATE_WORLD).set(key.isWorld());
        attributeMap.get(EntityAttributes.JUMPING).set(key.isJump());
        attributeMap.get(EntityAttributes.LAST_SPRINT).set(data.getData().prediction.isSprinting());
        attributeMap.get(EntityAttributes.SNEAK).set(data.getData().getUser().isSneaking());

        final int jumpTicks = (int) attributeMap.get(EntityAttributes.JUMP_TICKS).getBase();
        if (jumpTicks > 0) attributeMap.get(EntityAttributes.JUMP_TICKS).set(jumpTicks - 1);

        attributeMap.get(EntityAttributes.ATTRIBUTE_SPEED).set(data.getAIMoveSpeed());
        attributeMap.get(EntityAttributes.LADDER).set(data.getLadder());

        final List<Consumer<ArtemisData>> postData = new ArrayList<>();

        TransitionData input = new TransitionData(data.getData())
                .setBoundingBox(data.getEntityBoundingBox());

        Motion motion = new Motion(
                data.getMotionX(),
                data.getMotionY(),
                data.getMotionZ()
        );

        float forward = key.getForward();
        float strafe = key.getStrafe();

        if (key.isUsing()) {
            forward *= 0.2D;
            strafe *= 0.2D;

            input.addTag(Tags.USING);
        }

        if (attributeMap.poll(EntityAttributes.SNEAK)) {
            forward *= (float) 0.3D;
            strafe *= (float) 0.3D;

            input.addTag(Tags.SNEAK);
        }

        forward *= 0.98F;
        strafe *= 0.98F;

        // TODO: 9/13/2021 Handle velocity properly. The thing with the current is that
        //  if the player took multiple velocities in 1 tick this will break. Same will
        //  happen with the tranny hitting the wrong tick and receiving a velocity at that tick.
        if (key.isVelocity()) {
            final Velocity velocity = data.getPlayer().prediction.getQueuedVelocities().peek();

            if (velocity != null) {
                motion.setX(velocity.getX());
                motion.setY(velocity.getY());
                motion.setZ(velocity.getZ());
            } else {
                ServerUtil.console("Velocity was null. If the issue persists contact us.");
            }

            postData.add(data -> {
                data.getPlayer().prediction.setConfirmingVelocity(true);
                data.getPlayer().prediction.setVelocityTicks(0);
                data.getPlayer().prediction.getQueuedVelocities().poll();
            });

            input.addTag(Tags.VELOCITY);
        }

        if (key.isAttack()) {
            motion.setX(motion.getX() * 0.6D);
            motion.setZ(motion.getZ() * 0.6D);

            postData.add(data -> data.getPlayer().prediction.getQueuedAttacks().poll());

            input.addTag(Tags.HIT_SLOWDOWN);
        }

        motion = liquidCollisionProvider.provideMotion(input, data.getAttributeMap(), motion);

        /*
         * In post 1.8.9 versions the place where the movement is smaller than a certain
         * value is rounded this value is 0.003D and for pre 1.9 versions it is 0.005D.
         */
        final double minimum = data.getPlayer().getVersion().isOrAbove(ProtocolVersion.V1_9) ? 0.003D : 0.005D;

        if (Math.abs(motion.getX()) < minimum) motion.setX(0.0D);
        if (Math.abs(motion.getY()) < minimum) motion.setY(0.0D);
        if (Math.abs(motion.getZ()) < minimum) motion.setZ(0.0D);

        attributeMap.get(EntityAttributes.SPRINT).set(data.getPlayer().user.isSprinting());
        data.setSprintAttribute(key.isSprint());

        if (key.isSprint()) {
            input.addTag(Tags.SPRINT);
        }

        input   // Keyboard
                .setMoveForward(forward)
                .setMoveStrafe(strafe)
                .setMotionX(motion.getX())
                .setMotionY(motion.getY())
                .setMotionZ(motion.getZ())
                .setX(data.getX())
                .setY(data.getY())
                .setZ(data.getZ())
                .setAttributeMap(attributeMap)
                .setSprintingAttribute(key.isSprint())
                .setTargetX(data.getServerX())
                .setTargetY(data.getServerY())
                .setTargetZ(data.getServerZ())
                .setDumbFix(key.isDumbFix());

        if (key.isJump()) {
            input = jumpMoveProvider.provide(input);
        } else {
            attributeMap.get(EntityAttributes.JUMP_TICKS).set(0);
        }

        input = moveWithHeadingProvider.moveWithHeading(input);

        final double distance = new Point(
                data.getData().prediction.getX(),
                data.getData().prediction.getY(),
                data.getData().prediction.getZ()
        ).squareScaledDistanceTo(input.getResult(), 1, 3, 1);

        final double length = new Point(
                data.getPlayer().prediction.getLastX(),
                data.getPlayer().prediction.getLastY(),
                data.getPlayer().prediction.getLastZ()
        ).distanceTo(
                input.getResult()
        );

        final double deltaX = Math.abs(input.getTargetX() - input.getResult().getX());
        final double deltaY = Math.abs(input.getTargetY() - input.getResult().getY());
        final double deltaZ = Math.abs(input.getTargetZ() - input.getResult().getZ());

        return new BruteforceIteration(input, distance, deltaX, deltaY, deltaZ, length, postData);
    }
}
