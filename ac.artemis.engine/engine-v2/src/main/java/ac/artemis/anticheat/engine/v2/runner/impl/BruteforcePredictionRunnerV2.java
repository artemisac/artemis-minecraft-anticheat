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
import ac.artemis.anticheat.engine.v2.runner.PredictionRunner;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.attributes.AttributeMap;
import ac.artemis.core.v5.emulator.attributes.EntityAttributes;
import ac.artemis.core.v5.emulator.modal.Motion;
import ac.artemis.core.v5.emulator.tags.Tags;
import ac.artemis.core.v5.emulator.utils.OutputAction;
import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.core.v5.utils.ServerUtil;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.packet.protocol.ProtocolVersion;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BruteforcePredictionRunnerV2 implements PredictionRunner {
    private final ArtemisData data;

    public BruteforcePredictionRunnerV2(ArtemisData data) {
        this.data = data;

        final ProtocolVersion version = data.getData().getVersion();

        this.moveWithHeadingProvider = new MoveWithHeadingFactory()
                .setVersion(version)
                .build();

        this.jumpMoveProvider = new JumpMoveFactory()
                .build();
        this.optimizedIterationProvider = new OptimizedIterationFactory()
                .build();
        this.liquidCollisionProvider = new LiquidCollisionFactory()
                .build();
    }

    private final MoveWithHeadingProvider moveWithHeadingProvider;
    private final OptimizedIterationProvider optimizedIterationProvider;
    private final JumpMoveProvider jumpMoveProvider;
    private final LiquidCollisionProvider liquidCollisionProvider;

    // Experimental
    private BruteforceIteration lastIteration;
    private final Map<Point, TransitionData> transitionDataMap = new HashMap<>();
    private final List<TransitionData> sortedData = new ArrayList<>();
    private TransitionData snapshot;
    @Override
    public void run() {
        shortest = new AtomicReference<>(99.99D);
        shortestY = new AtomicReference<>(99.99D);
        iterationCount = new AtomicInteger();
        shortestIteration = new AtomicReference<>();
        longestDistance = new AtomicReference<>(0D);

        if (data.getData().prediction.isPos()
                && data.getData().getPrediction().getLastLocation() != null
                && data.getData().getPrediction().getLocation()
                .squareDistanceTo(data.getData().getPrediction().getLastLocation()) > 9.0E-4D) {
            if (!transitionDataMap.isEmpty()) {
                sortedData.clear();
                sortedData.addAll(transitionDataMap.entrySet().stream().sorted(Comparator.comparingDouble(e -> {
                    return e.getKey().squareDistanceTo(data.getPlayer().prediction.getLocation());
                })).map(Map.Entry::getValue).collect(Collectors.toList()));
                snapshot = data.snapshot();
            }

            predict();
            transitionDataMap.clear();
            sortedData.clear();

        } else if (data.getData().prediction.isLastPos()) {
            compensate();
        } else if (data.getData().prediction.isLastLastPos()) {
            reflect();
        } else {
            transitionDataMap.clear();
            sortedData.clear();
            emptyRun();
        }
        /*for (BoundingBox collidingBox : data.getCollidingBoxes(data.getBoundingBox().cloneBB().expand(0.1, 0.1, 0.1), true)) {
            collidingBox.draw(data.getPlayer().bukkitPlayer);
        }*/
    }

    private AtomicReference<Double> shortest;
    private AtomicReference<Double> shortestY;
    private AtomicInteger iterationCount;
    private AtomicReference<BruteforceIteration> shortestIteration;
    private AtomicReference<Double> longestDistance;

    private void predict() {
        final List<Double> distances = new ArrayList<>();
        data.getTags().clear();

        /*
         * Get the best case iteration scenario from the optimized iteration provider.
         * The iteration provider uses simple vector math as well as information received
         * from the client to create the most probable iteration set due to the low change
         * of desync. While not completely accurate, it reduces the total number of iterations
         * down to 1 in most cases, if not that it stays around 30-100 most of the time.
         * Without this system, the total number of iterations would stay around 200-576,
         * increasing the overhead the prediction bruteforce takes. While the system is
         * already very lightweight due to the world caching system, it always helps to
         * not do more work than you have to.
         */
        final OptimizedIterationResult bestIteration = optimizedIterationProvider.provide(data);
        bestIteration.setMissedFlyingIteration(transitionDataMap.isEmpty() ? new boolean[]{false,true} : new boolean[]{true, false});

        iteration: {
            /*
             * SP??? What may SP be! Well, firstly it's sprinting. Now that we got that out of the way, lets
             * talk about how sprinting works in minecraft. There's the sprinting status which is a metadata
             * component stored in the data-watcher. Essentially, it's a boolean that can be switched on and off
             * by the server.
             *
             * However, this boolean can also be altered by the setSprinting method. Now lets talk about sprinting
             * speed. Sprinting speed is an entity attribute. This means it'll give a boost to a player's walk
             * speed. The attribute is set in setSprinting, however overlooks the DataWatcher. This means that
             * if you send any sort of packet which updates the data-watcher sprinting status but not an attribute
             * packet, you'll end up with a sprint de-sync. This most notoriously is dangerous in a predictions
             * engine. Hence, we bruteforce it.
             */
            for (final boolean sp : bestIteration.getSprintIteration()) {
                /*
                 * Now, what is ET? Are we being invaded by aliens? Worry not! Everything's going to be fine!
                 * What's actually happened is that eating is handled in an extremely weird fashion. Hence,
                 * another loop.
                 *
                 * Much love,
                 * Ghast.
                 */
                for (final boolean et : new boolean[]{false, true}) {
                    /*
                     * The attack method is quite peculiar in the sense it's straight forward however can find
                     * to be inconsistent if the knockback resistance attribute is given to a player. To play
                     * safe, I've built a system which calculates whether a player has hit or not another player.
                     * If the player has hit the opponent, it will calculate and loop through this with the
                     * attack modifier.
                     */
                    for (final boolean at : bestIteration.getAttackIteration()) {

                        for (final boolean gr : bestIteration.getGroundIteration()) {
                            /*
                             * Velocities have long to be thought to be tricky. Fear not, they're easy. What we're
                             * doing here is storing the sent velocities and ensuring these are wrapped with a
                             * transaction packet before and after. If the transaction-end packet is not received,
                             * we bruteforce both scenarios as any of the two ticks could be the holder of this
                             * specific velocity modifier
                             */
                            for (final boolean vl : bestIteration.getVelocityIteration()) {
                                /*
                                 * Handling the world can come to be tricky at times. Hence, it was decided by
                                 * yours truly to handle both NMS and Warden World. Whilst WardenWorld is most
                                 * definitely the most reliable, NMS holds the advantage of having direct access
                                 * with Bukkit's nonsense. Hence, we first go through NMS then Warden world to
                                 * compensate for all the fucking connection block placing bullshit.
                                 */
                                for (final boolean wl : bestIteration.getWorldCompensationIteration()) {

                                    /*
                                     * One tricky thing I hate about Minecraft is the > 0.03 on flying packet. Sometimes,
                                     * for the most absurd reasons I know not of, the client will move just a teeny bit to
                                     * the point that it simply isn't considered a move (despite it very much being a move).
                                     * As a response, we get an offset which can be up to 0.02. This is often exploited in
                                     * hacked clients in velocity and fly modules; As a response, we iterate for it in the
                                     * case the last flying was not a position but the recent one was. This will ensure that
                                     * no offset is missed
                                     */
                                    for (final boolean ms : bestIteration.getMissedFlyingIteration()) {
                                        /*
                                         * Yes, this loop looks ugly. No, I'm not going to change it. Fuck you, I like my clarity. The reason it's not
                                         * compacted into a single loop is simply due to how it operates. I am not going to do silly maths to save one
                                         * or two IntNode calls. Fuck off
                                         */
                                        for (final byte x : bestIteration.getForwardIteration()) {
                                            for (final byte z : bestIteration.getStrafeIteration()) {
                                                /*
                                                 * This particular loop was first adapted by Artemis and discovered by yours truly: Ghast. It was found
                                                 * through trial and error that determining jump through the environment is a dump fucking idea and that
                                                 * one addition to the loop, which unfortunately double's it's repetition indeed, improves the accuracy
                                                 * by over 10%. This benefit is extremely good and also allows for a much more accurate detection of low
                                                 * hops through bad packets.
                                                 */
                                                for (final boolean y : bestIteration.getJumpIteration()) {

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
                                                    final BruteForceKey key = new BruteForceKey(data.getData())
                                                            .setForward(x)
                                                            .setStrafe(z)
                                                            .setJump(y)
                                                            .setSprint(sp)
                                                            .setUsing(et)
                                                            .setAttack(at)
                                                            .setGround(gr)
                                                            .setVelocity(vl)
                                                            .setWorld(wl)
                                                            .setMissedFlying(ms);

                                                    final BruteforceIteration iteration = getIteration(data, key);

                                                    /*
                                                     * Uh oh! What happened here! Well, to optimize the living shit of this
                                                     * living loop disaster, we decided to return null on a couple dozen of
                                                     * impossible scenarios by the game. Whilst this most definitely will result
                                                     * in cheaters having a constant invalid motion until they stop for 5 ticks,
                                                     * it will eliminate over 50 different possible scenarios.
                                                     */
                                                    if (iteration == null)
                                                        continue;

                                                    iterationCount.incrementAndGet();

                                                    if (iteration.getLength() > longestDistance.get()) {
                                                        longestDistance.set(iteration.getLength());
                                                    }

                                                    distances.add(iteration.distance);

                                                    /*
                                                     * Here we check if the distance is shorter than the atomic reference. In the
                                                     * case it is, we apply the shortest distance as the defined distance. In
                                                     * the scenario it's below 1E-6, we know for a guarantee that it's the correct
                                                     * one. Hence, we can break this horrendous fuckery of a loop and not have to
                                                     * iterate through every fucking god damn scenario again.
                                                     */
                                                    if (iteration.getDistance() < shortest.get()) {
                                                        shortest.set(iteration.getDistance());
                                                        shortestIteration.set(iteration);

                                                        if (iteration.getDistance() < 1E-11D) {
                                                            break iteration;
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
        }

        /*
         * Finally! The disaster fucking over. All we need to do now is to apply the correct
         * iteration and accept all of the modifiers in the right order! What could go wron--
         */
        final BruteforceIteration iteration = shortestIteration.get();

        /*
         * It went wrong I'm sorry I plead guilty to every crime possible. I do not know how
         * this is possible, I do not know why this is possible, but it's happened on-join and
         * it pains me every day. I demand pardon, I ask for forgiveness, for thy must forgive
         * the weak. I do not ask for you to keep a good impression of me. I simply pledge for
         * this to be overlooked. I thank you.
         */
        if (iteration == null || iteration.getData().getAttributeMap() == null) return;

        // -------------- POST ITERATION --------------

        final TransitionData transition = iteration.data;

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
        data.apply(iteration.getData());

        for (Consumer<ArtemisData> confirmedDataChange : iteration.getConfirmedDataChanges()) {
            confirmedDataChange.accept(data);
        }

        data.setDistance(iteration.distance);
        data.setIteration(iterationCount.get());
        data.getData().prediction.setIteration(iterationCount.get());
        data.getData().prediction.setDistanceX(iteration.distanceX);
        data.getData().prediction.setDistanceY(iteration.distanceY);
        data.getData().prediction.setDistanceZ(iteration.distanceZ);
        data.getData().prediction.setSprinting(iteration.getData().isSprintingAttribute());
        lastIteration = iteration;

        Server.v().broadcast("min=" + distances.stream().mapToDouble(e -> e).min().orElse(-1.0D) + " distance=" + data.getDistance());

        data.getTags().clear();
        data.getTags().addAll(iteration.getData().getTags().stream().map(Enum::name).collect(Collectors.toSet()));
    }

    private void compensate() {
        data.getTags().clear();

        /*
         * Get the best case iteration scenario from the optimized iteration provider.
         * The iteration provider uses simple vector math as well as information received
         * from the client to create the most probable iteration set due to the low change
         * of desync. While not completely accurate, it reduces the total number of iterations
         * down to 1 in most cases, if not that it stays around 30-100 most of the time.
         * Without this system, the total number of iterations would stay around 200-576,
         * increasing the overhead the prediction bruteforce takes. While the system is
         * already very lightweight due to the world caching system, it always helps to
         * not do more work than you have to.
         */
        final OptimizedIterationResult bestIteration = optimizedIterationProvider.provide(data);


        /*
         * Yes, this loop looks ugly. No, I'm not going to change it. Fuck you, I like my clarity. The reason it's not
         * compacted into a single loop is simply due to how it operates. I am not going to do silly maths to save one
         * or two IntNode calls. Fuck off
         */
        for (byte y = 0; y < 2; y++) {
            for (byte x = 0; x < bestIteration.getForwardIteration().length; x++) {
                for (byte z = 0; z < bestIteration.getStrafeIteration().length; z++) {

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
                    final BruteForceKey key = new BruteForceKey(data.getData())
                            .setForward(x)
                            .setStrafe(z)
                            .setJump(bestIteration.getJumpIteration()[0])
                            .setSprint(bestIteration.getSprintIteration()[0])
                            .setUsing(bestIteration.getUsingIteration()[0])
                            .setAttack(false)
                            .setGround(bestIteration.getGroundIteration()[0])
                            .setVelocity(bestIteration.getVelocityIteration()[0])
                            .setWorld(true)
                            .setMissedFlying(false);

                    final BruteforceIteration iteration = getIteration(data, key);

                    /*
                     * Uh oh! What happened here! Well, to optimize the living shit of this
                     * living loop disaster, we decided to return null on a couple dozen of
                     * impossible scenarios by the game. Whilst this most definitely will result
                     * in cheaters having a constant invalid motion until they stop for 5 ticks,
                     * it will eliminate over 50 different possible scenarios.
                     */
                    if (iteration == null)
                        continue;

                    final double distance = data.getData().prediction.getLocation()
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


                        //Bukkit.broadcastMessage("Potential 0.03 with distance " + distance);
                        transitionDataMap.put(iteration.getData().getResult(), iteration.data);
                    }
                }
            }
        }

        data.getTags().add("0.03 check");
        data.setDistance(0.0D);
    }

    private void reflect() {
        final List<TransitionData> datas = new ArrayList<>(transitionDataMap.values());
        transitionDataMap.clear();
        data.getTags().clear();

        for (TransitionData transitionData : datas) {
            data.apply(transitionData);
            compensate();
        }

        data.getTags().add("0.03 reflect");
        data.setDistance(0.0D);
    }

    private void emptyRun() {
        data.getTags().clear();

        /*
         * Get the best case iteration scenario from the optimized iteration provider.
         * The iteration provider uses simple vector math as well as information received
         * from the client to create the most probable iteration set due to the low change
         * of desync. While not completely accurate, it reduces the total number of iterations
         * down to 1 in most cases, if not that it stays around 30-100 most of the time.
         * Without this system, the total number of iterations would stay around 200-576,
         * increasing the overhead the prediction bruteforce takes. While the system is
         * already very lightweight due to the world caching system, it always helps to
         * not do more work than you have to.
         */
        final OptimizedIterationResult bestIteration = optimizedIterationProvider.provide(data);

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
        final BruteForceKey key = new BruteForceKey(data.getData())
                .setForward(0)
                .setStrafe(0)
                .setJump(false)
                .setSprint(false)
                .setUsing(false)
                .setAttack(false)
                .setGround(bestIteration.getGroundIteration()[0])
                .setVelocity(bestIteration.getVelocityIteration()[0])
                .setWorld(true)
                .setMissedFlying(false);

        final BruteforceIteration iteration = getIteration(data, key);

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
        data.apply(iteration.getData());

        data.getTags().add("Empty run");
        data.setDistance(0.0D);
        lastIteration = iteration;
    }

    private BruteforceIteration getIteration(final ArtemisData data, final BruteForceKey key) {
        final boolean jumping = key.isJump();
        final boolean sprinting = key.isSprint();
        final boolean using = key.isUsing();
        final boolean attack = key.isAttack();
        final boolean velocity = key.isVelocity();
        final boolean world = key.isWorld();
        final boolean ground = key.isGround();
        final boolean missed = key.isMissedFlying();

        if (velocity && data.getPlayer().prediction.getQueuedVelocities().isEmpty())
            return null;

        if (sprinting && using)
            return null;

        /*if (jumping && !data.isPreviousGround())
            return null;*/

        if (data.getPlayer().prediction.isPos() && missed && snapshot != null) {
            for (TransitionData sortedDatum : sortedData) {
                data.apply(sortedDatum);
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
                final BruteforceIteration iteration = getIteration(
                        data, // Emulator data
                        key.copy().setMissedFlying(false)
                );

                /*
                 * Uh oh! What happened here! Well, to optimize the living shit of this
                 * living loop disaster, we decided to return null on a couple dozen of
                 * impossible scenarios by the game. Whilst this most definitely will result
                 * in cheaters having a constant invalid motion until they stop for 5 ticks,
                 * it will eliminate over 50 different possible scenarios.
                 */
                if (iteration == null)
                    continue;

                /*
                 * Here we check if the distance is shorter than the atomic reference. In the
                 * case it is, we apply the shortest distance as the defined distance. In
                 * the scenario it's below 1E-6, we know for a guarantee that it's the correct
                 * one. Hence, we can break this horrendous fuckery of a loop and not have to
                 * iterate through every fucking god damn scenario again.
                 */
                if (iteration.getDistance() < shortest.get()) {
                    shortest.set(iteration.getDistance());
                    shortestIteration.set(iteration);
                    iteration.data.addTag(Tags.Z3R03);
                    if (iteration.getDistance() < 1E-5) {
                        //Bukkit.broadcastMessage("LETS GOOOO " + iteration.getDistance());
                        return iteration;
                    }
                }
            }

            data.apply(snapshot);
        }

        TransitionData input = new TransitionData(data.getData())
                .setBoundingBox(data.getEntityBoundingBox());

        /*
         * Here as we are obviously iterating we copy the values temporarily to not disturb the rest
         * of the loop. Pretty straight forward
         */
        Motion motion = new Motion(
                data.getMotionX(),
                data.getMotionY(),
                data.getMotionZ()
        );

        float forward = key.getForward();
        float strafe = key.getStrafe();

        final List<Consumer<ArtemisData>> postData = new ArrayList<>();

        /*
         * When a player uses an item, his move values are multiplied by a fifth. This significantly
         * slows down the player's speed.
         */
        if (using) {
            forward *= 0.2F;
            strafe *= 0.2F;

            input.addTag(Tags.USING);
        }

        /*
         * Alike with when using an item, move values are slowed down when sneaking. This makes sense.
         * Stop saying it doesn't. Really.
         */
        if (data.isSneaking()) {
            forward *= (float) 0.3D;
            strafe *= (float) 0.3D;

            input.addTag(Tags.SNEAK);
        }

        /*
         * Default value in MCP are integers (forward and strafe) multiplied by this odd constant. For
         * reasons unbeknownst to me, these values just seem to slow down the player a slight to give it
         * more of a natural feeling. Interesting stuff, Notch
         */
        forward *= 0.98F;
        strafe *= 0.98F;

        if (velocity) {
            final Velocity vel = data.getPlayer().prediction.getQueuedVelocities().peek();

            if (vel != null) {
                motion.setX(vel.getX());
                motion.setY(vel.getY());
                motion.setZ(vel.getZ());
            } else {
                ServerUtil.debug("Velocity was null.");
            }

            postData.add(new Consumer<ArtemisData>() {
                @Override
                public void accept(ArtemisData data) {
                    //System.out.println("Velocity fell on different tick");
                    data.getPlayer().prediction.setConfirmingVelocity(true);
                    data.getPlayer().prediction.setVelocityTicks(0);
                    data.getPlayer().prediction.getQueuedVelocities().poll();
                }
            });
        }

        if (attack) {
            motion.setX(motion.getX() * 0.6D);
            motion.setZ(motion.getZ() * 0.6D);


            postData.add(new Consumer<ArtemisData>() {
                @Override
                public void accept(ArtemisData data) {
                    data.getPlayer().prediction.getQueuedAttacks().poll();
                }
            });
        }

        motion = liquidCollisionProvider.provideMotion(input, data.getAttributeMap(), motion);

        /*
         * In post 1.8.9 versions the place where the movement is smaller than a certain
         * value is rounded this value is 0.003D and for pre 1.9 versions it is 0.005D.
         */
        final double minimumMovement = data.getPlayer().getVersion().isOrAbove(ProtocolVersion.V1_9)
                ? 0.003D
                : 0.005D;

        if (Math.abs(motion.getX()) < minimumMovement) {
            motion.setX(0.0D);
        }

        if (Math.abs(motion.getY()) < minimumMovement) {
            motion.setY(0.0D);
        }

        if (Math.abs(motion.getZ()) < minimumMovement) {
            motion.setZ(0.0D);
        }

        data.setSprintAttribute(sprinting);

        if (sprinting) {
            input.addTag(Tags.SPRINT);
        }

        final AttributeMap attributeMap = data.getAttributeMap().copy();

        attributeMap.get(EntityAttributes.GROUND).set(ground);
        attributeMap.get(EntityAttributes.COMPENSATE_WORLD).set(world);
        attributeMap.get(EntityAttributes.JUMPING).set(jumping);
        attributeMap.get(EntityAttributes.LAST_SPRINT).set(data.getData().prediction.isSprinting());

        final int jumpTicks = (int) attributeMap.get(EntityAttributes.JUMP_TICKS).getBase();

        if (jumpTicks > 0) {
            attributeMap.get(EntityAttributes.JUMP_TICKS).set(jumpTicks - 1);
        }

        /*
         * Speed modifying. In bukkit, the speed is multiplied by two for some reason. Henceforth,
         * we can just get the base player speed by divising it by two. This is done by this function
         * Furthermore, we need to add the valid attributes to it:
         */
        attributeMap.get(EntityAttributes.ATTRIBUTE_SPEED).set(data.getAIMoveSpeed());

        /*
         * Minecraft's attribute system for potions is split into 3 categories: type 0, type 1 and type 2.
         * Type 0: This is a value which will be added on top of the attribute
         * Type 1: This is a value which will multiply the attribute value
         * Type 2: This is a value which will be added based on a multiplication of the attribute value (1 + attr * value)
         *
         * Speed happens to be a type 2, as highlighted by the following code in MCP:
         *  private static final AttributeModifier sprintingSpeedBoostModifier = (new AttributeModifier(
         *         sprintingSpeedBoostModifierUUID, "Sprinting speed boost", Magic.SPRINT_MODIFIER, 2))
         *         .setSaved(false);
         *
         * It is henceforth safe to deem to define Speed as a type 2 effect, hence 1.F + (SprintEffectModifier * 0.2F)
         * with lossy conversion pretty much defines this.
         */
        if (sprinting) {
            final double speed = attributeMap.poll(EntityAttributes.ATTRIBUTE_SPEED);

            //attributeMap.get(EntityAttributes.ATTRIBUTE_SPEED).set(speed * (1.30000001192092896D));
        }

        if (data.getPlayer().getPlayer().hasEffect(PotionEffectType.SPEED)/*data.getActivePotionsMap().containsKey(PotionEffectType.SPEED.getId())*/) {
            input.addTag(Tags.POTION);
            /*attributeMap.get(EntityAttributes.ATTRIBUTE_SPEED)
                    .set((double) attributeMap.poll(EntityAttributes.ATTRIBUTE_SPEED) * this.getSpeedEffectModifier());*/
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
                .setSprintingAttribute(sprinting)
                .setTargetX(data.getServerX())
                .setTargetY(data.getServerY())
                .setTargetZ(data.getServerZ());

        /*
         * Provide the jump move and have the changes and shit for jumping ONLY if the attribute
         * has jumping activated :P. The jump modifies the velocity based on the yaw attribute of the player and
         * gives a pretty straight forward velocity. Surprisingly enough, it does NOT care about which keystrokes
         * you press when applying said velocity. It simply modifies it.
         */
        if (jumping) {
            input = jumpMoveProvider.provide(input);
        } else {
            attributeMap.get(EntityAttributes.JUMP_TICKS).set(0);
        }

        /*
         * Corresponds to moveEntityWithHeading in EntityLivingBase. This is the fundamental part which controls
         * the motion acceleration based on moveForward and moveStrafe
         */
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

        if (input.isSprintingAttribute()) {
            input.addTag(Tags.SPRINT_OBS);
        }

        final double deltaX = Math.abs(input.getTargetX() - input.getResult().getX());
        final double deltaY = Math.abs(input.getTargetY() - input.getResult().getY());
        final double deltaZ = Math.abs(input.getTargetZ() - input.getResult().getZ());

        return new BruteforceIteration(input, distance, deltaX, deltaY, deltaZ, length, postData);
    }

    public float getSpeedEffectModifier() {
        //final PotionEffect effect = data.getActivePotionsMap().getOrDefault(PotionEffectType.SPEED.getId(), null);
        final int level = getSpeedBoostAmplifier(data.getPlayer().getPlayer());

        return (float) (1.F + (float) level * (double) 0.2F); // This is how you do lossy conversion
    }

    protected int getSpeedBoostAmplifier(Player player) {
        if (player.hasEffect(PotionEffectType.SPEED)) {
            return player.getActivePotionEffects()
                    .stream()
                    .filter(effect -> effect.getType().equals(PotionEffectType.SPEED))
                    .findFirst()
                    .map(effect -> effect.getAmplifier() + 1)
                    .orElse(0);
        }
        return 0;
    }

    @Getter
    static class BruteforceIteration {
        private final TransitionData data;
        private final double distance;
        private final double distanceX;
        private final double distanceY;
        private final double distanceZ;
        private final double length;

        private final List<Consumer<ArtemisData>> confirmedDataChanges;

        public BruteforceIteration(TransitionData data, double distance, double distanceX, double distanceY, double distanceZ, double length, List<Consumer<ArtemisData>> confirmedDataChanges) {
            this.data = data;
            this.distance = distance;
            this.distanceX = distanceX;
            this.distanceY = distanceY;
            this.distanceZ = distanceZ;
            this.length = length;
            this.confirmedDataChanges = confirmedDataChanges;
        }

        public void addConfirmedChanges(final Collection<Consumer<ArtemisData>> changes) {
            confirmedDataChanges.addAll(changes);
        }
    }

    @Getter
    static class BruteForceKey {
        private final PlayerData data;

        private int forward, strafe;
        private boolean jump, sprint, using, attack, ground, velocity, world, missedFlying;

        public BruteForceKey(final PlayerData data) {
            this.data = data;
        }

        public BruteForceKey setForward(final int forward) {
            this.forward = forward;
            return this;
        }

        public BruteForceKey setStrafe(final int strafe) {
            this.strafe = strafe;
            return this;
        }

        public BruteForceKey setJump(final boolean jump) {
            this.jump = jump;
            return this;
        }

        public BruteForceKey setSprint(final boolean sprint) {
            this.sprint = sprint;
            return this;
        }

        public BruteForceKey setUsing(final boolean using) {
            this.using = using;
            return this;
        }

        public BruteForceKey setAttack(final boolean attack) {
            this.attack = attack;
            return this;
        }

        public BruteForceKey setGround(final boolean ground) {
            this.ground = ground;
            return this;
        }

        public BruteForceKey setVelocity(final boolean velocity) {
            this.velocity = velocity;
            return this;
        }

        public BruteForceKey setWorld(final boolean world) {
            this.world = world;
            return this;
        }

        public BruteForceKey setMissedFlying(final boolean missedFlying) {
            this.missedFlying = missedFlying;
            return this;
        }

        public BruteForceKey copy() {
            return new BruteForceKey(data)
                    .setForward(forward)
                    .setStrafe(strafe)
                    .setJump(jump)
                    .setSprint(sprint)
                    .setUsing(using)
                    .setAttack(attack)
                    .setGround(ground)
                    .setVelocity(velocity)
                    .setWorld(world)
                    .setMissedFlying(missedFlying);
        }
    }
}