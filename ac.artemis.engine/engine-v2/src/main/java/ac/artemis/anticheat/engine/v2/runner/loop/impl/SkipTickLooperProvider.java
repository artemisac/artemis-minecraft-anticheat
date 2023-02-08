package ac.artemis.anticheat.engine.v2.runner.loop.impl;

import ac.artemis.anticheat.engine.v2.optimizer.OptimizedIterationResult;
import ac.artemis.anticheat.engine.v2.runner.BruteforceIteration;
import ac.artemis.anticheat.engine.v2.runner.BruteforceKey;
import ac.artemis.anticheat.engine.v2.runner.loop.BruteforceLooper;
import ac.artemis.anticheat.engine.v2.runner.loop.LoopData;
import ac.artemis.anticheat.engine.v2.runner.loop.LoopReturn;
import ac.artemis.core.v5.emulator.utils.OutputAction;

public class SkipTickLooperProvider implements BruteforceLooper {
    @Override
    public LoopReturn run(LoopData loopData, OptimizedIterationResult result) {
        /*
         * Yes, this loop looks ugly. No, I'm not going to change it. Fuck you, I like my clarity. The reason it's not
         * compacted into a single loop is simply due to how it operates. I am not going to do silly maths to save one
         * or two IntNode calls. Fuck off
         */
        for (boolean y : new boolean[]{false, true}) {
            for (byte x : new byte[]{0, 1, -1}) {
                for (byte z : new byte[]{0, 1, -1}) {

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
                            .setForward(x)
                            .setStrafe(z)
                            .setJump(y)
                            .setSprint(result.getSprintIteration()[0])
                            .setUsing(result.getUsingIteration()[0])
                            .setAttack(false)
                            .setGround(result.getGroundIteration()[0])
                            .setVelocity(result.getVelocityIteration()[0])
                            .setWorld(true)
                            .setMissedFlying(false)
                            .setDumbFix(true);

                    final BruteforceIteration iteration = loopData.run(key);

                    /*
                     * Uh oh! What happened here! Well, to optimize the living shit of this
                     * living loop disaster, we decided to return null on a couple dozen of
                     * impossible scenarios by the game. Whilst this most definitely will result
                     * in cheaters having a constant invalid motion until they stop for 5 ticks,
                     * it will eliminate over 50 different possible scenarios.
                     */
                    if (iteration == null)
                        continue;

                    final double distance = loopData.getData().getData().prediction.getLocation()
                            .squareDistanceTo(iteration.getData().getResult());

                    if (distance < 9.0E-4D && distance > 0.0D) {
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


                        loopData.addIteration(iteration.getData().getResult(), iteration);
                    }
                }
            }
        }
        return new LoopReturn(0.0D, () -> {
            loopData.getData().getTags().add("0.03 check");
            loopData.getData().setDistance(0.0D);
        });
    }
}
