package ac.artemis.anticheat.engine.v2.runner.loop.impl;

import ac.artemis.anticheat.engine.v2.optimizer.OptimizedIterationResult;
import ac.artemis.anticheat.engine.v2.runner.BruteforceIteration;
import ac.artemis.anticheat.engine.v2.runner.BruteforceKey;
import ac.artemis.anticheat.engine.v2.runner.loop.BruteforceLooper;
import ac.artemis.anticheat.engine.v2.runner.loop.LoopData;
import ac.artemis.anticheat.engine.v2.runner.loop.LoopReturn;
import ac.artemis.core.v5.emulator.utils.OutputAction;

public class EmptyTickLooperProvider implements BruteforceLooper {
    @Override
    public LoopReturn run(LoopData loopData, OptimizedIterationResult result) {
        loopData.getData().getTags().clear();

        /*
         * Here we actually go forward with the iteration and calculate the distance
         * to the one received. The distance is a calculation with the following
         * formula:
         *
         * dx^2 + dy^2 + dz^2
         *
         * d: delta (double)
         * x: x coordinate (double)
         * y: y coordinate (double)
         * z: z coordinate (double)
         *
         * The final distance hence represents whether the scenario is close or not
         * to what we're expecting.
         */
        final BruteforceKey key = new BruteforceKey(loopData.getData().getData())
                .setForward(0)
                .setStrafe(0)
                .setJump(false)
                .setSprint(false)
                .setUsing(false)
                .setAttack(false)
                .setGround(result.getGroundIteration()[0])
                .setVelocity(result.getVelocityIteration()[0])
                .setWorld(true)
                .setMissedFlying(false);

        final BruteforceIteration iteration = loopData.run(key);

        return new LoopReturn(0.D, () -> {
            /*
             * It went wrong I'm sorry I plead guilty to every crime possible. I do not know how
             * this is possible, I do not know why this is possible, but it's happened on-join and
             * it pains me every day. I demand pardon, I ask for forgiveness, for thy must forgive
             * the weak. I do not ask for you to keep a good impression of me. I simply pledge for
             * this to be overlooked. I thank you.
             */
            if (iteration == null || iteration.getData().getAttributeMap() == null) return;

            // -------------- POST ITERATION --------------

            /*
             * What happens here is mysterious... jk. This corresponds to the consumers ran for
             * attacks and velocity. The reason I have this here is to directly poll the validated
             * velocity or combat from their respective queue as we have validated that these have
             * been processed in the tick.
             *
             * Once that's done all is good *whew*. Now we have to process the actual post-collision
             * changes. This specific method grabs every change which happens right after collisions
             * (this includes block collisions *super important*) and a handful of other things, such
             * as ground calculation etc... This all ends up serving an important part in the end
             *
             * Furthermore we handle all the post-collision post-move handling for the entity. This
             * includes a couple of things, such as gravity, friction slowdown and anything relating
             * to giving the force a slow-down to not make people go zooooom
             */
            for (final OutputAction action : iteration.getData().getActionList()) {
                action.accept(iteration.getData());
            }

            /*
             * Well well well! We have successfully finish the fucking horrendous disaster that was!
             * Here we set the data entity motion to what was recollected from the iteration. Hence,
             * it can be modified by the post-collision stuff down below without trouble!
             *
             * In the same method, we set the attributes after the iteration! This holds everything,
             * including ground status, sprint status and many other things. It's quite fancy!
             */
            loopData.getData().apply(iteration.getData());

            loopData.getData().getTags().add("Empty run");
            loopData.getData().setDistance(0.0D);
        });
    }
}
