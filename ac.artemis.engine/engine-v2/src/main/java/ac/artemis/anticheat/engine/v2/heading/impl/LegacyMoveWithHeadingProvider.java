package ac.artemis.anticheat.engine.v2.heading.impl;

import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.anticheat.engine.v2.flying.EntityFlyingFactory;
import ac.artemis.anticheat.engine.v2.flying.EntityFlyingProvider;
import ac.artemis.anticheat.engine.v2.heading.MoveWithHeadingProvider;
import ac.artemis.anticheat.engine.v2.move.EntityMoveProvider;
import ac.artemis.anticheat.engine.v2.move.EntityMoveProviderFactory;
import ac.artemis.core.v5.collision.BlockCollisionProvider;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.attributes.EntityAttributes;
import ac.artemis.core.v5.emulator.collision.CollisionProvider;
import ac.artemis.core.v5.emulator.collision.impl.LegacyBoundingBoxProvider;
import ac.artemis.core.v5.emulator.tags.Tags;
import ac.artemis.core.v5.emulator.utils.OutputAction;
import ac.artemis.core.v5.utils.EntityUtil;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.minecraft.MathHelper;
import ac.artemis.packet.protocol.ProtocolVersion;

/**
 * @author Ghast
 * @since 13/02/2021
 * Artemis © 2021
 */
public class LegacyMoveWithHeadingProvider implements MoveWithHeadingProvider {

    private static final EntityFlyingProvider entityFlyingProvider = new EntityFlyingFactory().build();
    private static final CollisionProvider collisionProvider = new LegacyBoundingBoxProvider();

    private final EntityMoveProvider entityMoveProvider;

    public LegacyMoveWithHeadingProvider(ProtocolVersion version) {
        this.entityMoveProvider = new EntityMoveProviderFactory()
                .setVersion(version)
                .build();
    }

    @Override
    public TransitionData moveWithHeading(TransitionData heading) {
        final boolean chunkLoaded = heading.poll(EntityAttributes.CHUNK_LOADED);
        final boolean water = heading.poll(EntityAttributes.WATER);
        final boolean lava = heading.poll(EntityAttributes.LAVA);
        final boolean flying = heading.poll(EntityAttributes.FLYING);
        final boolean ground = heading.poll(EntityAttributes.GROUND);
        final boolean sprint = heading.isSprintingAttribute();
        final boolean sneak = heading.poll(EntityAttributes.SNEAK);
        final boolean jump = heading.poll(EntityAttributes.JUMPING);
        final boolean ladder = heading.poll(EntityAttributes.LADDER);

        final double aiMoveSpeed = heading.poll(EntityAttributes.ATTRIBUTE_SPEED);

        final double fallDistance = heading.poll(EntityAttributes.FALL_DISTANCE);

        // If the chunk is loaded
        if (!chunkLoaded) {
            return heading;
        }

        if (water && !flying) {
            final double d0 = heading.getBoundingBox().minY;
            float frictionFactor = 0.8F;
            float drag = 0.02F;
            // Todo add compensation for enchants kekw
            float strider = (float) EntityUtil.getDepthStrider(heading.getData().getPlayer());

            if (strider > 3.0F) {
                strider = 3.0F;
            }

            if (!ground) {
                strider *= 0.5F;
            }

            if (strider > 0.0F) {
                frictionFactor += (0.54600006F - frictionFactor) * strider / 3.0F;
                drag += (aiMoveSpeed * 1.0F - drag) * strider / 3.0F;
            }

            heading.setFriction(drag);
            heading = entityFlyingProvider.provide(heading);
            heading = entityMoveProvider.provide((ArtemisData) heading.getData().getEntity(), heading);

            float finalFrictionFactor = frictionFactor;

            return heading.addAction(new OutputAction() {
                @Override
                public void accept(TransitionData output) {
                    output.setMotionX(output.getMotionX() * finalFrictionFactor);
                    output.setMotionY(output.getMotionY() * 0.800000011920929D);
                    output.setMotionZ(output.getMotionZ() * finalFrictionFactor);
                    output.setMotionY(output.getMotionY() - 0.02D);

                    if (output.isCollidedHorizontally()) {
                        final BoundingBox boundingBox = output.getBoundingBox().offset(
                                output.getMotionX(),
                                output.getMotionY() + 0.6000000238418579D - output.getY() + d0,
                                output.getMotionZ()
                        );

                        final boolean offset = collisionProvider
                                .getBoundingBoxes((ArtemisData) output.getData().getEntity(), boundingBox).isEmpty()
                                && BlockCollisionProvider.LIQUID_PROVIDER
                                .getCollidingBlocks(boundingBox, output.getData().getEntity()).isEmpty();

                        if (offset) {
                            output.setMotionY(0.30000001192092896D);
                        }
                    }
                }
            });
        }

        else if (lava && !flying) {
            // Move with a low friction
            final double d0 = heading.getBoundingBox().minY;
            heading.setFriction(0.02F);
            heading = entityFlyingProvider.provide(heading);
            heading = entityMoveProvider.provide((ArtemisData) heading.getData().getEntity(), heading);

            return heading.addAction(new OutputAction() {
                @Override
                public void accept(TransitionData output) {
                    output.setMotionX(output.getMotionX() * 0.5D);
                    output.setMotionY(output.getMotionY() * 0.5D);
                    output.setMotionZ(output.getMotionZ() * 0.5D);
                    output.setMotionY(output.getMotionY() - 0.02D);

                    if (output.isCollidedHorizontally()) {
                        final BoundingBox boundingBox = output.getBoundingBox().offset(
                                output.getMotionX(),
                                output.getMotionY() + 0.6000000238418579D - output.getY() + d0,
                                output.getMotionZ()
                        );

                        final boolean offset = collisionProvider.getBoundingBoxes((ArtemisData) output.getData().getEntity(), boundingBox).isEmpty()
                                && BlockCollisionProvider.LIQUID_PROVIDER
                                .getCollidingBlocks(boundingBox, output.getData().getEntity()).isEmpty();

                        if (offset) {
                            output.setMotionY(0.30000001192092896D);
                        }
                    }
                }
            });
        }

        else {
            // Grab the magic friction value, equivalent of 0.91F
            // These values are directly from NMS, quite useful most the time, these have to be improved nonetheless
            // Check if the user was on ground before as we're a tick behind since we're predicting the position
            final float frictionFactor = ground ? heading.getFrictionAtBB(1.D) * 0.91F : 0.91F;


            // This is the odd value "f" is in the formula.
            final float drag = 0.16277136F / (frictionFactor * frictionFactor * frictionFactor);

            final float acceleration;

            if (heading.isGround()) {
                acceleration = (float) (aiMoveSpeed * drag);
                heading.addTag(Tags.GROUND);
            } else {
                if (sprint) {
                    heading.addTag(Tags.SPRINT_HEAD);
                }
                acceleration = (float) (sprint ? ((double) 0.02F + (double) 0.02F * 0.3D) : 0.02F);
            }

            heading.setFriction(acceleration);

            // Move the entity's motion
            heading = entityFlyingProvider.provide(heading);

            heading.setFriction(frictionFactor);

            // This bit is entirely taken from NMS. It'll make the motionY of a data static if such user
            // Is colliding with a ladder.
            if (ladder) {
                final double f6 = (double) 0.15F;

                heading.setMotionX(MathHelper.clamp_double(heading.getMotionX(), -f6, f6));
                heading.setMotionZ(MathHelper.clamp_double(heading.getMotionZ(), -f6, f6));

                if (heading.getMotionY() < -0.15D) {
                    heading.setMotionY(-0.15D);
                }

                if (sneak && heading.getMotionY() < 0.0D) {
                    heading.setMotionY(0.0D);
                }
            }

            heading = entityMoveProvider.provide((ArtemisData) heading.getData().getEntity(), heading);

            // Moves the entity based on it's motion
            TransitionData finalHeading = heading;
            return heading.addAction(new OutputAction() {
                @Override
                public void accept(TransitionData output) {
                    // Debug purposes
                    //data.setMaxMotionX(motion.getX());
                    //data.setMaxMotionY(motion.getY());
                    //data.setMaxMotionZ(motion.getZ());

                    final double deltaY = output.getData().prediction.getDeltaY();

                    final boolean ladder = output.poll(EntityAttributes.LADDER);

                    if (ladder && output.isCollidedHorizontally()) {
                        //Bukkit.broadcastMessage("Collided, deltaY=" + deltaY);
                        output.setMotionY(0.2D);
                    } else if (ladder) {
                        //Bukkit.broadcastMessage("Not collided, deltaY=" + deltaY);
                    }

                    // Decrease the natural motion [Gravity]
                    output.setMotionY(output.getMotionY() - 0.08D);

                    // Natural update of motion for next position
                    output.setMotionX(output.getMotionX() * frictionFactor);
                    output.setMotionY(output.getMotionY() * 0.9800000190734863D);
                    output.setMotionZ(output.getMotionZ() * frictionFactor);
                }
            });
        }
    }

}
