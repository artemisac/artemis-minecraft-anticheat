package ac.artemis.core.v4.emulator.entity.utils;

import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v5.emulator.Emulator;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ghast
 * @since 17/10/2020
 * Artemis © 2020
 */

@Getter
@Setter
public class PlayerControls {
    /**
     * The speed at which the player is strafing. Postive numbers to the left and negative to the right.
     */
    public float moveStrafing;

    /**
     * The speed at which the player is moving forward. Negative numbers will move backwards.
     */
    public float moveForward;
    public boolean jumping;
    public boolean ladder;
    public boolean sprint;
    public boolean using;

    private final Emulator entity;

    public PlayerControls(Emulator entity) {
        this.entity = entity;
    }

    public void tick() {
        this.moveForward = 0.0F;
        this.moveStrafing = 0.0F;

        if (entity.getData().getMovement().getLocation() == null) {
            return;
        }
        final int[] vars = MoveUtil.getMoveForwardIteration(this.getEntity(), entity.getData().prediction.getLocation());
        int forward = vars[0];
        int strafe = vars[1];
        int jump = vars[2];
        int ladder = vars[3];
        int sprint = vars[4];
        int velocity = vars[5];
        int eating = vars[6];

        this.jumping = jump == 1;
        this.ladder = ladder == 1;
        this.sprint = sprint == 1;
        this.using = eating == 1;

        entity.setSprinting(this.isSprint());

        this.moveForward = forward;
        this.moveStrafing = strafe;

        if (entity.isSneaking()) {
            this.moveForward = (float)((double)this.moveForward * 0.3D);
            this.moveStrafing = (float)((double)this.moveStrafing * 0.3D);
        }

        if (velocity == 1) {
            Velocity v = entity.getData().prediction.getQueuedVelocity().pop();

            entity.setVelocity(v.getX(), v.getY(), v.getZ());
        }
    }
}
