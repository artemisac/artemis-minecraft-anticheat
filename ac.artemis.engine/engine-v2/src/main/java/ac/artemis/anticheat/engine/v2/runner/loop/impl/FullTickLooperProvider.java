package ac.artemis.anticheat.engine.v2.runner.loop.impl;

import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.anticheat.engine.v2.optimizer.OptimizedIterationResult;
import ac.artemis.anticheat.engine.v2.runner.BruteforceIteration;
import ac.artemis.anticheat.engine.v2.runner.BruteforceKey;
import ac.artemis.anticheat.engine.v2.runner.loop.BruteforceLooper;
import ac.artemis.anticheat.engine.v2.runner.loop.LoopData;
import ac.artemis.anticheat.engine.v2.runner.loop.LoopReturn;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.utils.OutputAction;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FullTickLooperProvider implements BruteforceLooper {
    @Override
    public LoopReturn run(final LoopData loopData, final OptimizedIterationResult result) {
        double smallest = Double.MAX_VALUE;
        BruteforceIteration iter = null;
        int run = 0;

        iteration:{
            for (final boolean sprinting : result.getSprintIteration()) {
                for (final boolean using : result.getUsingIteration()) {
                    for (final boolean attack : result.getAttackIteration()) {
                        for (final boolean ground : result.getGroundIteration()) {
                            for (final boolean velocity : result.getVelocityIteration()) {
                                for (final boolean world : result.getWorldCompensationIteration()) {
                                    for (final boolean missedFlying : result.getMissedFlyingIteration()) {
                                        for (final byte forward : result.getForwardIteration()) {
                                            for (final byte strafe : result.getStrafeIteration()) {
                                                for (final boolean jump : result.getJumpIteration()) {
                                                    final BruteforceKey key = new BruteforceKey(loopData.getData().getData())
                                                            .setForward(forward)
                                                            .setStrafe(strafe)
                                                            .setSprint(sprinting)
                                                            .setUsing(using)
                                                            .setAttack(attack)
                                                            .setGround(ground)
                                                            .setVelocity(velocity)
                                                            .setWorld(world)
                                                            .setMissedFlying(missedFlying)
                                                            .setJump(jump);

                                                    final BruteforceIteration iteration = loopData.run(key);

                                                    if (iteration == null || iteration.getData() == null) continue;

                                                    run++;
                                                    final double distance = iteration.getDistance();

                                                    if (distance < smallest) {
                                                        iter = iteration;
                                                        smallest = distance;

                                                        if (distance < 1E-8) break iteration;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        final BruteforceIteration finalIter = iter;
        final int finalRun = run;
        return new LoopReturn(iter == null ? 0.0 : iter.getDistance(), () -> {
            /*
             * It went wrong I'm sorry I plead guilty to every crime possible. I do not know how
             * this is possible, I do not know why this is possible, but it's happened on-join and
             * it pains me every day. I demand pardon, I ask for forgiveness, for thy must forgive
             * the weak. I do not ask for you to keep a good impression of me. I simply pledge for
             * this to be overlooked. I thank you.
             */
            if (finalIter == null || finalIter.getData().getAttributeMap() == null) return;

            // -------------- POST ITERATION --------------

            final TransitionData transition = finalIter.getData();

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
            for (final OutputAction action : transition.getActionList()) {
                action.accept(transition);
            }

            /*
             * Well well well! We have successfully finish the fucking horrendous disaster that was!
             * Here we set the data entity motion to what was recollected from the iteration. Hence,
             * it can be modified by the post-collision stuff down below without trouble!
             *
             * In the same method, we set the attributes after the iteration! This holds everything,
             * including ground status, sprint status and many other things. It's quite fancy!
             */
            loopData.getData().apply(finalIter.getData());

            for (final Consumer<ArtemisData> change : finalIter.getConfirmedDataChanges()) {
                change.accept(loopData.getData());
            }

            loopData.getData().setDistance(finalIter.getDistance());
            loopData.getData().setIteration(1);
            loopData.getData().getData().prediction.setIteration(1);
            loopData.getData().getData().prediction.setDistanceX(finalIter.getDistanceX());
            loopData.getData().getData().prediction.setDistanceY(finalIter.getDistanceY());
            loopData.getData().getData().prediction.setDistanceZ(finalIter.getDistanceZ());
            loopData.getData().getData().prediction.setSprinting(finalIter.getData().isSprintingAttribute());
            loopData.getData().getData().prediction.setFriction(finalIter.getData().getFriction());
            loopData.getData().getData().prediction.setIteration(finalRun);
            //Bukkit.broadcastMessage("distance=" + loopData.getData().getDistance());

            loopData.getData().getTags().clear();
            loopData.getData().getTags().addAll(finalIter.getData().getTags().stream().map(Enum::name).collect(Collectors.toSet()));
        });
    }
}
